package me.anutley.jdautils.examples.pingpong;

import me.anutley.jdautils.commands.commands.annotations.Command;
import me.anutley.jdautils.commands.commands.text.annotations.JDATextCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Command // The class must be annotated with this for the Command-Manager to pick it up
public class PingPongCommand {

    @JDATextCommand(name = "ping", description = "Simple ping-pong command")
    // Sets the name and description of the command and registers it
    public void pingPongTextCommand(MessageReceivedEvent event) {
        event.getMessage().reply("Pong!").queue();
    }
}
