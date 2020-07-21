package org.scenario.definitions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A mutable context of values which can be passed
 * around across steps. It has both a local context
 * visible only within a scenario, and a global
 * context for the whole suite.
 */
public class ScenarioContext {
    private final Map<String, Object> local;
    private final Map<String, Object> global;

    public ScenarioContext() {
        this.local = new HashMap<>();
        this.global = new HashMap<>();
    }

    public ScenarioContext(final Map<String, Object> global) {
        this.local = new HashMap<>();
        this.global = global;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final String key) {
        return (T) Optional.ofNullable(local.get(key))
                .orElseGet(() -> global.get(key));
    }

    public ScenarioContext put(final String key, final Object value) {
        local.put(key, value);
        return this;
    }

    public Map<String, Object> global() {
        return global;
    }
}
