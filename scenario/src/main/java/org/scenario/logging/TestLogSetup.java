package org.scenario.logging;

import org.scenario.annotations.AfterSuite;
import org.scenario.annotations.BeforeSuite;
import org.scenario.definitions.ScenarioContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class TestLogSetup {
    private static final String STDOUT_PROPERTY = "stdOut";
    private static final String STDERR_PROPERTY = "stdErr";
    private static final String IN_MEMORY_BUFFER = "outBuffer";

    private final LoggingConfiguration configuration;

    public TestLogSetup() {
        this.configuration = LoggingConfiguration.newWithDefaults();
    }

    public TestLogSetup(final LoggingConfiguration configuration) {
        this.configuration = configuration;
    }

    @BeforeSuite
    public void setup(final ScenarioContext context) {
        if (configuration.redirectStdOut()) {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(128);
            final InMemoryStdOut inMemoryStdOut = new InMemoryStdOut(outputStream);

            context.global().put(STDOUT_PROPERTY, System.out);
            context.global().put(STDERR_PROPERTY, System.err);
            context.global().put(IN_MEMORY_BUFFER, inMemoryStdOut);

            System.setOut(inMemoryStdOut);
            System.setErr(inMemoryStdOut);
        }
    }

    @AfterSuite
    public void resetAndPrint(final ScenarioContext context) throws IOException {
        if (configuration.redirectStdOut()) {
            final PrintStream stdOut = context.get(STDOUT_PROPERTY);
            final PrintStream stdErr = context.get(STDERR_PROPERTY);
            final InMemoryStdOut inMemoryStdOut = context.get(IN_MEMORY_BUFFER);

            System.setOut(stdOut);
            System.setErr(stdErr);

            System.out.write(inMemoryStdOut.getStream().toByteArray());
        }
    }
}
