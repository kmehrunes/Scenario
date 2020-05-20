package org.scenario.definitions;

public class Failure {
    private final ExecutableStep step;
    private final Throwable cause;

    public Failure(final ExecutableStep step, final Throwable cause) {
        this.step = step;
        this.cause = cause;
    }

    public ExecutableStep getStep() {
        return step;
    }

    public Throwable getCause() {
        return cause;
    }
}
