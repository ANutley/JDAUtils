package me.anutley.jdautils.commands.annotations;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Mark command with this to indicate they need a certain bot permission to be able to run
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface BotPermission {

    /**
     * @return The permission the bot needs to perform this command
     */
    Permission value();

}
