package org.scenario.examples.defaultrunner;

import org.scenario.annotations.Step;
import org.scenario.definitions.Scenario;
import org.scenario.definitions.ScenarioFlow;

public class HeadstrongScenario extends Scenario {

    protected HeadstrongScenario() {
        super("Headstrong");
    }

    @Override
    public ScenarioFlow flow() {
        return new ScenarioFlow.Builder()
                .instance(this)
                .step("backOff")
                .step("headstrong")
                .step("youAreWrong")
                .step("whereYouBelong")
                .build();
    }

    @Step(description = "Back off, I'll take you on")
    public void backOff() {

    }

    @Step(description = "Headstrong to take on anyone")
    public void headstrong() {

    }

    @Step(description = "I know that you're wrong")
    public void youAreWrong() {

    }

    @Step(description = "And this is not where you belong")
    public void whereYouBelong() {
        throw new RuntimeException();
    }
}
