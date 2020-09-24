package org.scenario.runners;

import org.scenario.definitions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Runs a suite, including its hooks and scenarios.
 */
public class SuiteRunner {

    public Report run(final Suite suite) {
        final StepRunner stepRunner = new StepRunner(suite.hooks());
        final StepExecutor stepExecutor = new StepExecutor();
        final ScenarioRunner scenarioRunner = new ScenarioRunner(suite.hooks(), stepRunner);

        final HooksRunner hooksRunner = new HooksRunner(suite.hooks(), stepExecutor);

        final Map<String, Object> globals = new HashMap<>();

        final Report beforeSuiteReport = hooksRunner.run(MethodScope.BEFORE_SUITE, suite,
                new ScenarioContext(globals), null, true);

        if (beforeSuiteReport.containsFailures()) {
            return beforeSuiteReport;
        }

        final List<StepReport> stepReports = new ArrayList<>();

        suite.scenarios()
                .stream()
                .map(scenario -> scenarioRunner.run(scenario, globals, suite.executionContext()))
                .map(Report::asList)
                .forEach(stepReports::addAll);

        final Report afterSuiteReport = hooksRunner.run(MethodScope.AFTER_SUITE, suite, new ScenarioContext(globals),
                new Report(stepReports), false);

        return new Report(Stream.concat(stepReports.stream(), afterSuiteReport.asList().stream())
                .collect(Collectors.toList()));
    }

}
