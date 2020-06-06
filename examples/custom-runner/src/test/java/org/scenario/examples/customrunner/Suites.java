package org.scenario.examples.customrunner;

import org.scenario.definitions.Suite;
import org.scenario.runners.DefaultOutputHooks;

import java.util.Collections;
import java.util.List;

public class Suites {
    static List<Suite> get() {
        return Collections.singletonList(new Suite.Builder()
                .name("2000's rock and metal")
                .loadHooks(new DefaultOutputHooks())
                .loadScenarios(new HeadStrongScenario())
                .addToContext("baseUrl", "N/A")
                .build());
    }
}
