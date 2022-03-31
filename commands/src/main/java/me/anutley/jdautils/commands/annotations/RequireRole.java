package me.anutley.jdautils.commands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotate commands with this to indicate the user running the command needs to have <em> at least one</em> of the following roles
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    /**
     * @return The id(s) of the role(s) the user is required to have to run this command
     */
    String[] value();
}