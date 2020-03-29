package org.scenario.definitions;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ExecutionContext {
    private final Map<Class<?>, Object> contextObjects;

    public ExecutionContext(final Map<Class<?>, Object> contextObjects) {
        this.contextObjects = contextObjects;
    }

    public Optional<Object> getByClass(final Class<?> clazz) {
        return Optional.ofNullable(contextObjects.get(clazz));
    }
    
    public static class Builder {
        private Map<Class<?>, Object> contextObjects = new HashMap<>();


        public Builder add(final Object object) {
            if (Annotation.class.isAssignableFrom(object.getClass())) {
                return add(object, ((Annotation) object).annotationType());
            } else {
                return add(object, object.getClass());
            }
        }

        public Builder add(final Object object, final Class<?> asType) {
            if (object == null) {
                contextObjects.put(asType, Optional.empty());
            } else if (Annotation.class.isAssignableFrom(object.getClass())) {
                contextObjects.put(((Annotation) object).annotationType(), object);
            } else {
                contextObjects.put(asType, object);
            }
            return this;
        }

        public ExecutionContext build() {
            return new ExecutionContext(contextObjects);
        }
    }
}
