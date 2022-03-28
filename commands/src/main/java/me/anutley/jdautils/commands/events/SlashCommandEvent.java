package me.anutley.jdautils.commands.events;

import me.anutley.jdautils.commands.application.slash.SlashCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import org.jetbrains.annotations.Nullable;

public class SlashCommandEvent extends CommandEvent<SlashCommandInteractionEvent, SlashCommand> {

    public SlashCommandEvent(SlashCommandInteractionEvent event, SlashCommand command) {
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
