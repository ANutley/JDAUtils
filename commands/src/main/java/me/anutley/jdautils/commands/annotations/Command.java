package me.anutley.jdautils.commands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Classes that contain commands MUST be annotated with the class to be properly picked up
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
}
