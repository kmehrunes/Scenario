package org.scenario.runners;

import org.scenario.definitions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Runs a scenario including its hooks.
 */
class ScenarioRunner {
    private final StepRunner stepRunner;
    private final Hooks hooks;

    ScenarioRunner(final Hooks hooks, final StepRunner stepRunner) {
        this.hooks = hooks;
        this.stepRunner = stepRunner;
    }

    Report run(final Scenario scenario, final Map<String, Object> globals, final ExecutionContext executionContext) {
        return run(scenario, new ScenarioContext(globals), executionContext);
    }

    private Report run(final Scenario scenario, final ScenarioContext scenarioContext,
                       final ExecutionContext executionContext) {
        final StepExecutor stepExecutor = new StepExecutor();
        final HooksRunner hooksRunner = new HooksRunner(hooks, stepExecutor);

        final Report beforeScenarioResult = hooksRunner.run(MethodScope.BEFORE_SCENARIO, scenario,
                scenarioContext, executionContext, true);

        if (beforeScenarioResult.containsFailures()) {
            return beforeScenarioResult;
        }

        final Report flowResults = runFlow(stepExecutor, scenario.flow().steps(), scenarioContext, executionContext);

        hooksRunner.run(MethodScope.AFTER_SCENARIO, scenario, scenarioContext, executionContext, false);

        return flowResults;
    }

    private Report runFlow(final StepExecutor stepExecutor, final List<ExecutableStep> steps,
                           final ScenarioContext scenarioContext, final ExecutionContext executionContext) {
        final List<StepReport> stepReports = new ArrayList<>();

        for (final ExecutableStep step : steps) {
            final Report report = stepRunner.runStep(stepExecutor, step, scenarioContext, executionContext);

            stepReports.addAll(report.asList());

            final boolean containsCircuitBreaker = report.failedSteps().stream()
                    .filter(StepReport::failed)
                    .filter(stepReport -> stepReport.getStep().scope() == MethodScope.FLOW)
                    .anyMatch(stepReport -> stepReport.getStep().breaksCircuit());

            if (containsCircuitBreaker) {
                break;
            }
        }

        return new Report(stepReports);
    }
}
