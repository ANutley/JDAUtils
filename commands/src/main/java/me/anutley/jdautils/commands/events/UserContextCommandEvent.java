package me.anutley.jdautils.commands.events;

import me.anutley.jdautils.commands.application.context.UserContextCommand;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

public class UserContextCommandEvent extends CommandEvent<UserContextInteractionEvent, UserContextCommand> {

    public UserContextCommandEvent(UserContextInteractionEvent event, UserContextCommand command) {
        super(event, command);
    }

}
