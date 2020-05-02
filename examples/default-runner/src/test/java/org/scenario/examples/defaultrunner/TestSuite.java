package org.scenario.examples.defaultrunner;

import org.scenario.annotations.SuiteDefinition;
import org.scenario.definitions.Suite;
import org.scenario.discovery.SuiteDiscovery;
import org.scenario.runners.DefaultOutputHooks;
import org.scenario.runners.SuiteRunner;

import java.util.List;

public class TestSuite {
    @SuiteDefinition
    public Suite exampleSuite() {
        return new Suite.Builder()
                .name("2000's rock and metal")
                .loadHooks(new DefaultOutputHooks())
                .loadScenarios(new HeadstrongScenario())
                .loadScenarios(new RandomScenarios())
                .addToContext("baseUrl", "N/A")
                .build();
    }
}
