package org.scenario.runners;

import org.scenario.annotations.Step;
import org.scenario.definitions.*;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StepRunner {
    private final Hooks hooks;

    public StepRunner(final Hooks hooks) {
        this.hooks = hooks;
    }

    public Failures runStep(final StepExecutor executor, final ExecutableStep step, final ScenarioContext context) {
        final HooksRunner hooksRunner = new HooksRunner(hooks, executor);
        final Failures beforeStepResults = hooksRunner.run(Hooks.Scope.BEFORE_STEP, prepareStepContext(step, context),
                null, true);

        if (!beforeStepResults.asList().isEmpty()) {
            return beforeStepResults;
        }

        Optional<Failure> failure = invokeStep(step, context);

        Failures afterStepFailures = hooksRunner.run(Hooks.Scope.AFTER_STEP, prepareStepContext(step, context),
                failure.map(Collections::singletonList).map(Failures::new).orElse(null), true);

        return new Failures(Stream.concat(afterStepFailures.asList().stream(), failure.stream())
                .collect(Collectors.toList()));
    }

    private ExecutionContext.Builder prepareStepContext(final ExecutableStep step,
                                                        final ScenarioContext scenarioContext) {
        return new ExecutionContext.Builder()
                .add(step)
                .add(step.method())
                .add(step.method().getAnnotation(Step.class))
                .add(scenarioContext);
    }

    private Optional<Failure> invokeStep(final ExecutableStep step, final ScenarioContext context) {
        try {
            if (acceptsContext(step.method())) {
                step.method().invoke(step.instance(), context);
            } else {
                step.method().invoke(step.instance());
            }

            return Optional.empty();
        } catch (final Throwable e) {
            return Optional.of(new Failure(step, e));
        }
    }

    private boolean acceptsContext(final Method method) {
        return method.getParameterCount() == 1;
    }
}
