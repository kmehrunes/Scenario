package org.scenario.definitions;

import java.util.Objects;

public final class Scenario {
    private final String name;
    private final String description;
    private final ScenarioFlow flow;

    public Scenario(final String name, final String description, final ScenarioFlow flow) {
        Objects.requireNonNull(name, "Scenario name cannot be null");
        Objects.requireNonNull(flow, "Scenario flow cannot be null");

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

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final Scenario scenario = (Scenario) object;
        return name.equals(scenario.name) &&
                Objects.equals(description, scenario.description) &&
                flow.equals(scenario.flow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, flow);
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
