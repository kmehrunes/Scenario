package org.scenario.runners;

import org.scenario.definitions.Suite;
import org.scenario.discovery.SuiteDiscovery;

import java.util.List;

public class DefaultTestsRunner {
    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalStateException("DefaultRunner requires at least one package to scan");
        }

        final SuiteDiscovery discovery = new SuiteDiscovery();

        final List<Suite> suites = discovery.discover(args);

        final SuiteRunner runner = new SuiteRunner();

        suites.forEach(runner::run);
    }
}
