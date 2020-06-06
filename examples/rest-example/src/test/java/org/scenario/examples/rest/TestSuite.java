package org.scenario.examples.rest;

import org.scenario.annotations.SuiteDefinition;
import org.scenario.definitions.Suite;
import org.scenario.runners.DefaultOutputHooks;

public class TestSuite {
    @SuiteDefinition
    public Suite exampleSuite() {
        return new Suite.Builder()
                .name("WireMock with ReST Assured")
                .loadHooks(new DefaultOutputHooks())
                .loadHooks(new WireMockHooks(false))
                .loadScenarios(new UserScenarios())
                .build();
    }
}
