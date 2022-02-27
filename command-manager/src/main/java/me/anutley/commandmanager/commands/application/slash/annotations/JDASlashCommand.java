package me.anutley.commandmanager.commands.application.slash.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JDASlashCommand {

    /**
     * 1-32 alphanumeric characters
     * @return the name of the slash command
     */
    String name();

    /**
     * 1-100 characters
     * @return the description of the slash command
     */
    String description() default "No description";

    /**
     * The base description which is set to the slash command if subcommand groups / subcommands are used.
     * This only needs to be set once per command group, and does not need to be set at all if it is only a single command
     * @return the base description of the slash command
     */
    String baseDescription() default "No base command description";

    /**
     *
     * @return the category of the command
     */
    String category() default "Uncategorised";
}
