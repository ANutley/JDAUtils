package me.anutley.jdautils.commands.events;

import me.anutley.jdautils.commands.application.slash.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SlashCommandEvent extends CommandEvent<SlashCommandInteractionEvent, SlashCommand> {

    public SlashCommandEvent(SlashCommandInteractionEvent event, SlashCommand command) {
        super(event, command);
    }

}
