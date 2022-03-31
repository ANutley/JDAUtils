package me.anutley.jdautils.commands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A simple key/value system to provide information that isn't provided by default.
 * For example a meta key "platform" with the value of "discord" or "minecraft".
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandMeta {

    String key();

    String value();
}
