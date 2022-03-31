package me.anutley.jdautils.commands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequireGuild {

    /**
     * @return The id(s) of the guild(s) which are required to run this command in
     */
    String[] value();
}