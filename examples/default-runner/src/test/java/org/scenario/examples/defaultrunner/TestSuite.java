package org.scenario.examples.defaultrunner;

import org.scenario.annotations.SuiteDefinition;
import org.scenario.definitions.Suite;
import org.scenario.runners.DefaultOutputHooks;

public class TestSuite {
    @SuiteDefinition
    public Suite exampleSuite() {
        return new Suite.Builder()
                .name("2000's rock and metal")
                .loadHooks(new DefaultOutputHooks())
                .loadScenarios(new HeadstrongScenario())
                .build();
    }
}
