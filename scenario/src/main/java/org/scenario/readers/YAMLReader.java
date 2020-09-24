package org.scenario.readers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.scenario.readers.model.DSLSuite;

import java.io.File;
import java.io.IOException;

public class YAMLReader implements DSLReader {
    private final ObjectMapper objectMapper;

    public YAMLReader() {
        objectMapper = new ObjectMapper(new YAMLFactory());
    }

    public DSLSuite readSuite(final File file) throws IOException {
        return objectMapper.readValue(file, DSLSuite.class);
    }
}
