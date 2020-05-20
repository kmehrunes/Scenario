package org.scenario.discovery;

import org.junit.jupiter.api.Test;
import org.scenario.annotations.ScenarioDefinition;
import org.scenario.definitions.Scenario;
import org.scenario.definitions.ScenarioFlow;
import org.scenario.exceptions.DefinitionException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScenarioDiscoveryTest {

    public static class ValidScenariosContainer {
        @ScenarioDefinition
        public Scenario scenario() {
            return new Scenario.Builder()
                    .name("scenario")
                    .description("scenario")
                    .flow(new ScenarioFlow.Builder().instance(this).build())
                    .build();
        }

        public Scenario notAScenario() {
            return null;
        }
    }

    public static class InvalidScenariosContainer {
        @ScenarioDefinition
        public String scenario() {
            return "this should fail";
        }
    }

    @Test
    void discoverValidScenarios() {
        final ScenarioDiscovery discovery = new ScenarioDiscovery();
        final ValidScenariosContainer container = new ValidScenariosContainer();

        final List<Scenario> scenarios = discovery.discover(container);

        final Scenario expectedScenario = new Scenario.Builder()
                .name("scenario")
                .description("scenario")
                .flow(new ScenarioFlow.Builder().instance(container).build())
                .build();

        assertThat(scenarios).containsExactly(expectedScenario);
    }

    @Test
    void discoverInvalidScenarios() {
        final ScenarioDiscovery discovery = new ScenarioDiscovery();
        final InvalidScenariosContainer container = new InvalidScenariosContainer();

        assertThatThrownBy(() -> discovery.discover(container)).isInstanceOf(DefinitionException.class);
    }
}