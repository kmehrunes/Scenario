package org.scenario.definitions;

public final class Scenario {
    private final String name;
    private final String description;
    private final ScenarioFlow flow;

    public Scenario(final String name, final String description, final ScenarioFlow flow) {
        this.name = name;
        this.description = description;
        this.flow = flow;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public ScenarioFlow flow() {
        return flow;
    }

    public static class Builder {
        private String name;
        private String description;
        private ScenarioFlow flow;

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        public Builder flow(final ScenarioFlow flow) {
            this.flow = flow;
            return this;
        }

        public Scenario build() {
            return new Scenario(name, description, flow);
        }
    }
}
