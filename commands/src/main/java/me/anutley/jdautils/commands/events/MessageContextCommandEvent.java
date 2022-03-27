package me.anutley.jdautils.commands.events;

import me.anutley.jdautils.commands.application.context.MessageContextCommand;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

public class MessageContextCommandEvent extends CommandEvent<MessageContextInteractionEvent, MessageContextCommand> {

    public MessageContextCommandEvent(MessageContextInteractionEvent event, MessageContextCommand command) {
        super(event, command);
    }

}
