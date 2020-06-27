package org.scenario.definitions;


import org.scenario.annotations.Step;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScenarioFlow {
    private final List<ExecutableStep> steps;
    private final Object instance;

    private ScenarioFlow(final Object instance, final List<String> stepsNames) {
        Objects.requireNonNull(instance, "Flow instance cannot be null");
        Objects.requireNonNull(stepsNames, "Flow steps cannot be null");

        this.instance = instance;
        this.steps = stepsNames.stream()
                .map(name -> step(instance, name))
                .collect(Collectors.toList());
    }

    public List<ExecutableStep> steps() {
        return steps;
    }

    private ExecutableStep step(final Object instance, final String name) {
        final Method method = stepMethod(name);
        final Step annotation = method.getAnnotation(Step.class);

        return new ExecutableStep(name, annotation.description(), method, instance, MethodScope.FLOW);
    }

    private Method stepMethod(final String name) {
        return Stream.of(instance.getClass().getMethods())
                .filter(method -> method.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Class " + instance.getClass().getSimpleName() +
                        " doesn't contain method " + name));

    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final ScenarioFlow that = (ScenarioFlow) object;
        return steps.equals(that.steps) &&
                instance.equals(that.instance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(steps, instance);
    }

    public static class Builder {
        private List<String> steps = new ArrayList<>();
        private Object instance;

        public Builder instance(final Object instance) {
            this.instance = instance;
            return this;
        }

        public Builder step(final String name) {
            steps.add(name);
            return this;
        }

        public ScenarioFlow build() {
            return new ScenarioFlow(instance, steps);
        }
    }
}

