package org.scenario.logging;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class InMemoryStdOut extends PrintStream {
    private final ByteArrayOutputStream stream;

    public InMemoryStdOut(final ByteArrayOutputStream outputStream) {
        super(outputStream);
        this.stream = outputStream;
    }

    public ByteArrayOutputStream getStream() {
        return this.stream;
    }
}
