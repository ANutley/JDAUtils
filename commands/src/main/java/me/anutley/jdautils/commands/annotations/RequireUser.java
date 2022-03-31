package me.anutley.jdautils.commands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotate commands with this to indicate the user running this command needs to have one of the following ids
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireUser {

    /**
     * @return The id(s) of the user(s) which are required to run this command
     */
    String[] value();
}