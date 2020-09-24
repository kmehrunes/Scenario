package org.scenario.readers;

import org.junit.jupiter.api.Test;
import org.scenario.definitions.Container;
import org.scenario.definitions.Suite;
import org.scenario.discovery.StepsContainersDiscovery;
import org.scenario.readers.model.DSLScenario;
import org.scenario.readers.model.DSLSuite;
import org.scenario.util.Packages;

import java.util.Collections;
import java.util.List;

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

        final List<Container> containers = new StepsContainersDiscovery().discover(this.getClass().getPackage().getName());
        final List<Class<?>> classes = Packages.findClasses(this.getClass().getPackage().getName());

        final DSLConverter converter = new DSLConverter(classes, containers);

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

        final List<Container> containers = new StepsContainersDiscovery().discover(this.getClass().getPackage().getName());
        final List<Class<?>> classes = Packages.findClasses(this.getClass().getPackage().getName());

        final DSLConverter converter = new DSLConverter(classes, containers);

        final Suite expected = new Suite.Builder()
                .name("Test suite")
                .loadScenarios(testClass)
                .loadHooks(testClass)
                .build();

        final Suite actual = converter.convertSuite(dslSuite);

        assertThat(actual).isEqualTo(expected);
    }
}