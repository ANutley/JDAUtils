package me.anutley.jdautils.commands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Classes that contain commands MUST be annotated with this to be properly picked up by the reflections library
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
}
