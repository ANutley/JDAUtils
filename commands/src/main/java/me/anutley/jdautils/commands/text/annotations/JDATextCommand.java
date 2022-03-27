package me.anutley.jdautils.commands.text.annotations;

import me.anutley.jdautils.commands.annotations.CommandMeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate command methods with this to indicate that it is text based command
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JDATextCommand {

    /**
     * @return the name (or trigger) of the text command
     */
    String name();

    /**
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
     * @return An array of {@link CommandMeta}
     */
    CommandMeta[] meta() default {};
}
