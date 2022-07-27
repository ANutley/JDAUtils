package me.anutley.jdautils.commands.application.context.annotations;

import me.anutley.jdautils.commands.annotations.CommandMeta;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotate command methods with this to indicate that it is user context command
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JDAUserContextCommand {

    /**
     * 1-32 alphanumeric characters
     *
     * @return the name of the context command
     */
    String name();

    /**
     * This is not provided to Discord (as context commands do not have a description), but this can be used internally, for example in a help command
     *
     * @return the description of the command
     */
    String description() default "";

    /**
     * @return the category of the command
     */
    String category() default "";

    /**
     * @return the usage of the command
     */
    String usage() default "";

    /**
     * This can be used to set meta information that isn't provided by default. For example a meta key "platform" with the value of "discord" or "minecraft".
     *
     * @return An array of {@link CommandMeta}
     */
    CommandMeta[] meta() default {};

}
