package org.scenario.definitions;

import java.util.ArrayList;
import java.util.List;

public final class Hooks {
    public enum Scope {
        BEFORE_SUITE,
        AFTER_SUITE,

        BEFORE_SCENARIO,
        AFTER_SCENARIO,

        BEFORE_STEP,
        AFTER_STEP
    }

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

    public List<ExecutableStep> executableSteps(final Hooks.Scope scope) {
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

        public Builder addHook(final Hooks.Scope scope, final ExecutableStep executableStep) {
            scopeList(scope).add(executableStep);
            return this;
        }

        public Hooks build() {
            return new Hooks(beforeSuite, afterSuite,
                    beforeScenario, afterScenario,
                    beforeStep, afterStep);
        }

        private List<ExecutableStep> scopeList(final Hooks.Scope scope) {
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
