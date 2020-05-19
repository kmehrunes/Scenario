package org.scenario.util;

import org.scenario.exceptions.DefinitionException;
import org.scenario.exceptions.RuntimeReflectionException;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Packages {
    public static List<Class<?>> findClasses(final String packageName) {
        final URL root = packageRootResource(packageName)
                .orElseThrow(() -> new DefinitionException("Couldn't load package " + packageName));

        final File[] files = classFilesFromResource(root)
                .orElseThrow(() -> new DefinitionException("Couldn't get class files from " + root));

        return Stream.of(files)
                .map(file -> classFromFile(packageName, file))
                .collect(Collectors.toList());
    }

    private static Optional<URL> packageRootResource(final String packageName) {
        return Optional.ofNullable(Thread.currentThread().getContextClassLoader()
                .getResource(packageName.replace(".", "/")));
    }

    private static Optional<File[]> classFilesFromResource(final URL root) {
        final FilenameFilter isClassName = (dir, fileName) -> fileName.endsWith(".class");

        return Optional.of(new File(root.getFile()))
                .map(file -> file.listFiles(isClassName));
    }

    private static Class<?> classFromFile(final String packageName, final File file) {
        final String className = file.getName().replace(".class", "");

        try {
            return Class.forName(packageName + "." + className);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeReflectionException(e);
        }
    }
}
