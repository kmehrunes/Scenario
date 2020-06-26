package org.scenario.runners;

import org.scenario.definitions.*;
import org.scenario.util.Output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class HooksRunner {
    private final Hooks hooks;
    private final StepExecutor executor;

    HooksRunner(final Hooks hooks, final StepExecutor executor) {
        this.hooks = hooks;
        this.executor = executor;
    }

    Report run(final Hooks.Scope scope, final Scenario scenario, final ScenarioContext scenarioContext,
               final ExecutionContext executionContext, boolean abortOnFailure) {
        final ExecutionContext.Builder executionContextBuilder = executionContext.toBuilder()
                .add(scenario, Scenario.class)
                .add(scenarioContext);

        return run(scope, executionContextBuilder, null, abortOnFailure);
    }

    Report run(final Hooks.Scope scope, final Suite suite, final ScenarioContext scenarioContext,
               final Report previousReport, boolean abortOnFailure) {

        final ExecutionContext.Builder executionContextBuilder = suite.executionContext().toBuilder()
                .add(suite, Suite.class)
                .add(scenarioContext);

        return run(scope, executionContextBuilder, previousReport, abortOnFailure);
    }

    Report run(final Hooks.Scope scope, final ExecutionContext.Builder contextBuilder,
               final Report previousReport, boolean abortOnFailure) {
        final List<StepReport> hooksStepReports = new ArrayList<>();

        for (final ExecutableStep step : hooks.executableSteps(scope)) {
            final ExecutionContext executionContext = contextBuilder
                    .add(previousReport, Report.class)
                    .build();

            final StepReport stepReport = executor.execute(step, executionContext);

            if (stepReport.failed()) {
                Output.error.println((step.description().isEmpty() ? step.name() : step.description())
                        + " Failed with exception " + stepReport.getFailureCause());

                stepReport.getFailureCause().printStackTrace();

                if (abortOnFailure) {
                    return new Report(Collections.singletonList(stepReport));
                } else {
                    hooksStepReports.add(stepReport);
                }
            }
        }

        return new Report(hooksStepReports);
    }
}
