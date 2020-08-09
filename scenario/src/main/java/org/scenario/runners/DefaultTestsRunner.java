package org.scenario.runners;

import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.scenario.definitions.Report;
import org.scenario.definitions.StepReport;
import org.scenario.definitions.Suite;
import org.scenario.discovery.SuiteDiscovery;
import org.scenario.exceptions.TestFailuresExceptions;
import org.scenario.readers.DSLConverter;
import org.scenario.readers.DSLReader;
import org.scenario.readers.YAMLReader;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A runner to be used if no custom runner is given. It
 * requires a list of packages to be passed to it. Suites
 * defined within the packages will be discovered by
 * {@link SuiteDiscovery} and run. It will throw a
 * {@link TestFailuresExceptions} if any suite finished
 * with any failure.
 */
public class DefaultTestsRunner {
    private static final Pattern RESOURCES_REGEX = Pattern.compile(".*\\.suite\\.yaml");

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalStateException("DefaultRunner requires at least one package to scan");
        }

        final List<Suite> suites = findSuites(args);
        final SuiteRunner runner = new SuiteRunner();

        final List<StepReport> stepReports = suites.stream()
                .map(runner::run)
                .flatMap(suiteFailures -> suiteFailures.asList().stream())
                .collect(Collectors.toList());

        final Report report = new Report(stepReports);

        if (report.containsFailures()) {
            throw new TestFailuresExceptions("There are test failures");
        }
    }

    private static List<Suite> findSuites(final String... packages) {
        final SuiteDiscovery suiteDiscovery = new SuiteDiscovery();
        final List<File> dslSuitesFiles = loadSuiteYamlFiles(packages);

        final List<Suite> dslSuites = readDslSuites(Arrays.asList(packages), dslSuitesFiles);
        final List<Suite> suites = suiteDiscovery.discover(packages);

        return Stream.concat(dslSuites.stream(), suites.stream())
                .collect(Collectors.toList());
    }

    private static List<File> loadSuiteYamlFiles(final String... packages) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final Configuration configuration = new ConfigurationBuilder()
                .addClassLoader(classLoader)
                .addScanners(new ResourcesScanner())
                .forPackages(packages);

        final Reflections reflections = new Reflections(configuration);

        return reflections.getResources(RESOURCES_REGEX)
                .stream()
                .map(classLoader::getResource)
                .filter(Objects::nonNull)
                .map(URL::getFile)
                .map(File::new)
                .collect(Collectors.toList());
    }

    private static List<Suite> readDslSuites(final List<String> packageNames, final List<File> suitesFiles) {
        final DSLReader reader = new YAMLReader();
        final DSLConverter converter = new DSLConverter(packageNames);

        return suitesFiles.stream()
                .map(file -> {
                    try {
                        return reader.readSuite(file);
                    } catch (final IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .map(converter::convertSuite)
                .collect(Collectors.toList());
    }
}
