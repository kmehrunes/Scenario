package org.scenario.readers;

import org.junit.jupiter.api.Test;
import org.scenario.definitions.Suite;
import org.scenario.readers.model.DSLScenario;
import org.scenario.readers.model.DSLSuite;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class DSLConverterTest {


    @Test
    void convertSuiteAllMethodSteps() {
        final ScenarioTest testClass = new ScenarioTest();

        final DSLScenario dslScenario = new DSLScenario()
                .setName("Test scenario")
                .setClass(testClass.getClass().getSimpleName())
                .setFlow(Collections.singletonList("methodStep"));

        final DSLSuite dslSuite = new DSLSuite()
                .setName("Test suite")
                .setHooks(Collections.singletonList(testClass.getClass().getSimpleName()))
                .setScenarios("Test scenario", dslScenario);

        final DSLConverter converter = new DSLConverter(Collections.singletonList(this.getClass().getPackageName()));

        final Suite expected = new Suite.Builder()
                .name("Test suite")
                .loadScenarios(testClass)
                .loadHooks(testClass)
                .build();

        final Suite actual = converter.convertSuite(dslSuite);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void convertSuiteAllContainerSteps() {
        final ScenarioTest testClass = new ScenarioTest();

        final DSLScenario dslScenario = new DSLScenario()
                .setName("Test scenario")
                .setFlow(Collections.singletonList("Scenario::methodStep"));

        final DSLSuite dslSuite = new DSLSuite()
                .setName("Test suite")
                .setHooks(Collections.singletonList(testClass.getClass().getSimpleName()))
                .setScenarios("Test scenario", dslScenario);

        final DSLConverter converter = new DSLConverter(Collections.singletonList(this.getClass().getPackageName()));

        final Suite expected = new Suite.Builder()
                .name("Test suite")
                .loadScenarios(testClass)
                .loadHooks(testClass)
                .build();

        final Suite actual = converter.convertSuite(dslSuite);

        assertThat(actual).isEqualTo(expected);
    }
}