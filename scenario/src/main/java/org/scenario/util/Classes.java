package org.scenario.util;

import java.lang.reflect.Constructor;

public class Classes {
    public static <T> T instantiateDefault(final Class<?> clazz) {
        final Constructor constructor;
        try {
            constructor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("No public constructor found for class " + clazz.getName());
        }

        try {
            return (T) constructor.newInstance();
        } catch (final Exception e) {
            throw new IllegalStateException("Failed to create an instance of class " + clazz.getName());
        }
    }
}
