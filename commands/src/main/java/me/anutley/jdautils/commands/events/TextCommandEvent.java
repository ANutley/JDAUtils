package me.anutley.jdautils.commands.events;

import me.anutley.jdautils.commands.text.TextCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import org.jetbrains.annotations.Nullable;

public class TextCommandEvent extends CommandEvent<MessageReceivedEvent, TextCommand> {

    public TextCommandEvent(MessageReceivedEvent event, TextCommand command) {
        super(event, command);
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

}
