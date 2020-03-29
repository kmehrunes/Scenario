package org.scenario.runners;

import org.scenario.definitions.*;
import org.scenario.util.Output;

import java.util.*;
import java.util.stream.Collectors;

public class ScenarioRunner {
    private final StepRunner stepRunner;
    private final Hooks hooks;

    public ScenarioRunner(final Hooks hooks, final StepRunner stepRunner) {
        this.hooks = hooks;
        this.stepRunner = stepRunner;
    }

    public List<Failure> run(final Scenario scenario, final Map<String, Object> globals) {
        return run(scenario, new ScenarioContext(globals));
    }

    private List<Failure> run(final Scenario scenario, final ScenarioContext context) {
        final StepExecutor stepExecutor = new StepExecutor();
        final List<Failure> beforeScenarioResult = executeBeforeScenarioHooks(stepExecutor, scenario, context);

        if (!beforeScenarioResult.isEmpty()) {
            return beforeScenarioResult;
        }

        final List<Failure> flowResults = runFlow(stepExecutor, scenario.flow().steps(), context);

        executeAfterScenarioHooks(stepExecutor, scenario, context, flowResults);

        return flowResults;
    }

    private List<Failure> runFlow(final StepExecutor stepExecutor, final List<ExecutableStep> steps, final ScenarioContext context) {
        return steps.stream()
                .map(step -> stepRunner.runStep(stepExecutor, step, context))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<Failure> executeBeforeScenarioHooks(final StepExecutor executor, final Scenario scenario,
                                                     final ScenarioContext scenarioContext) {
        return executeHooks(executor, scenario, Hooks.Scope.BEFORE_SCENARIO, scenarioContext, null, true);
    }

    private void executeAfterScenarioHooks(final StepExecutor executor, final Scenario scenario,
                                           final ScenarioContext scenarioContext, final List<Failure> flowFailures) {
        executeHooks(executor, scenario, Hooks.Scope.AFTER_SCENARIO, scenarioContext, flowFailures, false);
    }

    private List<Failure> executeHooks(final StepExecutor executor, final Scenario scenario, final Hooks.Scope scope,
                                       final ScenarioContext scenarioContext, final List<Failure> flowFailures,
                                       boolean abortOnFailure) {
        final List<Failure> failures = new ArrayList<>();

        for (final ExecutableStep step : hooks.executableSteps(scope)) {
            final ExecutionContext executionContext = new ExecutionContext.Builder()
                    .add(scenario, Scenario.class)
                    .add(scenarioContext)
                    .add(flowFailures, List.class)
                    .build();

            final Optional<Failure> failure = executor.execute(step, executionContext);

            if (failure.isPresent()) {
                Output.error.println((step.description().isEmpty() ? step.name() : step.description())
                        + " Failed with exception " + failure.get().getCause());

                failure.get().getCause().printStackTrace();

                if (abortOnFailure) {
                    return Collections.singletonList(failure.get());
                } else {
                    failures.add(failure.get());
                }
            }
        }

        return failures;
    }
}
