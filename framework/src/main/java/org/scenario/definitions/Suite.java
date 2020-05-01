package org.scenario.definitions;

import org.scenario.discovery.HooksFinder;
import org.scenario.discovery.ScenarioDiscovery;

import java.util.ArrayList;
import java.util.List;

public final class Suite {
    private final String name;
    private final String description;
    private final Hooks hooks;
    private final List<Scenario> scenarios;
    private final ExecutionContext.Builder executionContext;

    public Suite(final String name, final String description, final Hooks hooks,
                 final List<Scenario> scenarios, final ExecutionContext.Builder executionContext) {
        this.name = name;
        this.description = description;
        this.hooks = hooks;
        this.scenarios = scenarios;
        this.executionContext = executionContext;
    }

    public List<Scenario> scenarios() {
        return scenarios;
    }

    public Hooks hooks() {
        return hooks;
    }

    public String name() {
        return name;
    }

    public String description() {
        return name;
    }

    public ExecutionContext.Builder executionContext() {
        return executionContext;
    }

    public static class Builder {
        private String name;
        private String description;
        private final Hooks.Builder hooksBuilder;
        private final List<Scenario> scenarios;

        private final HooksFinder hooksFinder;
        private final ScenarioDiscovery scenarioDiscovery;

        private final ExecutionContext.Builder executionContext;

        public Builder() {
            this.hooksBuilder = new Hooks.Builder();
            this.scenarios = new ArrayList<>();

            this.hooksFinder = new HooksFinder();
            this.scenarioDiscovery = new ScenarioDiscovery();

            this.executionContext = new ExecutionContext.Builder();
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        public Builder scenario(final Scenario scenario) {
            this.scenarios.add(scenario);
            return this;
        }

        public Builder loadScenarios(final Object configuration) {
            this.scenarios.addAll(scenarioDiscovery.discover(configuration));
            return this;
        }

        public Builder loadHooks(final Object configuration) {
            hooksFinder.findInObject(configuration)
                    .forEach(hook -> hooksBuilder.addHook(hook.scope(), hook.executableStep()));

            return this;
        }

        public Builder addToContext(final Object object) {
            executionContext.add(object);

            return this;
        }

        public Builder addToContext(final String name, final Object object) {
            executionContext.addNamed(name, object);

            return this;
        }

        public Suite build() {
            return new Suite(name, description, hooksBuilder.build(), scenarios, executionContext);
        }
    }
}
