package org.scenario.definitions;

import java.util.Objects;

/**
 * A user-defined hook definition which is meant to
 * be executed within a certain scope. For available
 * scopes check {@link MethodScope}.
 */
public class Hook {
    private final MethodScope scope;
    private final ExecutableStep executableStep;

    public Hook(final MethodScope scope, final ExecutableStep executableStep) {
        Objects.requireNonNull(scope, "Hook scope cannot be null");
        Objects.requireNonNull(executableStep, "Hook executable step cannot be null");

        this.scope = scope;
        this.executableStep = executableStep;
    }

    public MethodScope scope() {
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
