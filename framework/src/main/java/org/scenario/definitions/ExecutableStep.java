package org.scenario.definitions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ExecutableStep {
    private final String name;
    private final String description;
    private final Method method;
    private final Object instance;

    public ExecutableStep(final String name, final String description, final Method method, final Object instance) {
        this.name = name;
        this.description = description;
        this.method = method;
        this.instance = instance;
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
}
