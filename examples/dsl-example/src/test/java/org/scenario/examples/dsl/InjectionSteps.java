package org.scenario.examples.dsl;

import org.scenario.annotations.Name;
import org.scenario.annotations.ScenarioDefinition;
import org.scenario.annotations.Step;
import org.scenario.annotations.StepsContainer;
import org.scenario.definitions.Scenario;
import org.scenario.definitions.ScenarioContext;
import org.scenario.definitions.ScenarioFlow;

@StepsContainer("Injection")
public class InjectionSteps {

    @ScenarioDefinition
    public Scenario injectionScenario() {
        return new Scenario.Builder()
                .name("Parameter injection")
                .description("Verify that parameters were injected correctly")
                .flow(new ScenarioFlow.Builder()
                        .instance(this)
                        .step("urlIsInjected")
                        .step("scenarioContextIsInjected")
                        .build()
                )
                .build();
    }

    @Step(description = "URL was injected")
    public void urlIsInjected(@Name("baseUrl") String url) {
        assert url != null;
        assert url.equals("N/A");
    }

    @Step(name = "scenarioContext", description = "Scenario context was injected")
    public void scenarioContextIsInjected(ScenarioContext scenarioContext) {
        assert scenarioContext != null;
    }
}
