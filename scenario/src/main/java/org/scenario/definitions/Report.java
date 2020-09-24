package org.scenario.definitions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The full execution report of a set of steps.
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
