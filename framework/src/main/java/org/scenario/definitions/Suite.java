package org.scenario.definitions;

import java.util.ArrayList;
import java.util.List;

public class Suite {
    private final String name;
    private final Hooks hooks;
    private final List<Scenario> scenarios;

    public Suite(final String name, final Hooks hooks, final List<Scenario> scenarios) {
        this.name = name;
        this.hooks = hooks;
        this.scenarios = scenarios;
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

    public static class Builder {
        private String name;
        private final Hooks.Builder hooksBuilder;
        private final List<Scenario> scenarios;

        private final HooksFinder hooksFinder;

        public Builder() {
            this.hooksBuilder = new Hooks.Builder();
            this.scenarios = new ArrayList<>();
            this.hooksFinder = new HooksFinder();
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder scenario(final Scenario scenario) {
            this.scenarios.add(scenario);
            return this;
        }

        public Builder loadHooks(final Object configuration) {
            hooksFinder.findInObject(configuration)
                    .forEach(hook -> hooksBuilder.addHook(hook.scope(), hook.executableStep()));

            return this;
        }

        public Suite build() {
            return new Suite(name, hooksBuilder.build(), scenarios);
        }
    }
}
