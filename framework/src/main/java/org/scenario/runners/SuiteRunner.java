package org.scenario.runners;

import org.scenario.definitions.*;
import org.scenario.util.Output;

import java.util.*;

public class SuiteRunner {

    public void run(final Suite suite) {
        final StepRunner stepRunner = new StepRunner(suite.hooks());
        final StepExecutor stepExecutor = new StepExecutor();
        final ScenarioRunner scenarioRunner = new ScenarioRunner(suite.hooks(), stepRunner);

        final Map<String, Object> globals = new HashMap<>();
        final List<Failure> failures = new ArrayList<>();

        if (!executeBeforeSuiteHooks(stepExecutor, suite, globals).isEmpty()) {
            Output.error.println("An error happened during suite setup steps. The suite will be ignored");
            return;
        }

        suite.scenarios().forEach(scenario -> failures.addAll(scenarioRunner.run(scenario, globals)));

        executeAfterSuiteHooks(stepExecutor, suite, globals, failures);
    }

    private List<Failure> executeBeforeSuiteHooks(final StepExecutor executor, final Suite suite, final Map<String, Object> globals) {
        return executeHooks(executor, suite, Hooks.Scope.BEFORE_SUITE, globals, null, true);
    }

    private void executeAfterSuiteHooks(final StepExecutor executor, final Suite suite, final Map<String, Object> globals,
                                        final List<Failure> scenariosFailures) {
        executeHooks(executor, suite, Hooks.Scope.AFTER_SUITE, globals, scenariosFailures, false);
    }

    private List<Failure> executeHooks(final StepExecutor executor, final Suite suite, final Hooks.Scope scope,
                                       final Map<String, Object> globals, final List<Failure> scenariosFailures,
                                       boolean abortOnFailure) {
        final List<Failure> failures = new ArrayList<>();

        for (final ExecutableStep step : suite.hooks().executableSteps(scope)) {
            final ExecutionContext executionContext = new ExecutionContext.Builder()
                    .add(suite)
                    .add(new ScenarioContext(globals))
                    .add(scenariosFailures, List.class)
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
