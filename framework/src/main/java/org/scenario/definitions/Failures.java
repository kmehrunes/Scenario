package org.scenario.definitions;

import java.util.List;

/**
 * A class to wrap a list of failures. It was introduced to
 * handle passing correct parameter to the hooks even after
 * type erasure.
 */
public class Failures {
    private final List<Failure> failures;

    public Failures(List<Failure> failures) {
        this.failures = failures;
    }

    public List<Failure> asList() {
        return failures;
    }
}
