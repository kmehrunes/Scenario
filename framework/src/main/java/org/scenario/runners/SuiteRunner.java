package org.scenario.runners;

import org.scenario.definitions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuiteRunner {

    public void run(final Suite suite) {
        final StepRunner stepRunner = new StepRunner(suite.hooks());
        final StepExecutor stepExecutor = new StepExecutor();
        final ScenarioRunner scenarioRunner = new ScenarioRunner(suite.hooks(), stepRunner);

        final HooksRunner hooksRunner = new HooksRunner(suite.hooks(), stepExecutor);

        final Map<String, Object> globals = new HashMap<>();

        final Failures beforeSuiteFailures = hooksRunner.run(Hooks.Scope.BEFORE_SUITE, suite,
                new ScenarioContext(globals), null, true);

        if (!beforeSuiteFailures.asList().isEmpty()) {
            return;
        }

        final List<Failure> failures = new ArrayList<>();

        suite.scenarios().forEach(scenario -> failures.addAll(scenarioRunner.run(scenario, globals).asList()));

        hooksRunner.run(Hooks.Scope.AFTER_SUITE, suite, new ScenarioContext(globals),
                new Failures(failures), false);
    }

}
