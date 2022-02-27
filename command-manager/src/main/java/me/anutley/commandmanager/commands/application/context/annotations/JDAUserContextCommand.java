package me.anutley.commandmanager.commands.application.context.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotate command methods with this to indicate that it is user context command
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JDAUserContextCommand {

    /**
     * 1-32 alphanumeric characters
     * @return the name of the context command
     */
    String name();

    /**
     *
     * @return the description of the context method
     */
    String description() default "No description";

    /**
     *
     * @return the category of the command
     */
    String category() default "Uncategorised";

}
