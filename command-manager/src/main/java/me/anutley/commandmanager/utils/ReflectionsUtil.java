package me.anutley.commandmanager.utils;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.reflections.scanners.Scanners.SubTypes;

public class ReflectionsUtil {

    /**
     *
     * @param packageName The package name to search in
     * @param clazz The class to filter the search by
     * @return a list of all the classes found from the search
     */
    public static List<Class<?>> getClassesByPackage(String packageName, Class<?> clazz) {
        Reflections reflect = new Reflections(
                new ConfigurationBuilder()
                        .forPackages(packageName)
                        .setScanners(Scanners.Resources, SubTypes.filterResultsBy(c -> true))
                        .setUrls(ClasspathHelper.forPackage(packageName))
                        .filterInputsBy(new FilterBuilder().includePackage(packageName))
        );

        return new ArrayList<>(reflect.getSubTypesOf(clazz));
    }
}
