package me.anutley.jdautils.commands.events;

import me.anutley.jdautils.commands.text.TextCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TextCommandEvent extends CommandEvent<MessageReceivedEvent, TextCommand> {

    public TextCommandEvent(MessageReceivedEvent event, TextCommand command) {
        super(event, command);
    }

}
