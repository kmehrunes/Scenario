package org.scenario.discovery;

import org.junit.jupiter.api.Test;
import org.scenario.annotations.Step;
import org.scenario.annotations.StepsContainer;
import org.scenario.definitions.Container;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StepsContainersDiscoveryTest {

    @StepsContainer("TestSteps")
    public static class TestSteps {
        @Step(name = "named")
        public void namedStep() { }

        @Step
        public void unnamedStep() { }
    }

    @Test
    void discover() throws NoSuchMethodException {
        final StepsContainersDiscovery discovery = new StepsContainersDiscovery();

        final List<Container> containers = discovery.discover(this.getClass().getPackageName());

        assertThat(containers).hasSize(1);
        assertThat(containers.get(0).name()).isEqualTo("TestSteps");

        assertThat(containers.get(0).findStep("named")).isPresent();
        assertThat(containers.get(0).findStep("named").get().method())
                .isEqualTo(TestSteps.class.getMethod("namedStep"));
        assertThat(containers.get(0).findStep("named").get().instance())
                .isNotNull();

        assertThat(containers.get(0).findStep("unnamedStep")).isPresent();
        assertThat(containers.get(0).findStep("unnamedStep").get().method())
                .isEqualTo(TestSteps.class.getMethod("unnamedStep"));
        assertThat(containers.get(0).findStep("unnamedStep").get().instance())
                .isNotNull();

        assertThat(containers.get(0).findStep("namedStep")).isEmpty();
    }
}