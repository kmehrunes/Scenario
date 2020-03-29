package org.scenario.runners;

import org.scenario.annotations.Step;
import org.scenario.definitions.*;
import org.scenario.util.Output;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StepRunner {
    private final Hooks hooks;

    public StepRunner(final Hooks hooks) {
        this.hooks = hooks;
    }

    public List<Failure> runStep(final StepExecutor executor, final ExecutableStep step, final ScenarioContext context) {
        final List<Failure> beforeStepResults = runHooks(executor, Hooks.Scope.BEFORE_STEP, step, context,
                null, true);

        if (!beforeStepResults.isEmpty()) {
            return beforeStepResults;
        }

        Optional<Failure> failure = invokeStep(step, context);

        List<Failure> afterStepFailures = runHooks(executor, Hooks.Scope.AFTER_STEP, step, context, failure.orElse(null), false);

        return Stream.concat(afterStepFailures.stream(), failure.stream())
                .collect(Collectors.toList());
    }

    private List<Failure> runHooks(final StepExecutor executor, final Hooks.Scope scope,
                                   final ExecutableStep step, final ScenarioContext scenarioContext,
                                   final Failure stepFailure,
                                   final boolean abortOnFailure) {
        final List<Failure> failures = new ArrayList<>();

        for (final ExecutableStep beforeStep : hooks.executableSteps(scope)) {
            final ExecutionContext executionContext = new ExecutionContext.Builder()
                    .add(step)
                    .add(step.method())
                    .add(step.method().getAnnotation(Step.class))
                    .add(scenarioContext)
                    .add(stepFailure, Failure.class)
                    .build();

            final Optional<Failure> failure = executor.execute(beforeStep, executionContext);

            if (failure.isPresent()) {
                failures.add(failure.get());

                if (abortOnFailure) {
                    break;
                }
            }
        }

        return failures;
    }

    private Optional<Failure> invokeStep(final ExecutableStep step, final ScenarioContext context) {
        try {
            if (acceptsContext(step.method())) {
                step.method().invoke(step.instance(), context);
            } else {
                step.method().invoke(step.instance());
            }

            return Optional.empty();
        } catch (final Throwable e) {
            return Optional.of(new Failure(step, e));
        }
    }

    private boolean acceptsContext(final Method method) {
        return method.getParameterCount() == 1;
    }
}
