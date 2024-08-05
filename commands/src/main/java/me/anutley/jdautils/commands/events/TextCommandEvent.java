package me.anutley.jdautils.commands.events;

import me.anutley.jdautils.commands.text.TextCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import org.jetbrains.annotations.Nullable;

public class TextCommandEvent extends CommandEvent<MessageReceivedEvent, TextCommand> {

    private final String[] args;
    public TextCommandEvent(MessageReceivedEvent event, TextCommand command, String[] args) {
        super(event, command);
        this.args = args;
    }

    @Override
    public User getUser() {
        return getDiscordEvent().getAuthor();
    }

    @Override
    public @Nullable Member getMember() {
        return getDiscordEvent().getMember();
    }

    @Override
    public @Nullable Guild getGuild() {
        return getDiscordEvent().getGuild();
    }

    @Override
    public MessageChannel getMessageChannel() {
        return getDiscordEvent().getChannel();
    }

    @Override
    public CommandInteraction getInteraction() {
        return null;
    }

    @Override
    public boolean isFromGuild() {
        return getDiscordEvent().isFromGuild();
    }

    /**
     * @return The args that sent when running this command. It determines the correct args regardless of whether the mention or normal prefix was used
     */
    public String[] getArgs() {
        return args;
    }

}
