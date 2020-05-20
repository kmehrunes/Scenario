package org.scenario.runners;

import org.scenario.annotations.Step;
import org.scenario.annotations.Timeout;
import org.scenario.definitions.*;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class StepRunner {
    private final Hooks hooks;

    StepRunner(final Hooks hooks) {
        this.hooks = hooks;
    }

    Failures runStep(final StepExecutor executor, final ExecutableStep step, final ScenarioContext scenarioContext) {
        return runStep(executor, step, scenarioContext, new ExecutionContext.Builder().build());
    }

    Failures runStep(final StepExecutor executor, final ExecutableStep step,
                     final ScenarioContext scenarioContext, final ExecutionContext executionContext) {
        final HooksRunner hooksRunner = new HooksRunner(hooks, executor);
        final Failures beforeStepResults = hooksRunner.run(Hooks.Scope.BEFORE_STEP, prepareStepContext(step, scenarioContext, executionContext),
                null, true);

        if (!beforeStepResults.asList().isEmpty()) {
            return beforeStepResults;
        }

        Optional<Failure> failure = invokeStep(executor, step, scenarioContext, executionContext);

        Failures afterStepFailures = hooksRunner.run(Hooks.Scope.AFTER_STEP, prepareStepContext(step, scenarioContext, executionContext),
                failure.map(Collections::singletonList).map(Failures::new).orElse(null), true);

        return new Failures(Stream.concat(afterStepFailures.asList().stream(), failure.stream())
                .collect(Collectors.toList()));
    }

    private ExecutionContext.Builder prepareStepContext(final ExecutableStep step, final ScenarioContext scenarioContext,
                                                        final ExecutionContext executionContext) {
        return executionContext.toBuilder()
                .add(step)
                .add(step.method())
                .add(step.method().getAnnotation(Step.class))
                .add(scenarioContext);
    }

    private ExecutionContext prepareStepContext(final ScenarioContext scenarioContext, final ExecutionContext executionContext) {
        return executionContext.toBuilder()
                .add(scenarioContext)
                .build();
    }

    private Optional<Failure> invokeStep(final StepExecutor executor, final ExecutableStep step,
                                         final ScenarioContext scenarioContext, final ExecutionContext executionContext) {
        return doInvoke(executor, step, prepareStepContext(scenarioContext, executionContext));
    }

    private Optional<Failure> doInvoke(final StepExecutor executor, final ExecutableStep step,
                                       final ExecutionContext executionContext) {
        final Timeout timeout = step.method().getAnnotation(Timeout.class);

        if (timeout != null) {
            return CompletableFuture.supplyAsync(() -> executor.execute(step, executionContext))
                    .completeOnTimeout(Optional.of(timeoutFailure(step, timeout)), timeout.value(), timeout.unit())
                    .join();
        } else {
            return executor.execute(step, executionContext);
        }
    }

    private Failure timeoutFailure(final ExecutableStep step, final Timeout timeout) {
        return new Failure(step, new TimeoutException("Step took longer than " + timeout.value() + " " + timeout.unit()));
    }
}
