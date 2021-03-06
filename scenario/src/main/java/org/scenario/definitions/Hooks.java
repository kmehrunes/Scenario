package org.scenario.definitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A container for all hooks of a suite. An instance
 * of this class is created only once when the suite
 * is created and cannot be updated afterwards.
 */
public final class Hooks {

    private final List<ExecutableStep> beforeSuite;
    private final List<ExecutableStep> afterSuite;

    private final List<ExecutableStep> beforeScenario;
    private final List<ExecutableStep> afterScenario;

    private final List<ExecutableStep> beforeStep;
    private final List<ExecutableStep> afterStep;

    private Hooks(final List<ExecutableStep> beforeSuite,
                 final List<ExecutableStep> afterSuite,
                 final List<ExecutableStep> beforeScenario,
                 final List<ExecutableStep> afterScenario,
                 final List<ExecutableStep> beforeStep,
                 final List<ExecutableStep> afterStep) {
        this.beforeSuite = beforeSuite;
        this.afterSuite = afterSuite;
        this.beforeScenario = beforeScenario;
        this.afterScenario = afterScenario;
        this.beforeStep = beforeStep;
        this.afterStep = afterStep;
    }

    public static Hooks empty() {
        return new Hooks(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    public List<ExecutableStep> executableSteps(final MethodScope scope) {
        switch (scope) {
            case BEFORE_SUITE: return beforeSuite;
            case AFTER_SUITE: return afterSuite;

            case BEFORE_SCENARIO: return beforeScenario;
            case AFTER_SCENARIO: return afterScenario;

            case BEFORE_STEP: return beforeStep;
            case AFTER_STEP: return afterStep;

            default: throw new IllegalStateException();
        }
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final Hooks hooks = (Hooks) object;
        return Objects.equals(beforeSuite, hooks.beforeSuite) &&
                Objects.equals(afterSuite, hooks.afterSuite) &&
                Objects.equals(beforeScenario, hooks.beforeScenario) &&
                Objects.equals(afterScenario, hooks.afterScenario) &&
                Objects.equals(beforeStep, hooks.beforeStep) &&
                Objects.equals(afterStep, hooks.afterStep);
    }

    public static class Builder {
        private final List<ExecutableStep> beforeSuite;
        private final List<ExecutableStep> afterSuite;

        private final List<ExecutableStep> beforeScenario;
        private final List<ExecutableStep> afterScenario;

        private final List<ExecutableStep> beforeStep;
        private final List<ExecutableStep> afterStep;

        public Builder() {
            this.beforeSuite = new ArrayList<>();
            this.afterSuite = new ArrayList<>();
            this.beforeScenario = new ArrayList<>();
            this.afterScenario = new ArrayList<>();
            this.beforeStep = new ArrayList<>();
            this.afterStep = new ArrayList<>();
        }

        public Builder addHook(final MethodScope scope, final ExecutableStep executableStep) {
            scopeList(scope).add(executableStep);
            return this;
        }

        public Hooks build() {
            return new Hooks(beforeSuite, afterSuite,
                    beforeScenario, afterScenario,
                    beforeStep, afterStep);
        }

        private List<ExecutableStep> scopeList(final MethodScope scope) {
            switch (scope) {
                case BEFORE_SUITE: return beforeSuite;
                case AFTER_SUITE: return afterSuite;

                case BEFORE_SCENARIO: return beforeScenario;
                case AFTER_SCENARIO: return afterScenario;

                case BEFORE_STEP: return beforeStep;
                case AFTER_STEP: return afterStep;

                default: throw new IllegalStateException();
            }
        }
    }
}
