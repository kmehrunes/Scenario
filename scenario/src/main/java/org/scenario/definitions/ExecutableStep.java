package org.scenario.definitions;

import org.scenario.annotations.CircuitBreaker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * A wrapper for a method to be executed within a scope. It
 * includes the object for which the method will be invoked as
 * well as some other meta data provided by the annotations.
 */
public class ExecutableStep {
    private final String name;
    private final String description;
    private final Method method;
    private final Object instance;
    private final MethodScope scope;

    public ExecutableStep(final String name, final String description, final Method method, final Object instance) {
        this(name, description, method, instance, null);
    }

    public ExecutableStep(final String name, final String description, final Method method, final Object instance,
                          final MethodScope scope) {
        Objects.requireNonNull(name, "Executable step name cannot be null");
        Objects.requireNonNull(description, "Executable step description cannot be null");
        Objects.requireNonNull(method, "Executable step method cannot be null");
        Objects.requireNonNull(instance, "Executable step instance cannot be null");

        this.name = name;
        this.description = description;
        this.method = method;
        this.instance = instance;
        this.scope = scope;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public Method method() {
        return method;
    }

    public Object instance() {
        return instance;
    }

    public void execute(final Object... args) throws InvocationTargetException, IllegalAccessException {
        method.invoke(instance, args);
    }

    public boolean breaksCircuit() {
        return method.getAnnotation(CircuitBreaker.class) != null;
    }

    public MethodScope scope() {
        return scope;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final ExecutableStep step = (ExecutableStep) object;
        return Objects.equals(name, step.name) &&
                Objects.equals(description, step.description) &&
                Objects.equals(method, step.method) &&
                Objects.equals(instance, step.instance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, method, instance);
    }
}
