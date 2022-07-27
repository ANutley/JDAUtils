package me.anutley.jdautils.commands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotate commands with this to indicate the user running this command needs to have one of the following ids
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireUser {

    /**
     * @return The list of ids that must contain the id of the user attempting to run the command to give them permission to run it
     */
    String[] value();
}