package org.scenario.runners;

import org.scenario.definitions.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        final Report beforeScenarioResult = hooksRunner.run(Hooks.Scope.BEFORE_SCENARIO, scenario,
                scenarioContext, executionContext, true);

        if (beforeScenarioResult.containsFailures()) {
            return beforeScenarioResult;
        }

        final Report flowResults = runFlow(stepExecutor, scenario.flow().steps(), scenarioContext, executionContext);

        hooksRunner.run(Hooks.Scope.AFTER_SCENARIO, scenario, scenarioContext, executionContext, false);

        return flowResults;
    }

    private Report runFlow(final StepExecutor stepExecutor, final List<ExecutableStep> steps,
                           final ScenarioContext scenarioContext, final ExecutionContext executionContext) {
        return new Report(steps.stream()
                .map(step -> stepRunner.runStep(stepExecutor, step, scenarioContext, executionContext))
                .map(Report::asList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
    }
}
