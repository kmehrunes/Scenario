package org.scenario.runners;

import org.junit.jupiter.api.Test;
import org.scenario.annotations.BeforeStep;
import org.scenario.annotations.Name;
import org.scenario.annotations.Resource;
import org.scenario.annotations.Step;
import org.scenario.definitions.ExecutableStep;
import org.scenario.definitions.ExecutionContext;
import org.scenario.definitions.Failure;
import org.scenario.definitions.ScenarioContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StepExecutorTest {

    @BeforeStep
    public void beforeStep(final Step step, final ScenarioContext scenarioContext, final Method method, final Failure failure) {
        Objects.requireNonNull(step);
        Objects.requireNonNull(scenarioContext);
        Objects.requireNonNull(method);

        assertNull(failure);
    }

    @Step
    public void step() { }

    @Step
    public void stepWithParam(final @Name("url") String url) { }

    @Step
    public void stepWithResourceParam(final @Resource("test-resource.txt") String value) { }

    public void stepWithResourceParamWrongType(final @Resource("test-resource.txt") Integer value) { }

    @Test
    void prepareArgs() throws NoSuchMethodException {
        final Method stepMethod = this.getClass().getMethod("step");
        final Method beforeStepMethod = this.getClass().getMethod("beforeStep", Step.class, ScenarioContext.class,
                Method.class, Failure.class);
        final Step stepAnnotation = stepMethod.getAnnotation(Step.class);
        final ScenarioContext scenarioContext = new ScenarioContext(new HashMap<>());

        final ExecutionContext executionContext = new ExecutionContext.Builder()
                .add(stepMethod)
                .add(scenarioContext)
                .add(stepAnnotation)
                .add(null, Failure.class)
                .build();

        final ExecutableStep executableStep = new ExecutableStep(beforeStepMethod.getName(),
                stepAnnotation.description(), beforeStepMethod, this);

        final StepExecutor executor = new StepExecutor();

        final Object[] actual = executor.prepareArgs(executableStep, executionContext);
        final Object[] expected = new Object[] {
                stepAnnotation,
                scenarioContext,
                stepMethod,
                null
        };

        assertArrayEquals(expected, actual);
    }

    @Test
    void prepareNamedArgs() throws NoSuchMethodException {
        final Method stepMethod = this.getClass().getMethod("stepWithParam", String.class);

        final ExecutionContext executionContext = new ExecutionContext.Builder()
                .add(stepMethod)
                .addNamed("url", "url-value")
                .build();

        final ExecutableStep executableStep = new ExecutableStep(stepMethod.getName(),
                "", stepMethod, this);

        final StepExecutor executor = new StepExecutor();

        final Object[] actual = executor.prepareArgs(executableStep, executionContext);
        final Object[] expected = new Object[] {
                "url-value"
        };

        assertArrayEquals(expected, actual);
    }

    @Test
    void prepareResourceArgs() throws NoSuchMethodException {
        final Method stepMethod = this.getClass().getMethod("stepWithResourceParam", String.class);

        final ExecutionContext executionContext = new ExecutionContext.Builder()
                .add(stepMethod)
                .build();

        final ExecutableStep executableStep = new ExecutableStep(stepMethod.getName(),
                "", stepMethod, this);

        final StepExecutor executor = new StepExecutor();

        final Object[] actual = executor.prepareArgs(executableStep, executionContext);
        final Object[] expected = new Object[] {
                "LOADED"
        };

        assertArrayEquals(expected, actual);
    }

    @Test
    void prepareResourceArgsWrongType() throws NoSuchMethodException {
        final Method stepMethod = this.getClass().getMethod("stepWithResourceParamWrongType", Integer.class);

        final ExecutionContext executionContext = new ExecutionContext.Builder()
                .add(stepMethod)
                .build();

        final ExecutableStep executableStep = new ExecutableStep(stepMethod.getName(),
                "", stepMethod, this);

        final StepExecutor executor = new StepExecutor();

        assertThatThrownBy(() -> executor.prepareArgs(executableStep, executionContext))
                .isInstanceOf(IllegalArgumentException.class);
    }
}