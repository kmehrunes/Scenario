package org.scenario.definitions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A class to wrap a list of stepReports. It was introduced to
 * handle passing correct parameter to the hooks even after
 * type erasure.
 */
public class Report {
    private final List<StepReport> stepReports;

    public Report(List<StepReport> stepReports) {
        this.stepReports = stepReports;
    }

    public List<StepReport> asList() {
        return stepReports;
    }

    public List<StepReport> failedSteps() {
        return stepReports.stream()
                .filter(StepReport::failed)
                .collect(Collectors.toList());
    }

    public boolean containsFailures() {
        return stepReports.stream().anyMatch(StepReport::failed);
    }
}
