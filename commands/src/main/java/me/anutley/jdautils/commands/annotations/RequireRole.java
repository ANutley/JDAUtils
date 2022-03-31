package me.anutley.jdautils.commands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    /**
     * @return The id(s) of the role(s) the user is required to have to run this command
     */
    String[] value();
}