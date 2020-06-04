package org.scenario.runners;

import org.scenario.annotations.Name;
import org.scenario.definitions.ExecutableStep;
import org.scenario.definitions.ExecutionContext;
import org.scenario.definitions.Failure;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

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

        final Parameter param = method.getParameters()[paramIndex];
        final Class<?> paramType = method.getParameterTypes()[paramIndex];

        if (ResourceParams.isResource(param)) {
            return getFromResources(param, paramType);
        } else {
            return getFromContext(method, param, paramType, executionContext);
        }
    }

    private Object getFromResources(final Parameter param, final Class<?> paramType) {
        if (paramType == String.class) {
            return ResourceParams.readResource(param).asString();
        } else if (paramType == byte[].class) {
            return ResourceParams.readResource(param).asBytes();
        } else if (paramType == InputStream.class) {
            return ResourceParams.readResource(param).asInputStream();
        } else {
            throw new IllegalArgumentException("Resource parameter must be of type String, InputStream, or byte[]");
        }
    }

    private Object getFromContext(final Method method, final Parameter param, final Class<?> paramType,
                                  final ExecutionContext executionContext) {
        final Name name = param.getAnnotation(Name.class);

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
