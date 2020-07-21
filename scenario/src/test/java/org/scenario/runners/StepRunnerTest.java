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

    private List<StepReport> runStep(final Method method) {
        final StepRunner runner = new StepRunner(Hooks.empty());
        final StepExecutor executor = new StepExecutor();

        final ExecutableStep step = new ExecutableStep("", "", method, this);

        return runner.runStep(executor, step, new ScenarioContext()).asList();
    }

    @Test
    void runNoTimeoutSuccessStep() throws NoSuchMethodException {
        final List<StepReport> stepReports = runStep(this.getClass().getMethod("noTimeoutSuccessStep"));

        assertThat(stepReports).hasSize(1);
        assertThat(stepReports.get(0).succeeded()).isTrue();
    }

    @Test
    void runNoTimeoutIllegalArgumentExceptionStep() throws NoSuchMethodException {
        final List<StepReport> stepReports = runStep(this.getClass().getMethod("noTimeoutIllegalArgumentException"));

        assertThat(stepReports).hasSize(1);

        final StepReport stepReport = stepReports.get(0);

        assertThat(stepReport.getFailureCause()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void runTimeoutSuccessStep() throws NoSuchMethodException {
        final List<StepReport> stepReports = runStep(this.getClass().getMethod("timeoutSuccessStep"));

        assertThat(stepReports).hasSize(1);
        assertThat(stepReports.get(0).succeeded()).isTrue();
    }

    @Test
    void runTimeoutFailureStepStep() throws NoSuchMethodException {
        final List<StepReport> stepReports = runStep(this.getClass().getMethod("timeoutFailureStep"));

        assertThat(stepReports).hasSize(1);

        final StepReport stepReport = stepReports.get(0);

        assertThat(stepReport.getFailureCause()).isInstanceOf(TimeoutException.class);
    }

    @Test
    void runTimeoutIllegalArgumentExceptionStep() throws NoSuchMethodException {
        final List<StepReport> stepReports = runStep(this.getClass().getMethod("timeoutIllegalArgumentException"));

        assertThat(stepReports).hasSize(1);

        final StepReport stepReport = stepReports.get(0);

        assertThat(stepReport.getFailureCause()).isInstanceOf(IllegalArgumentException.class);
    }
}