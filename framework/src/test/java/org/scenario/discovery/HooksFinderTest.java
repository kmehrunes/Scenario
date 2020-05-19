package org.scenario.discovery;

import org.junit.jupiter.api.Test;
import org.scenario.annotations.AfterStep;
import org.scenario.annotations.BeforeSuite;
import org.scenario.definitions.ExecutableStep;
import org.scenario.definitions.Hook;
import org.scenario.definitions.Hooks;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HooksFinderTest {

    static class HooksContainer {
        @BeforeSuite(description = "Before suite")
        public void beforeSuite() { }

        @AfterStep
        public void afterStep() { }
    }

    @Test
    void findInObject() throws NoSuchMethodException {
        final HooksFinder hooksFinder = new HooksFinder();
        final HooksContainer container = new HooksContainer();

        final List<Hook> hooks = hooksFinder.findInObject(container);

        final ExecutableStep expectedBeforeSuite = new ExecutableStep("beforeSuite", "Before suite",
                HooksContainer.class.getMethod("beforeSuite"), container);
        final ExecutableStep expectedAfterStep = new ExecutableStep("afterStep", "",
                HooksContainer.class.getMethod("afterStep"), container);

        assertThat(hooks).containsExactlyInAnyOrder(
                        new Hook(Hooks.Scope.BEFORE_SUITE, expectedBeforeSuite),
                        new Hook(Hooks.Scope.AFTER_STEP, expectedAfterStep));
    }
}