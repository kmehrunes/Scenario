# Scenario
A flexible testing framework (and library) designed for integration and end-to-end tests.

## Roadmap
Here are the planned features:
- [x] Loading resources (0.0.2)
- [x] Timeouts and circuit breakers (0.0.2)
- [ ] Template engine for resources
- [ ] Report generation
- [ ] Loading scenarios from YAML files
- [ ] Maven plugin
- [ ] Gradle plugin

## Getting the Framework
The framework will be added to Maven central repository soon. In the meantime you
can build it locally by running `mvn clean install`.

## Overview
The framework has a very basic design centered around suites. A suite has a
collection of scenarios, and each scenario has a set of steps. And steps can be
shared across scenarios. For example:
```
tests
  - suite A
    - scenario X
      - step i
      - step j
    - scenario Y
      - step i
      - step k
  - suite B
    - scenario Z
      - step l
      - step m
```

## Basic Example
Let's take a look at a basic example with one suite and one scenario.
```java
public class BasicSuite {
    @SuiteDefinition
    public Suite exampleSuite() {
        return new Suite.Builder()
                .name("Basic suite")
                .loadHooks(new DefaultOutputHooks()) // we'll cover that in later sections
                .loadScenarios(new BasicScenario())
                .build();
    }
}

public class BasicScenario {
    @ScenarioDefinition
    public Scenario scenario() {
        return new Scenario.Builder()
                .name("Basic scenario")
                .flow(new ScenarioFlow.Builder()
                        .instance(this)
                        .step("firstStep")
                        .step("secondStep")
                        .build())
                .build();
    }

    @Step(description = "The first step")
    public void firstStep() { .. step implementation .. }

    @Step(description = "The second step")
    public void secondStep() { .. step implementation .. }
}
```
### Suite definition
To define a suite you need a method which returns a `Suite` and is annotated with `@SuiteDefinition`. The suite is simply given a name, some hooks to ouput the results, and an object which contains scenario definitions. You can add as many as you want by calling `loadScenarios()` for each object.

### Scenario definition
To define a scenario you need a method which returns A `Scenario` and has `@ScenarioDefinition` annotation. Each scenario must have a name and a flow. You can have as many scenario definitions as you want, and you can reuse the same step for multiple scenarios.

#### The flow
A flow is just a set of step methods and class instance which contains them, in our case we just use `this` since we are referencing methods within the same class. You can instantiate a different class with whatever configuration and use that instance.

#### The steps
Each step is just a method which will be called when running the scenario. Here we show only a basic step with no parameters we will show how to use parameterized steps later. Each step also has a description.

## Scenario context
Any step, or hook, can request a `ScenarioContext` as a parameter and an instance will be provided as an argument when that step is called. A scenario context is a way to pass information around across scenario steps. For example, one step could create an entity and then add its ID to the context so that following steps can use it.

Inside a scenario context is also a global context. While each scenario gets a clean slate of its own context, it also gets the global context which could have information passed on at the suite level.
```java
@Step(description = "Scenario context was injected")
public void scenarioContextIsInjected(ScenarioContext scenarioContext) {
    assert scenarioContext != null;
}
```

## Parameterized Steps
A step with `ScenarioContext` as a parameter type is just one example of a parameterized step. You can inject values directly, by type of by name. You can add these values in your suite definition like this
```java
new Suite.Builder()
      ... the rest of the suite ...
      .addToContext(value) // value could be injected by type
      .addToContext("name", value) // value will be injected by name
      .build();
```
And then a step can request it like this
```java
@Step(description = "URL was injected")
public void urlIsInjected(@Name("name") String url) { // you can drop the annotation to get the value by type
    assert url != null;
}
```

### Resource Parameters
A special case of parameters are ones which need the content of a resource. Such
parameters can be annotated with `@Resource("path-of-resource-file")`.

## Circuit Breakers
Often times you might want to stop a scenario if a step failed because the other
steps depend on it. You can annotate such steps with `@CircuitBreaker`. For example
```java
@Step(description = "Create a user")
@CircuitBreaker // if we couldn't create a user then no need to continue
public void createUser(final ScenarioContext context) {
    ...
}
```
**This only applies to scenario flow steps and has no effect on hooks**

## Hooks
The framework supports adding hooks at various levels. In fact, the whole execution flow goes like this:
```
for each suite:
  run BEFORE_SUITE hooks
  for each scenario:
    run BEFORE_SCENARIO hooks
    for each step:
      run BEFORE_STEP hooks
      run step
      run AFTER_STEP hooks
    run AFTER_SCENARIO hooks
  run AFTER_SUITE hooks
```

A hook is just a method with the relevant annotation on it, and it can also be parameterized. As shown in the basic example, we enable `DefaultOutputHooks` for our suite, and one of those hooks is just defined as
```java
@BeforeSuite
public void logBeforeSuite(final Suite suite) {
    Output.info.println("Suite " + suite.name());
}
```

## Using as a Library
You may choose to use `DefaultTestsRunner` to run your tests but maybe you want more control on how the tests are run. For that, the framework exposes its internal components which can help you with that. Those components are split into `discovery` and `runners` packages. You can create your own runner by reusing the components from them.

## Running the Tests
Maven and Gradle plugins will be provided in the future. For now you can still use Scenario through Maven exec plugin as follows (don't forget to provide the test packages to scan for suites)
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <version>${exec-plugin.version}</version>

            <executions>
                <execution>
                    <phase>verify</phase>

                    <goals>
                        <goal>exec</goal>
                    </goals>

                    <configuration>
                        <executable>java</executable>
                        <classpathScope>test</classpathScope>

                        <arguments>
                            <argument>-classpath</argument>

                            <classpath/>

                            <argument>org.scenario.runners.DefaultTestsRunner</argument>
                            <argument>${test-package}</argument>
                        </arguments>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```
