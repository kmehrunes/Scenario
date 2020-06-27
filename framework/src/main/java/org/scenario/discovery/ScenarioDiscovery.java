package org.scenario.discovery;

import org.scenario.annotations.ScenarioDefinition;
import org.scenario.definitions.ExecutableStep;
import org.scenario.definitions.Scenario;
import org.scenario.exceptions.DefinitionException;
import org.scenario.exceptions.RuntimeReflectionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScenarioDiscovery {
    public List<Scenario> discover(final Object object) {
        final List<ExecutableStep> scenarioDefinitions = findScenarioDefinitionsMethods(object);

        return scenarioDefinitions.stream()
                .map(definition -> {
                    try {
                        return definition.method().invoke(definition.instance());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeReflectionException(e);
                    }
                })
                .map(result -> (Scenario) result)
                .collect(Collectors.toList());
    }

    private List<ExecutableStep> findScenarioDefinitionsMethods(final Object instance) {
        final List<Method> definitions = Stream.of(instance.getClass().getMethods())
                .filter(this::definesScenario)
                .peek(this::returnsScenario)
                .collect(Collectors.toList());

        if (definitions.isEmpty()) {
            return Collections.emptyList();
        }

        return definitions.stream()
                .map(method -> new ExecutableStep(method.getName(), "", method, instance))
                .collect(Collectors.toList());
    }

    private boolean definesScenario(final Method method) {
        return method.getAnnotation(ScenarioDefinition.class) != null;
    }

    private void returnsScenario(final Method method) {
        if (!Scenario.class.isAssignableFrom(method.getReturnType())) {
            throw new DefinitionException("Method " + method.getName() + " doesn't return an instance of Scenario");
        }
    }

}
