package org.scenario.runners;

import org.scenario.definitions.Report;
import org.scenario.definitions.StepReport;
import org.scenario.definitions.Suite;
import org.scenario.discovery.SuiteDiscovery;
import org.scenario.exceptions.TestFailuresExceptions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A runner to be used if no custom runner is given. It
 * requires a list of packages to be passed to it. Suites
 * defined within the packages will be discovered by
 * {@link SuiteDiscovery} and run. It will throw a
 * {@link TestFailuresExceptions} if any suite finished
 * with any failure.
 */
public class DefaultTestsRunner {
    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalStateException("DefaultRunner requires at least one package to scan");
        }

        final SuiteDiscovery discovery = new SuiteDiscovery();

        final List<Suite> suites = discovery.discover(args);

        final SuiteRunner runner = new SuiteRunner();

        final List<StepReport> stepReports = suites.stream()
                .map(runner::run)
                .flatMap(suiteFailures -> suiteFailures.asList().stream())
                .collect(Collectors.toList());

        final Report report = new Report(stepReports);

        if (report.containsFailures()) {
            throw new TestFailuresExceptions("There are test failures");
        }
    }
}
