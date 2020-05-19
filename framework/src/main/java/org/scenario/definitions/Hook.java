package org.scenario.definitions;

import java.util.Objects;

public class Hook {
    private final Hooks.Scope scope;
    private final ExecutableStep executableStep;

    public Hook(final Hooks.Scope scope, final ExecutableStep executableStep) {
        Objects.requireNonNull(scope, "Hook scope cannot be null");
        Objects.requireNonNull(executableStep, "Hook executable step cannot be null");

        this.scope = scope;
        this.executableStep = executableStep;
    }

    public Hooks.Scope scope() {
        return scope;
    }

    public ExecutableStep executableStep() {
        return executableStep;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final Hook hook = (Hook) object;
        return scope == hook.scope &&
                executableStep.equals(hook.executableStep);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, executableStep);
    }
}
