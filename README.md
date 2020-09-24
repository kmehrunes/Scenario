# Scenario
A flexible testing framework (and library) designed for integration and end-to-end tests.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.kmehrunes/scenario/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.kmehrunes/scenario)
[![Build Status](https://img.shields.io/endpoint.svg?url=https%3A%2F%2Factions-badge.atrox.dev%2Fkmehrunes%2Fscenario%2Fbadge%3Fref%3Dmaster&style=popout)](https://actions-badge.atrox.dev/kmehrunes/scenario/goto?ref=master)

## Roadmap
Here are the planned features:
- [x] Loading resources (0.0.2)
- [x] Timeouts and circuit breakers (0.0.2)
- [x] Loading scenarios from YAML files
- [ ] Template engine for resources
- [ ] Report generation
- [ ] Maven plugin
- [ ] Gradle plugin

## Getting the Framework
Starting from version 0.0.2, the framework is available on Maven Central.

### Maven
```xml
<dependency>
  <groupId>com.github.kmehrunes</groupId>
  <artifactId>scenario</artifactId>
  <version>0.0.2</version>
</dependency>
```

### Gradle
```groovy
implementation 'com.github.kmehrunes:scenario:0.0.2'
```

### SBT
```scala
libraryDependencies += "com.github.kmehrunes" % "scenario" % "0.0.2"
```

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

Suites could be defined in Java (or other JVM languages) or using YAML files.

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

## Step Execution Properties
Beside the properties defined by a step annotation, some execution properties
can be defined.

### Timeouts
Often times you will want to specify a timeout to prevent a step from taking too
long. To specify a timeout you can use `@Timeout` annotation.
```java
@Step(description = "Create a user")
@Timeout(unit = TimeUnit.SECONDS, value = 3)
public void timeout() {
    ...
}
```

### Circuit Breakers
You might want to stop a scenario if a step failed because the other steps depend
on it. You can annotate such steps with `@CircuitBreaker`. For example
```java
@Step(description = "Create a user")
@CircuitBreaker // if we couldn't create a user then no need to continue
public void circuitBreaker(final ScenarioContext context) {
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

## Using YAML
You can opt for defining suites using YAML files. In that case, the steps still
need to be defined in Java or whatever other language you're using. YAML suites
are read from resources and their names must end in **.suits.yaml**.

### Suite
Suites have four primary properties:
- **name**: String
- **description**: String
- **hooks**: Array of strings of class names, they must exist in the packages
given to the runner
- **context**: Just regular key-value pairs

```yaml
name: Suite name
description: Suite description
hooks:
  - HookClass
  - AnotherHookClass
context:
  property: value
  another: value
```

### Scenarios
Anything which doesn't define one of the four properties of a suite is treated
as a scenario. A scenario can be linked to a class using the class name, or just
use steps from a steps containers directly. A flow step which uses a container
must follow the format `<container name>::<step name>`. They're used separately
in the example below but they can be mixed together.

```yaml
usingContainers:
  name: Container scenario
  description: Given a description
  flow:
    - Container::firstStep
    - Container::secondStep

usingClass:
  name: Class scenario
  class: StepsClass
  flow:
    - methodName
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
