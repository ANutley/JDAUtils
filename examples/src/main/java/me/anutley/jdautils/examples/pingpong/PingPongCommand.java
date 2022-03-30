package me.anutley.jdautils.examples.pingpong;

import me.anutley.jdautils.commands.annotations.Command;
import me.anutley.jdautils.commands.events.TextCommandEvent;
import me.anutley.jdautils.commands.text.annotations.JDATextCommand;

@Command // The class must be annotated with this for the Command-Manager to pick it up
public class PingPongCommand {

    // Sets the name and description of the command and registers it
    @JDATextCommand(name = "ping", description = "Simple ping-pong command")
    public void pingPongTextCommand(TextCommandEvent event) {
        event.getDiscordEvent().getMessage().reply("pong").queue();
    }
}
