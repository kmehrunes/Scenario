package org.scenario.readers.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class DSLScenario {
    private String name;
    private String description;
    private String _class;
    private List<String> flow;

    public String getName() {
        return name;
    }

    public DSLScenario setName(final String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public DSLScenario setDescription(final String description) {
        this.description = description;
        return this;
    }

    @JsonProperty("class")
    public String getClassName() {
        return _class;
    }

    public DSLScenario setClass(final String _class) {
        this._class = _class;
        return this;
    }

    public List<String> getFlow() {
        return flow;
    }

    public DSLScenario setFlow(final List<String> flow) {
        this.flow = flow;
        return this;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final DSLScenario that = (DSLScenario) object;
        return Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(_class, that._class) &&
                Objects.equals(flow, that.flow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, _class, flow);
    }

    @Override
    public String toString() {
        return "DSLScenario{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", _class='" + _class + '\'' +
                ", flow=" + flow +
                '}';
    }
}
