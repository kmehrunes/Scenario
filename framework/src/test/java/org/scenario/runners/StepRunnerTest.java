package org.scenario.runners;

import org.junit.jupiter.api.Test;
import org.scenario.annotations.Step;
import org.scenario.annotations.Timeout;
import org.scenario.definitions.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

public class StepRunnerTest {

    @Step
    public void noTimeoutSuccessStep() { }

    @Step
    public void noTimeoutIllegalArgumentException() {
        throw new IllegalArgumentException();
    }

    @Step
    @Timeout(value = 1)
    public void timeoutSuccessStep() { }

    @Step
    @Timeout(value = 1)
    public void timeoutFailureStep() throws InterruptedException {
        Thread.sleep(2000);
    }

    @Step
    @Timeout(value = 1)
    public void timeoutIllegalArgumentException() {
        throw new IllegalArgumentException();
    }

    private List<Failure> runStep(final Method method) {
        final StepRunner runner = new StepRunner(Hooks.empty());
        final StepExecutor executor = new StepExecutor();

        final ExecutableStep step = new ExecutableStep("", "", method, this);

        return runner.runStep(executor, step, new ScenarioContext()).asList();
    }

    @Test
    void runNoTimeoutSuccessStep() throws NoSuchMethodException {
        final List<Failure> failures = runStep(this.getClass().getMethod("noTimeoutSuccessStep"));

        assertThat(failures).isEmpty();
    }

    @Test
    void runNoTimeoutIllegalArgumentExceptionStep() throws NoSuchMethodException {
        final List<Failure> failures = runStep(this.getClass().getMethod("noTimeoutIllegalArgumentException"));

        assertThat(failures).hasSize(1);

        final Failure failure = failures.get(0);

        assertThat(failure.getCause()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void runTimeoutSuccessStep() throws NoSuchMethodException {
        final List<Failure> failures = runStep(this.getClass().getMethod("timeoutSuccessStep"));

        assertThat(failures).isEmpty();
    }

    @Test
    void runTimeoutFailureStepStep() throws NoSuchMethodException {
        final List<Failure> failures = runStep(this.getClass().getMethod("timeoutFailureStep"));

        assertThat(failures).hasSize(1);

        final Failure failure = failures.get(0);

        assertThat(failure.getCause()).isInstanceOf(TimeoutException.class);
    }

    @Test
    void runTimeoutIllegalArgumentExceptionStep() throws NoSuchMethodException {
        final List<Failure> failures = runStep(this.getClass().getMethod("timeoutIllegalArgumentException"));

        assertThat(failures).hasSize(1);

        final Failure failure = failures.get(0);

        assertThat(failure.getCause()).isInstanceOf(IllegalArgumentException.class);
    }
}