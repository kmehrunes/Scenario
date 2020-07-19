package org.scenario.definitions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;

/**
 * Convenience wrapper for reading a resource file.
 */
public class ResourceFile {
    private final File resource;

    public ResourceFile(final File resource) {
        this.resource = resource;
    }

    public InputStream asInputStream() {
        try {
            return Files.newInputStream(resource.toPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String asString() {
        try {
            return Files.readString(resource.toPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public byte[] asBytes() {
        try {
            return Files.readAllBytes(resource.toPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
