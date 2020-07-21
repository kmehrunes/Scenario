package org.scenario.logging;

public class LoggingConfiguration {
    private boolean redirectStdOut = true;
    private boolean configureTestOut = true;

    public static LoggingConfiguration newWithDefaults() {
        return new LoggingConfiguration();
    }

    public boolean redirectStdOut() {
        return this.redirectStdOut;
    }

    public boolean configureTestOut() {
        return this.configureTestOut;
    }

    public LoggingConfiguration setRedirectStdOut(final boolean redirectStdOut) {
        this.redirectStdOut = redirectStdOut;
        return this;
    }

    public LoggingConfiguration setConfigureTestOut(final boolean configureTestOut) {
        this.configureTestOut = configureTestOut;
        return this;
    }
}
