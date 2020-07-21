package org.scenario.definitions;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The context which will be passed to all steps regardless
 * of a scope. It contains context information added by the
 * suite definition. It's not meant to only be queried and
 * never updated. For mutable context parameters within
 * scopes of a scenario use {@link ScenarioContext}.
 */
public class ExecutionContext {
    private final Map<Class<?>, Object> contextObjects;
    private final Map<String, Object> namedContextObjects;

    public ExecutionContext(final Map<Class<?>, Object> contextObjects, final Map<String, Object> namedContextObjects) {
        this.contextObjects = contextObjects;
        this.namedContextObjects = namedContextObjects;
    }

    public Optional<Object> getByClass(final Class<?> clazz) {
        return Optional.ofNullable(contextObjects.get(clazz));
    }

    public Optional<Object> getByName(final String name) {
        return Optional.ofNullable(namedContextObjects.get(name));
    }

    public Builder toBuilder() {
        final Builder builder = new Builder();

        builder.contextObjects(new HashMap<>(contextObjects));
        builder.namedContextObjects(new HashMap<>(namedContextObjects));

        return builder;
    }
    
    public static class Builder {
        private Map<Class<?>, Object> contextObjects = new HashMap<>();
        private Map<String, Object> namedContextObjects = new HashMap<>();

        public Builder contextObjects(final Map<Class<?>, Object> contextObjects) {
            this.contextObjects = contextObjects;
            return this;
        }

        public Builder namedContextObjects(final Map<String, Object> namedContextObjects) {
            this.namedContextObjects = namedContextObjects;
            return this;
        }

        public Builder add(final Object object) {
            if (Annotation.class.isAssignableFrom(object.getClass())) {
                return add(object, ((Annotation) object).annotationType());
            } else {
                return add(object, object.getClass());
            }
        }

        public Builder addNamed(final String name, final Object object) {
            if (Annotation.class.isAssignableFrom(object.getClass())) {
                return addNamed(name, object, ((Annotation) object).annotationType());
            } else {
                return addNamed(name, object, object.getClass());
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

        public Builder addNamed(final String name, final Object object, final Class<?> asType) {
            if (object == null) {
                contextObjects.put(asType, Optional.empty());
                namedContextObjects.put(name, Optional.empty());
            } else if (Annotation.class.isAssignableFrom(object.getClass())) {
                contextObjects.put(((Annotation) object).annotationType(), object);
                namedContextObjects.put(name, object);
            } else {
                contextObjects.put(asType, object);
                namedContextObjects.put(name, object);
            }
            return this;
        }

        public ExecutionContext build() {
            return new ExecutionContext(contextObjects, namedContextObjects);
        }
    }
}
