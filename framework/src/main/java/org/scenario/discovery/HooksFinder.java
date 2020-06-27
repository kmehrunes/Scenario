package org.scenario.discovery;

import org.scenario.annotations.*;
import org.scenario.definitions.ExecutableStep;
import org.scenario.definitions.Hook;
import org.scenario.definitions.MethodScope;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HooksFinder {
    public List<Hook> findInObject(final Object object) {
        Objects.requireNonNull(object, "object cannot be null");

        return Stream.of(object.getClass().getMethods())
                .flatMap(method -> findInMethod(method, object))
                .collect(Collectors.toList());
    }

    private Stream<Hook> findInMethod(final Method method, final Object instance) {
        final Stream.Builder<Hook> stream = Stream.builder();

        findHook(method, instance, MethodScope.BEFORE_SUITE, BeforeSuite.class, BeforeSuite::description)
                .ifPresent(stream::add);
        findHook(method, instance, MethodScope.AFTER_SUITE, AfterSuite.class, AfterSuite::description)
                .ifPresent(stream::add);

        findHook(method, instance, MethodScope.BEFORE_SCENARIO, BeforeScenario.class, BeforeScenario::description)
                .ifPresent(stream::add);
        findHook(method, instance, MethodScope.AFTER_SCENARIO, AfterScenario.class, AfterScenario::description)
                .ifPresent(stream::add);

        findHook(method, instance, MethodScope.BEFORE_STEP, BeforeStep.class, BeforeStep::description)
                .ifPresent(stream::add);
        findHook(method, instance, MethodScope.AFTER_STEP, AfterStep.class, AfterStep::description)
                .ifPresent(stream::add);

        return stream.build();
    }

    private <T extends Annotation> Optional<Hook> findHook(final Method method, final Object instance, final MethodScope scope,
                                                           final Class<T> annotation, final Function<T, String> descriptionMethod) {
        return Optional.ofNullable(method.getAnnotation(annotation))
                .map(descriptionMethod)
                .map(description -> new ExecutableStep(method.getName(), description, method, instance, scope))
                .map(executableStep -> new Hook(scope, executableStep));
    }
}
