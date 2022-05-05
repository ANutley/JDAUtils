package me.anutley.jdautils.commands;

import me.anutley.jdautils.commands.annotations.CommandMeta;
import me.anutley.jdautils.commands.events.CommandEvent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * The base command which all other commands extend
 *
 * @param <A> The annotation which contains the command related information such as the name, description, or category.
 *            It also contains command specific information, for example the slash command options for a slash command
 * @param <E> The event which this event uses
 */
public abstract class Command<A extends Annotation, E extends CommandEvent<?, ?>> {

    private final A annotation;
    private final Method method;
    private final Object instance;

    /**
     * @param annotation The annotation that the command method has, to retrieve information such as the name or description
     * @param method     The method which correlates to this command
     */
    public <T> Command(A annotation, Method method, T instance) {
        this.annotation = annotation;
        this.method = method;
        this.instance = instance;
    }

    /**
     * @return Returns the annotation which holds the information about this command
     */
    public A getAnnotation() {
        return annotation;
    }

    /**
     * @return Returns the method which is invoked to execute the command
     */
    public Method getMethod() {
        return method;
    }

    /**
     * @return Returns an instance of the command class, this is used to invoke the command method
     */
    public Object getInstance() {
        return instance;
    }

    /**
     * @return The name of the command
     */
    public abstract String getName();

    /**
     * @return The description of the command
     */
    public abstract String getDescription();

    /**
     * @return The category of the command
     */
    public abstract String getCategory();

    /**
     * @return The usage of the command
     */
    public abstract String getUsage();

    /**
     * @return A hashmap containing the {@link CommandMeta} information
     */
    public abstract HashMap<String, String> getMetaTags();

    /**
     * This method is used to invoke the command method with the correct arguments
     *
     * @param event The event which should be used to invoke the command method
     */
    public abstract void execute(E event);

}
