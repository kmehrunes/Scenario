package org.scenario.examples.customrunner;

import org.scenario.definitions.StepReport;
import org.scenario.definitions.Suite;
import org.scenario.runners.SuiteRunner;
import org.scenario.util.Output;

import java.util.List;
import java.util.stream.Collectors;

public class TestsMain {
    public static void main(String[] args) {
        final List<Suite> suites = Suites.get();
        final SuiteRunner runner = new SuiteRunner();

        final List<StepReport> stepReports = suites.stream()
                .map(runner::run)
                .flatMap(suiteFailures -> suiteFailures.asList().stream())
                .collect(Collectors.toList());

        if (!stepReports.isEmpty()) {
            Output.warn.println("Some tests have failed but we aren't going to fail the build for it");
        }
    }
}
