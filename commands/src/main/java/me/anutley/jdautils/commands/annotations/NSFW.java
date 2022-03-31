package me.anutley.jdautils.commands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotate command methods with this to indicate they can only be used in NSFW channels
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NSFW {
}
