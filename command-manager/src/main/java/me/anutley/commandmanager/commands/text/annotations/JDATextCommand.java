package me.anutley.commandmanager.commands.text.annotations;

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
     *
     * @return the name (or trigger) of the text command
     */
    String name();

    /**
     *
     * @return the description of the command
     */
    String description() default "No description";

    /**
     *
     * @return the category of the command
     */
    String category() default "Uncategorised";
}
