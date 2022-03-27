package me.anutley.jdautils.commands.application.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotate command methods with this to create a guild command instead of a global command
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GuildCommand {

    /**
     * @return returns the id of the guild that this command should be created in
     */
    String value();
}
