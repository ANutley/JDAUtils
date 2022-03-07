package me.anutley.jdautils.commands.utils;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static org.reflections.scanners.Scanners.SubTypes;

public class ReflectionsUtil {

    /**
     * @param packageName The package name to search in
     * @param clazz       The annotation to filter the search by
     * @return a list of all the classes found from the search
     */
    public static List<Class<?>> getClassesWithAnnotationsByPackage(String packageName, Class<? extends Annotation> clazz) {
        Reflections reflect = new Reflections(
                new ConfigurationBuilder()
                        .forPackages(packageName)
                        .setScanners(Scanners.Resources, Scanners.TypesAnnotated, SubTypes.filterResultsBy(c -> true))
                        .setUrls(ClasspathHelper.forPackage(packageName))
                        .filterInputsBy(new FilterBuilder().includePackage(packageName))
        );

        return new ArrayList<>(reflect.getTypesAnnotatedWith(clazz));
    }
}
