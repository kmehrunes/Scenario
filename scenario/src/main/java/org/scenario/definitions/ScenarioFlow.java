package org.scenario.definitions;


import org.scenario.annotations.Step;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An ordered sequence of the steps of a scenario.
 */
public class ScenarioFlow {
    private final List<ExecutableStep> steps;

    private ScenarioFlow(final List<ExecutableStep> steps) {
        Objects.requireNonNull(steps, "Flow steps cannot be null");

        this.steps = steps;
    }

    public List<ExecutableStep> steps() {
        return steps;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final ScenarioFlow that = (ScenarioFlow) object;
        return steps.equals(that.steps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(steps);
    }

    public static class Builder {
        private List<String> stepsNames = new ArrayList<>();
        private List<ExecutableStep> executableSteps = new ArrayList<>();
        private Object instance;

        public Builder instance(final Object instance) {
            this.instance = instance;
            return this;
        }

        /**
         * Add a step by name. This can only be used if an
         * instance is also specified. The an executable step
         * will be created for a method with that name in the
         * instance.
         */
        public Builder step(final String name) {
            stepsNames.add(name);
            return this;
        }

        /**
         * Add an executable step directly. An instance doesn't
         * have to be used for this and there is no restriction
         * on the object it can use.
         */
        public Builder step(final ExecutableStep step) {
            executableSteps.add(step);
            return this;
        }

        public ScenarioFlow build() {
            final Stream<ExecutableStep> transformedSteps = stepsNames.stream()
                    .map(stepName -> executableStep(instance, stepName));

            final List<ExecutableStep> combinedSteps = Stream.concat(executableSteps.stream(), transformedSteps)
                    .collect(Collectors.toList());

            return new ScenarioFlow(combinedSteps);
        }

        private ExecutableStep executableStep(final Object instance, final String name) {
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
    }
}

