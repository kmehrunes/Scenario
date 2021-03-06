package org.scenario.runners;

import org.scenario.annotations.Step;
import org.scenario.annotations.Timeout;
import org.scenario.definitions.*;
import org.scenario.exceptions.TestFailuresExceptions;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Runs a single scenario step including its hooks. It
 * delegates the actual execution of the step to {@link StepExecutor}.
 */
class StepRunner {
    private final Hooks hooks;

    StepRunner(final Hooks hooks) {
        this.hooks = hooks;
    }

    Report runStep(final StepExecutor executor, final ExecutableStep step, final ScenarioContext scenarioContext) {
        return runStep(executor, step, scenarioContext, new ExecutionContext.Builder().build());
    }

    Report runStep(final StepExecutor executor, final ExecutableStep step,
                   final ScenarioContext scenarioContext, final ExecutionContext executionContext) {
        final HooksRunner hooksRunner = new HooksRunner(hooks, executor);
        final Report beforeStepResults = hooksRunner.run(MethodScope.BEFORE_STEP, prepareStepContext(step, scenarioContext, executionContext),
                null, true);

        if (beforeStepResults.containsFailures()) {
            return beforeStepResults;
        }

        final StepReport stepReport = invokeStep(executor, step, scenarioContext, executionContext);
        final Report report = new Report(Collections.singletonList(stepReport));

        final Report afterStepReport = hooksRunner.run(MethodScope.AFTER_STEP, prepareStepContext(step, scenarioContext, executionContext),
                report, true);

        return new Report(Stream.concat(afterStepReport.asList().stream(), Stream.of(stepReport))
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

    private StepReport invokeStep(final StepExecutor executor, final ExecutableStep step,
                                  final ScenarioContext scenarioContext, final ExecutionContext executionContext) {
        return doInvoke(executor, step, prepareStepContext(scenarioContext, executionContext));
    }

    private StepReport doInvoke(final StepExecutor executor, final ExecutableStep step, final ExecutionContext executionContext) {
        final Timeout timeout = step.method().getAnnotation(Timeout.class);

        if (timeout != null) {
            try {
                return CompletableFuture.supplyAsync(() -> executor.execute(step, executionContext))
                        .get(timeout.value(), timeout.unit());
            } catch (final InterruptedException | ExecutionException e) {
                throw new TestFailuresExceptions("Test was interrupted");
            } catch (final TimeoutException e) {
                return timeoutFailure(step, timeout);
            }
        } else {
            return executor.execute(step, executionContext);
        }
    }

    private StepReport timeoutFailure(final ExecutableStep step, final Timeout timeout) {
        final TimeoutException exception = new TimeoutException("Step took longer than " + timeout.value() + " " + timeout.unit());
        return StepReport.failure(step, exception);
    }
}
