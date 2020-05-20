package org.scenario.runners;

import org.scenario.annotations.*;
import org.scenario.definitions.*;
import org.scenario.util.Output;

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
    public void logAfterStep(final ExecutableStep executableStep, final Step step, final Failures failures) {
        if (failures == null) {
            Output.success.println("\t\t|__ " + executableStep.name() + " - " + step.description() + " [PASSED]");
        } else {
            Output.error.println("\t\t|__ " + executableStep.name() + " - " + step.description() + " [FAILED]");
        }
    }

    @AfterSuite
    public void reportAfterSuite(final Suite suite, final Failures failures) {
        if (failures == null || failures.asList().isEmpty()) {
            Output.success.println("Suite " + suite.name() + " finished successfully");
        } else {
            Output.error.println("Suite " + suite.name() + " finished with failures");

            for (int i = 0; i < failures.asList().size(); i++) {
                final Failure failure = failures.asList().get(i);
                Output.error.println(String.format("[%d.] %s::%s - %s", i + 1,
                        failure.getStep().instance().getClass().getSimpleName(), failure.getStep().method().getName(),
                        failure.getStep().description()));
                failure.getCause().printStackTrace();
            }
        }
    }
}
