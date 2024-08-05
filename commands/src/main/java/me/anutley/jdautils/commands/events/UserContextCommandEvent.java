package me.anutley.jdautils.commands.events;

import me.anutley.jdautils.commands.application.context.UserContextCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import org.jetbrains.annotations.Nullable;

public class UserContextCommandEvent extends CommandEvent<UserContextInteractionEvent, UserContextCommand> {

    public UserContextCommandEvent(UserContextInteractionEvent event, UserContextCommand command) {
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
        return getDiscordEvent().getMessageChannel();
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
