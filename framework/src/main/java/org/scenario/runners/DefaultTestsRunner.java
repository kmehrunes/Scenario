package org.scenario.runners;

import org.scenario.definitions.Failure;
import org.scenario.definitions.Suite;
import org.scenario.discovery.SuiteDiscovery;
import org.scenario.exceptions.TestFailuresExceptions;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultTestsRunner {
    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalStateException("DefaultRunner requires at least one package to scan");
        }

        final SuiteDiscovery discovery = new SuiteDiscovery();

        final List<Suite> suites = discovery.discover(args);

        final SuiteRunner runner = new SuiteRunner();

        final List<Failure> failures = suites.stream()
                .map(runner::run)
                .flatMap(suiteFailures -> suiteFailures.asList().stream())
                .collect(Collectors.toList());

        if (!failures.isEmpty()) {
            throw new TestFailuresExceptions("There are test failures");
        }
    }
}
