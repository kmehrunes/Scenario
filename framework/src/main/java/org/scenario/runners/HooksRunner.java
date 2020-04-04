package org.scenario.runners;

import org.scenario.definitions.*;
import org.scenario.util.Output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class HooksRunner {
    private final Hooks hooks;
    private final StepExecutor executor;

    HooksRunner(final Hooks hooks, final StepExecutor executor) {
        this.hooks = hooks;
        this.executor = executor;
    }

    Failures run(final Hooks.Scope scope, final Scenario scenario, final ScenarioContext scenarioContext,
                 final Failures previousFailures, boolean abortOnFailure) {
        final ExecutionContext.Builder executionContextBuilder = new ExecutionContext.Builder()
                .add(scenario, Scenario.class)
                .add(scenarioContext);

        return run(scope, executionContextBuilder, previousFailures, abortOnFailure);
    }

    Failures run(final Hooks.Scope scope, final Suite suite, final ScenarioContext scenarioContext,
                 final Failures previousFailures, boolean abortOnFailure) {

        final ExecutionContext.Builder executionContextBuilder = new ExecutionContext.Builder()
                .add(suite, Suite.class)
                .add(scenarioContext);

        return run(scope, executionContextBuilder, previousFailures, abortOnFailure);
    }

    Failures run(final Hooks.Scope scope, final ExecutionContext.Builder contextBuilder,
                 final Failures previousFailures, boolean abortOnFailure) {
        final List<Failure> hooksFailures = new ArrayList<>();

        for (final ExecutableStep step : hooks.executableSteps(scope)) {
            final ExecutionContext executionContext = contextBuilder
                    .add(previousFailures, Failures.class)
                    .build();

            final Optional<Failure> failure = executor.execute(step, executionContext);

            if (failure.isPresent()) {
                Output.error.println((step.description().isEmpty() ? step.name() : step.description())
                        + " Failed with exception " + failure.get().getCause());

                failure.get().getCause().printStackTrace();

                if (abortOnFailure) {
                    return new Failures(Collections.singletonList(failure.get()));
                } else {
                    hooksFailures.add(failure.get());
                }
            }
        }

        return new Failures(hooksFailures);
    }
}
