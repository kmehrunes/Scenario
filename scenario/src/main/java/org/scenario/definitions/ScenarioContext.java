package org.scenario.definitions;

import java.util.HashMap;
import java.util.Map;

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

    public Object get(final String key) {
        return local.get(key);
    }

    public ScenarioContext put(final String key, final Object value) {
        local.put(key, value);
        return this;
    }

    public Map<String, Object> global() {
        return global;
    }
}
