package me.anutley.jdautils.commands.events;

import me.anutley.jdautils.commands.application.context.MessageContextCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import org.jetbrains.annotations.Nullable;

public class MessageContextCommandEvent extends CommandEvent<MessageContextInteractionEvent, MessageContextCommand> {

    public MessageContextCommandEvent(MessageContextInteractionEvent event, MessageContextCommand command) {
        super(event, command);
    }

    @Override
    public User getUser() {
        return getDiscordEvent().getUser();
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
        return getDiscordEvent().getInteraction();
    }

    @Override
    public boolean isFromGuild() {
        return getDiscordEvent().isFromGuild();
    }

}
