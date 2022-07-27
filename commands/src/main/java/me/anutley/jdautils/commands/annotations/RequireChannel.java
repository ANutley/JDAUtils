package me.anutley.jdautils.commands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotate commands with this to indicate they need to be in one of the provided channels to be run
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireChannel {

    /**
     * @return The id(s) of the channel(s) which the command has to be run in
     */
    String[] value();

}