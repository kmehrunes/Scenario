package org.scenario.readers;

import org.scenario.definitions.*;
import org.scenario.discovery.StepsContainersDiscovery;
import org.scenario.readers.model.DSLScenario;
import org.scenario.readers.model.DSLSuite;
import org.scenario.util.Classes;
import org.scenario.util.Packages;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A converter which converts DSL suites to regular suites
 * which the runners understand.
 */
public class DSLConverter {
    private final Map<String, Container> containers;
    private final Map<String, Class> classes;

    public DSLConverter(final List<String> packageNames) {
        this.classes = packageNames.stream()
                .map(Packages::findClasses)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Class::getSimpleName, Function.identity()));

        this.containers = new StepsContainersDiscovery().discover(packageNames)
                .stream()
                .collect(Collectors.toMap(Container::name, Function.identity()));
    }

    public Suite convertSuite(final DSLSuite dsl) {
        final Suite.Builder suite = new Suite.Builder();

        dsl.getHooks().forEach(hooksClassName -> {
            Optional.ofNullable(classes.get(hooksClassName))
                    .map(Classes::instantiateDefault)
                    .map(suite::loadHooks)
                    .orElseThrow(() -> new IllegalArgumentException("No class with name " + hooksClassName +
                            " was found. Make sure that its package is included"));
        });

        dsl.getScenarios().values()
                .stream()
                .map(this::convertScenario)
                .forEach(suite::scenario);

        return suite
                .name(dsl.getName())
                .build();
    }

    private Scenario convertScenario(final DSLScenario dsl) {
        final Object instance = Optional.ofNullable(dsl.getClassName())
                .map(className -> Optional.ofNullable(classes.get(className))
                        .orElseThrow(() -> new IllegalArgumentException("No class with name " + className +
                                " was found. Make sure that its package is included"))
                )
                .map(Classes::instantiateDefault)
                .orElse(null);

        final ScenarioFlow scenarioFlow = convertFlow(dsl.getFlow(), instance);

        return new Scenario.Builder()
                .name(dsl.getName())
                .description(dsl.getDescription())
                .flow(scenarioFlow)
                .build();
    }

    private ScenarioFlow convertFlow(final List<String> dsl, final Object instance) {
        final ScenarioFlow.Builder scenarioFlow = new ScenarioFlow.Builder()
                .instance(instance);

        dsl.stream()
                .map(this::validateAndGetParts)
                .forEach(parts -> {
                    if (parts.length == 1) {
                        if (instance == null) {
                            throw new IllegalArgumentException("A class must be specified if a step method name is used");
                        }

                        scenarioFlow.step(parts[0]);
                    } else {
                        scenarioFlow.step(findStepFromContainer(parts[0], parts[1]));
                    }
                });

        return scenarioFlow.build();
    }

    private ExecutableStep findStepFromContainer(final String containerName, final String stepName) {
        final Container container = Optional.ofNullable(containers.get(containerName))
                .orElseThrow(() -> new IllegalArgumentException("No container named " + containerName +
                        " was found. Make sure that you have the right name and its package is included"));

        return container.findStep(stepName)
                .orElseThrow(() -> new IllegalArgumentException("Container " + container.name() +
                        " doesn't have a step named " + stepName));
    }

    private String[] validateAndGetParts(final String stepName) {
        final String[] parts = stepName.split("::");

        if (parts.length == 1 || parts.length == 2) {
            return parts;
        }  else {
            throw new IllegalArgumentException("A step name should be either the method name if a class is specified or  "
                    + " must follow the format <container>::<step>");
        }
    }
}
