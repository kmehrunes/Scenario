package org.scenario.runners;

import org.scenario.annotations.*;
import org.scenario.definitions.*;
import org.scenario.util.Output;

import java.util.List;

public class DefaultOutputHooks {

    @BeforeSuite
    public void logBeforeSuite(final Suite suite) {
        Output.info.println("Suite " + suite.name());
    }

    @BeforeScenario
    public void logBeforeScenario(final Scenario scenario) {
        Output.info.println("\tScenario " + scenario.name());
    }

    @AfterStep
    public void logAfterStep(final ExecutableStep executableStep, final Step step, final Report report) {
        if (report.containsFailures()) {
            Output.error.println("\t\t|__ " + executableStep.name() + " - " + step.description() + " [FAILED]");
        } else {
            Output.success.println("\t\t|__ " + executableStep.name() + " - " + step.description() + " [PASSED]");
        }
    }

    @AfterSuite
    public void reportAfterSuite(final Suite suite, final Report report) {
        if (report == null || !report.containsFailures()) {
            Output.success.println("Suite " + suite.name() + " finished successfully");
        } else {
            Output.error.println("Suite " + suite.name() + " finished with report");

            final List<StepReport> failedSteps = report.failedSteps();

            for (int i = 0; i < failedSteps.size(); i++) {
                final StepReport stepReport = failedSteps.get(i);
                Output.error.println(String.format("[%d.] %s::%s - %s", i + 1,
                        stepReport.getStep().instance().getClass().getSimpleName(), stepReport.getStep().method().getName(),
                        stepReport.getStep().description()));
                stepReport.getFailureCause().printStackTrace();
            }
        }
    }
}
