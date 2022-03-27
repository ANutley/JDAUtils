package me.anutley.jdautils.commands.events;

import me.anutley.jdautils.commands.Command;
import net.dv8tion.jda.api.events.GenericEvent;

/**
 * The base event which all other command events extend
 * @param <E> The Discord event
 * @param <C> The command type
 */
public abstract class CommandEvent<E extends GenericEvent, C extends Command<?, ?>> {

    private final E discordEvent;
    private final C command;

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


}
