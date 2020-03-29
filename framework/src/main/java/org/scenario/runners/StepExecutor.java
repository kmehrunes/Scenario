package org.scenario.runners;

import org.scenario.definitions.*;

import java.util.Optional;

public class StepExecutor {
    public Optional<Failure> execute(final ExecutableStep executableStep, final ExecutionContext executionContext) {
        try {
            final Object[] args = prepareArgs(executableStep, executionContext);
            executableStep.execute(args);
            return Optional.empty();
        } catch (final Exception e) {
            return Optional.of(new Failure(executableStep, e));
        }
    }

    Object[] prepareArgs(final ExecutableStep executableStep, final ExecutionContext executionContext) {
        final Class<?>[] paramTypes = executableStep.method().getParameterTypes();
        final Object[] args = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            final Class<?> paramType = paramTypes[i];

            final Object arg = executionContext.getByClass(paramType)
                    .orElseThrow(() -> new IllegalArgumentException("Couldn't bind anything to parameter of type " + paramType
                            + " for method " + executableStep.method().getName()));

            if (arg instanceof Optional) {
                args[i] = ((Optional<?>) arg).orElse(null);
            } else {
                args[i] = arg;
            }
        }

        return args;
    }
}
