package org.scenario.definitions;

import java.util.*;

/**
 * A steps container which contains steps not used directly
 * by a scenario but can be used in scenarios defined by
 * the DSL.
 *
 * @see {@link org.scenario.discovery.StepsContainersDiscovery}
 * @see {@link org.scenario.readers.DSLConverter}
 */
public class Container {
    private final String name;
    private final List<ExecutableStep> steps;
    private final Map<String, ExecutableStep> stepsMap;

    public Container(final String name, final List<ExecutableStep> steps) {
        this.name = name;
        this.steps = steps;
        this.stepsMap = map(steps);
    }

    public Optional<ExecutableStep> findStep(final String name) {
        return Optional.ofNullable(stepsMap.get(name));
    }

    private Map<String, ExecutableStep> map(final List<ExecutableStep> steps) {
        final Map<String, ExecutableStep> map = new HashMap<>();

        for (final ExecutableStep step : steps) {
            if (map.containsKey(step.name())) {
                throw new IllegalArgumentException("Found two steps with the same name " + step.name());
            }

            map.put(step.name(), step);
        }

        return map;
    }

    public String name() {
        return name;
    }

    public List<ExecutableStep> steps() {
        return steps;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final Container container = (Container) object;
        return name.equals(container.name) &&
                steps.equals(container.steps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, steps);
    }

    public static class Builder {
        private String name;
        private List<ExecutableStep> steps = new ArrayList<>();

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder steps(final List<ExecutableStep> steps) {
            this.steps = steps;
            return this;
        }

        public Builder addStep(final ExecutableStep step) {
            this.steps.add(step);
            return this;
        }

        public Container build() {
            return new Container(name, steps);
        }
    }
}
