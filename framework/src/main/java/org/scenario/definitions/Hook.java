package org.scenario.definitions;

public class Hook {
    private final Hooks.Scope scope;
    private final ExecutableStep executableStep;

    public Hook(final Hooks.Scope scope, final ExecutableStep executableStep) {
        this.scope = scope;
        this.executableStep = executableStep;
    }

    public Hooks.Scope scope() {
        return scope;
    }

    public ExecutableStep executableStep() {
        return executableStep;
    }
}
