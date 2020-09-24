package org.scenario.readers;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.scenario.readers.model.DSLScenario;
import org.scenario.readers.model.DSLSuite;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class YAMLReaderTest {

    @Test
    void readSuite() throws IOException {
        final URL validSuiteUrl = this.getClass().getClassLoader().getResource("dsl-suites/valid.yaml");
        final File validSuite = new File(validSuiteUrl.getFile());
        final YAMLReader reader = new YAMLReader();

        final DSLSuite expected = new DSLSuite().setPackage("org.scenario.examples")
                .setName("Valid Suite")
                .setHooks(Collections.singletonList("DefaultOutputHooks"))
                .setContext(ImmutableMap.of("name", "value"))
                .setScenarios("posts", new DSLScenario().setName("User's posts")
                        .setDescription("The flow of user posting something")
                        .setClass("UserScenario")
                        .setFlow(Arrays.asList("createUser", "publishPost")));

        final DSLSuite actual = reader.readSuite(validSuite);

        assertThat(actual).isEqualTo(expected);
    }
}