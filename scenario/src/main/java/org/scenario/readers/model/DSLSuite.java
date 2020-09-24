package org.scenario.readers.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DSLSuite {
    private String pkg;
    private String name;
    private List<String> hooks;
    private Map<String, String> context;
    private Map<String, DSLScenario> scenarios = new HashMap<>();

    public String getPackage() {
        return pkg;
    }

    public DSLSuite setPackage(final String pkg) {
        this.pkg = pkg;
        return this;
    }

    public String getName() {
        return name;
    }

    public DSLSuite setName(final String name) {
        this.name = name;
        return this;
    }

    public List<String> getHooks() {
        return hooks;
    }

    public DSLSuite setHooks(final List<String> hooks) {
        this.hooks = hooks;
        return this;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public DSLSuite setContext(final Map<String, String> context) {
        this.context = context;
        return this;
    }

    public Map<String, DSLScenario> getScenarios() {
        return scenarios;
    }

    @JsonAnySetter
    public DSLSuite setScenarios(final String name, final DSLScenario value) {
        this.scenarios.put(name, value);
        return this;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final DSLSuite dslSuite = (DSLSuite) object;
        return Objects.equals(pkg, dslSuite.pkg) &&
                Objects.equals(name, dslSuite.name) &&
                Objects.equals(hooks, dslSuite.hooks) &&
                Objects.equals(context, dslSuite.context) &&
                Objects.equals(scenarios, dslSuite.scenarios);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pkg, name, hooks, context, scenarios);
    }

    @Override
    public String toString() {
        return "DSLSuite{" +
                "pkg='" + pkg + '\'' +
                ", name='" + name + '\'' +
                ", hooks=" + hooks +
                ", context=" + context +
                ", scenarios=" + scenarios +
                '}';
    }
}
