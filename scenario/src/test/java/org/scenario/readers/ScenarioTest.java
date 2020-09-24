package org.scenario.readers;

import org.scenario.annotations.BeforeStep;
import org.scenario.annotations.ScenarioDefinition;
import org.scenario.annotations.Step;
import org.scenario.annotations.StepsContainer;
import org.scenario.definitions.Scenario;
import org.scenario.definitions.ScenarioFlow;

@StepsContainer("Scenario")
public class ScenarioTest {
    @ScenarioDefinition
    public Scenario scenario() {
        return new Scenario.Builder()
                .name("Test scenario")
                .flow(new ScenarioFlow.Builder()
                        .instance(this)
                        .step("methodStep")
                        .build()
                )
                .build();
    }

    @Step
    public void methodStep() {

    }

    @BeforeStep
    public void beforeStep() {

    }

    @Override
    public boolean equals(final Object object) {
        return object != null && object.getClass() == this.getClass();
    }
}
