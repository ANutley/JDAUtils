package me.anutley.jdautils.commands.application.slash.annotations;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.Command.Option;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An annotation to represent a {@link Option} when registering commands
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SlashOption {

    /**
     * 1-32 characters
     *
     * @return the name of the option
     */
    String name();

    /**
     * 1-100 characters
     *
     * @return the description of the option
     */
    String description();

    /**
     * @return the option type of the option
     */
    OptionType type();

    /**
     * @return whether this option is required
     */
    boolean required() default true;

    /**
     * @return the auto-generated choices which should be supplied to the command
     */
    SlashChoice[] choices() default {};

    /**
     * @return the channel types which should be allowed (This should only be used if the {@link SlashOption#type()} is set to {@link OptionType#CHANNEL}
     */
    ChannelType[] channelTypes() default {};

}
