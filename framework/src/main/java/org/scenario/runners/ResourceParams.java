package org.scenario.runners;

import org.scenario.annotations.Resource;
import org.scenario.definitions.ResourceFile;

import java.io.File;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.Optional;

public class ResourceParams {
    public static boolean isResource(final Parameter parameter) {
        return parameter.getAnnotation(Resource.class) != null;
    }

    public static ResourceFile readResource(final Parameter parameter) {
        return Optional.of(parameter.getAnnotation(Resource.class))
                .map(Resource::value)
                .map(resource -> ResourceParams.class.getClassLoader().getResource(resource))
                .map(URL::getFile)
                .map(fileName -> new ResourceFile(new File(fileName)))
                .orElseThrow(() -> new IllegalStateException("Failed to load resource for parameter " + parameter.getName()));
    }
}
