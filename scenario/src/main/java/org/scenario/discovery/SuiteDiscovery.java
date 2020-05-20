package org.scenario.discovery;

import org.scenario.annotations.SuiteDefinition;
import org.scenario.definitions.ExecutableStep;
import org.scenario.definitions.Suite;
import org.scenario.exceptions.DefinitionException;
import org.scenario.exceptions.RuntimeReflectionException;
import org.scenario.util.Packages;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SuiteDiscovery {
    public List<Suite> discover(final String... packages) {
        final List<Suite> suites = new ArrayList<>();

        for (final String packageName : packages) {
            final List<Class<?>> classes = Packages.findClasses(packageName);
            final List<ExecutableStep> suiteDefinitions = findSuiteDefinitions(classes);

            suiteDefinitions.stream()
                    .map(definition -> {
                        try {
                            return definition.method().invoke(definition.instance());
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeReflectionException(e);
                        }
                    })
                    .map(result -> (Suite) result)
                    .forEach(suites::add);
        }

        return suites;
    }

    private List<ExecutableStep> findSuiteDefinitions(final List<Class<?>> classes) {
        return classes.stream()
                .map(this::findSuiteDefinitionsMethods)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<ExecutableStep> findSuiteDefinitionsMethods(final Class<?> clazz) {
        final List<Method> definitions = Stream.of(clazz.getMethods())
                .filter(this::definesSuite)
                .peek(this::verifyReturnsSuite)
                .collect(Collectors.toList());

        if (definitions.isEmpty()) {
            return Collections.emptyList();
        }

        final Object instance = createInstance(clazz);

        return definitions.stream()
                .map(method -> new ExecutableStep(method.getName(), "", method, instance))
                .collect(Collectors.toList());
    }

    private boolean definesSuite(final Method method) {
        return method.getAnnotation(SuiteDefinition.class) != null;
    }

    private void verifyReturnsSuite(final Method method) {
        if (!Suite.class.isAssignableFrom(method.getReturnType())) {
            throw new DefinitionException("Method " + method.getName() + " doesn't return an instance of Suite");
        }
    }

    private Object createInstance(final Class<?> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeReflectionException(e);
        }
    }
}
