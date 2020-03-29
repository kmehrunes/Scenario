package org.scenario.definitions;

public abstract class Scenario {
    private final String name;

    protected Scenario(final String name) {
        this.name = name;
    }

    public abstract ScenarioFlow flow();

    public String name() {
        return name;
    }
}
