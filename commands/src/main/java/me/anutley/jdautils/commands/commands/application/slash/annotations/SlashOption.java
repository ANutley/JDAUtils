package me.anutley.jdautils.commands.commands.application.slash.annotations;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SlashOption {

    /**
     * @return the name of the option
     */
    String name();

    /**
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
