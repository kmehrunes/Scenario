package org.scenario.discovery;

import org.scenario.annotations.Step;
import org.scenario.annotations.StepsContainer;
import org.scenario.definitions.Container;
import org.scenario.definitions.ExecutableStep;
import org.scenario.util.Classes;
import org.scenario.util.Packages;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A class to discover step containers in packages. It will
 * find classes annotated with {@link StepsContainer}, create
 * an instance of those classes using their default
 * constructors, and then create instances of {@link Container}
 * which contain the steps defined within them.
 */
public class StepsContainersDiscovery {
    public List<Container> discover(final List<String> packages) {
        return discover(packages.toArray(new String[0]));
    }

    public List<Container> discover(final String... packages) {
        final List<Container> all = new ArrayList<>();

        for (final String packageName : packages) {
            final List<Class<?>> classes = Packages.findClasses(packageName);
            final List<Container> containers = findContainers(classes);

            all.addAll(containers);
        }

        return all;
    }

    private List<Container> findContainers(final List<Class<?>> classes) {
        return classes.stream()
                .filter(clazz -> clazz.getAnnotation(StepsContainer.class) != null)
                .map(this::createContainer)
                .collect(Collectors.toList());
    }

    private Container createContainer(final Class<?> clazz) {
        final Object instance = Classes.instantiateDefault(clazz);

        final StepsContainer containerAnnotation = clazz.getAnnotation(StepsContainer.class);
        final List<ExecutableStep> steps = findSteps(instance);

        return new Container.Builder()
                .name(containerAnnotation.value())
                .steps(steps)
                .build();
    }

    private List<ExecutableStep> findSteps(final Object instance) {
        final List<Method> definitions = Stream.of(instance.getClass().getMethods())
                .filter(this::definesStep)
                .collect(Collectors.toList());

        return definitions.stream()
                .map(method -> {
                    final Step step = method.getAnnotation(Step.class);
                    final String name = step.name().trim().isEmpty() ? method.getName() : step.name();

                    return new ExecutableStep(name, "", method, instance);
                })
                .collect(Collectors.toList());
    }

    private boolean definesStep(final Method method) {
        return method.getAnnotation(Step.class) != null;
    }
}
