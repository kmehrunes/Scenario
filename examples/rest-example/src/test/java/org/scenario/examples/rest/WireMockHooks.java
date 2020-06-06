package org.scenario.examples.rest;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import org.scenario.annotations.AfterSuite;
import org.scenario.annotations.BeforeSuite;
import org.scenario.definitions.ScenarioContext;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class WireMockHooks {
    private final boolean verbose;

    public WireMockHooks() {
        this(false);
    }

    public WireMockHooks(final boolean verbose) {
        this.verbose = verbose;
    }

    @BeforeSuite
    public void startWireMock(final ScenarioContext context) {
        final WireMockServer wireMockServer = new WireMockServer(options()
                .notifier(new ConsoleNotifier(verbose))
                .extensions(new ResponseTemplateTransformer(false)) // don't apply it globally
        );

        wireMockServer.start();

        context.global().put("wireMockPort", wireMockServer.port());
        context.global().put("baseUrl", wireMockServer.baseUrl());
        context.global().put("wireMockServer", wireMockServer);

        System.out.println("Started WireMock server on port: " + wireMockServer.port());
    }

    @AfterSuite
    public void stopWireMock(final ScenarioContext context) {
        final WireMockServer wireMockServer = Optional.ofNullable(context.global().get("wireMockServer"))
                .filter(value -> value instanceof WireMockServer)
                .map(value -> (WireMockServer) value)
                .orElseThrow(() -> new IllegalStateException("No valid value found for wireMockServer in context"));

        wireMockServer.stop();

        System.out.println("Successfully shutdown WireMock server");
    }
}
