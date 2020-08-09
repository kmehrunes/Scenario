package org.scenario.readers;

import org.scenario.readers.model.DSLSuite;

import java.io.File;
import java.io.IOException;

public interface DSLReader {
    DSLSuite readSuite(File file) throws IOException;
}
