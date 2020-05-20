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

    Failures run(final Scenario scenario, final Map<String, Object> globals, final ExecutionContext executionContext) {
        return run(scenario, new ScenarioContext(globals), executionContext);
    }

    private Failures run(final Scenario scenario, final ScenarioContext scenarioContext,
                         final ExecutionContext executionContext) {
        final StepExecutor stepExecutor = new StepExecutor();
        final HooksRunner hooksRunner = new HooksRunner(hooks, stepExecutor);

        final Failures beforeScenarioResult = hooksRunner.run(Hooks.Scope.BEFORE_SCENARIO, scenario,
                scenarioContext, executionContext, true);

        if (!beforeScenarioResult.asList().isEmpty()) {
            return beforeScenarioResult;
        }

        final Failures flowResults = runFlow(stepExecutor, scenario.flow().steps(), scenarioContext, executionContext);

        hooksRunner.run(Hooks.Scope.AFTER_SCENARIO, scenario, scenarioContext, executionContext, false);

        return flowResults;
    }

    private Failures runFlow(final StepExecutor stepExecutor, final List<ExecutableStep> steps,
                             final ScenarioContext scenarioContext, final ExecutionContext executionContext) {
        return new Failures(steps.stream()
                .map(step -> stepRunner.runStep(stepExecutor, step, scenarioContext, executionContext))
                .map(Failures::asList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
    }
}
