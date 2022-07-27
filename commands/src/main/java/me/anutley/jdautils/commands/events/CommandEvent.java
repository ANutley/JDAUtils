package me.anutley.jdautils.commands.events;

import me.anutley.jdautils.commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import org.jetbrains.annotations.Nullable;

/**
 * The base event which all other command events extend
 * @param <E> The Discord event
 * @param <C> The command type
 */
public abstract class CommandEvent<E extends GenericEvent, C extends Command<?, ?>> {

    private final E discordEvent;
    private final C command;

    /**
     * @param discordEvent The Discord event
     * @param command The command type
     */
    public CommandEvent(E discordEvent, C command) {
        this.discordEvent = discordEvent;
        this.command = command;
    }

    /**
     * @return the Discord event from this event
     */
    public E getDiscordEvent() {
        return discordEvent;
    }

    /**
     * @return the command that has been run
     */
    public C getCommand() {
        return command;
    }

    /**
     * @return The user who ran the command
     */
    public abstract User getUser();

    /**
     * @return The (potentially null) member who ran the command
     */
    public abstract @Nullable Member getMember();

    /**
     * @return The (potentially null) guild where the command was run
     */
    public abstract @Nullable Guild getGuild();

    /**
     * @return The message channel in which this command was run
     */
    public abstract MessageChannel getMessageChannel();

    /**
     * This will <em>always</em> return null for {@link TextCommandEvent}s
     * @return The interaction relating to this command
     */
    public abstract CommandInteraction getInteraction();

    /**
     * @return Whether this command was ran from a guild or not
     */
    public abstract boolean isFromGuild();



}
