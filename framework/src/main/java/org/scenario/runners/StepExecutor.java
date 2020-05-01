package org.scenario.runners;

import org.scenario.annotations.Name;
import org.scenario.definitions.ExecutableStep;
import org.scenario.definitions.ExecutionContext;
import org.scenario.definitions.Failure;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

class StepExecutor {
    Optional<Failure> execute(final ExecutableStep executableStep, final ExecutionContext executionContext) {
        try {
            final Object[] args = prepareArgs(executableStep, executionContext);
            executableStep.execute(args);
            return Optional.empty();
        } catch (final InvocationTargetException e) {
            return Optional.of(new Failure(executableStep, e.getCause()));
        } catch (final Throwable e) {
            return Optional.of(new Failure(executableStep, e));
        }
    }

    Object[] prepareArgs(final ExecutableStep executableStep, final ExecutionContext executionContext) {
        final Object[] args = new Object[executableStep.method().getParameterCount()];

        for (int i = 0; i < args.length; i++) {
            final Object arg = getArgOrFail(executableStep.method(), i, executionContext);

            if (arg instanceof Optional) {
                args[i] = ((Optional<?>) arg).orElse(null);
            } else {
                args[i] = arg;
            }
        }

        return args;
    }

    private Object getArgOrFail(final Method method, final int paramIndex, final ExecutionContext executionContext) {
        if (paramIndex < 0 || paramIndex >= method.getParameterCount()) {
            throw new IllegalArgumentException("Parameter index for method " + method.getName() +
                    " has to be between 0 and" + method.getParameterCount());
        }

        final Class<?> paramType = method.getParameterTypes()[paramIndex];

        final Name name = Stream.of(method.getParameterAnnotations()[paramIndex])
                .filter(annotation -> annotation.annotationType().equals(Name.class))
                .map(annotation -> (Name) annotation)
                .findFirst()
                .orElse(null);

        if (name == null) {
            return executionContext.getByClass(paramType)
                    .orElseThrow(() -> new IllegalArgumentException("Couldn't bind anything to parameter of type " + paramType
                            + " for method " + method.getName()));
        } else {
            return executionContext.getByName(name.value())
                    .orElseThrow(() -> new IllegalArgumentException("Couldn't bind anything with a name " + name.value()
                            + " for method " + method.getName()));
        }
    }
}
