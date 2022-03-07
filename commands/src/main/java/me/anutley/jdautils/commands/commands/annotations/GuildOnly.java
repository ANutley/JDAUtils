package me.anutley.jdautils.commands.commands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotate command methods with this to indicate that they can only be used in Guilds
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GuildOnly {
}
