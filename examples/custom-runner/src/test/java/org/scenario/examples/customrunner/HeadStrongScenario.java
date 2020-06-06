package org.scenario.examples.customrunner;

import org.scenario.annotations.ScenarioDefinition;
import org.scenario.annotations.Step;
import org.scenario.definitions.Scenario;
import org.scenario.definitions.ScenarioFlow;

public class HeadStrongScenario {
    @ScenarioDefinition
    public Scenario scenario() {
        return new Scenario.Builder()
                .name("Headstrong")
                .flow(new ScenarioFlow.Builder()
                        .instance(this)
                        .step("backOff")
                        .step("headstrong")
                        .step("youAreWrong")
                        .step("whereYouBelong")
                        .build())
                .build();
    }

    @Step(description = "Back off, I'll take you on")
    public void backOff() { }

    @Step(description = "Headstrong to take on anyone")
    public void headstrong() { }

    @Step(description = "I know that you're wrong")
    public void youAreWrong() { }

    @Step(description = "And this is not where you belong")
    public void whereYouBelong() {
        throw new IllegalStateException();
    }
}
