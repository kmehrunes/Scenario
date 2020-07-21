package org.scenario.definitions;

import java.time.Duration;

/**
 * The execution report of a single {@link ExecutableStep}
 */
public class StepReport {
    private final ExecutableStep step;
    private final Throwable failureCause;
    private final Duration timeTaken;

    private StepReport(final ExecutableStep step, final Throwable failureCause, final Duration timeTaken) {
        this.step = step;
        this.failureCause = failureCause;
        this.timeTaken = timeTaken;
    }

    public static StepReport failure(final ExecutableStep step, final Throwable failureCause) {
        return new StepReport(step, failureCause, null);
    }

    public static StepReport success(final ExecutableStep step, final Duration timeTaken) {
        return new StepReport(step, null, timeTaken);
    }

    public static StepReport success(final ExecutableStep step) {
        return new StepReport(step, null, null);
    }

    public ExecutableStep getStep() {
        return step;
    }

    public Throwable getFailureCause() {
        return failureCause;
    }

    public boolean succeeded() {
        return failureCause == null;
    }

    public boolean failed() {
        return failureCause != null;
    }

    public Duration getTimeTaken() {
        return timeTaken;
    }
}
