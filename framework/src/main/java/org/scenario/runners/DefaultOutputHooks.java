package org.scenario.runners;

import org.scenario.annotations.*;
import org.scenario.definitions.ExecutableStep;
import org.scenario.definitions.Failure;
import org.scenario.definitions.Scenario;
import org.scenario.definitions.Suite;
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
    public void logAfterStep(final ExecutableStep executableStep, final Step step, final Failure failure) {
        if (failure == null) {
            Output.success.println("\t\t|__ " + executableStep.name() + " - " + step.description() + " [PASSED]");
        } else {
            Output.error.println("\t\t|__ " + executableStep.name() + " - " + step.description() + " [FAILED]");
        }
    }

    @AfterSuite
    public void reportAfterSuite(final Suite suite, final List<Failure> failures) {
        if (failures == null || failures.isEmpty()) {
            Output.success.println("Suite " + suite.name() + " finished successfully");
        } else {
            Output.error.println("Suite " + suite.name() + " finished with failures");

            for (int i = 0; i < failures.size(); i++) {
                final Failure failure = failures.get(i);
                Output.error.println(String.format("[%d.] %s::%s - %s", i + 1,
                        failure.getStep().instance().getClass().getSimpleName(), failure.getStep().method().getName(),
                        failure.getStep().description()));
                failure.getCause().printStackTrace();
            }
        }
    }
}
