package me.anutley.examples.pingpong;

import me.anutley.commandmanager.commands.text.annotations.JDATextCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingPongCommand {

    @JDATextCommand(name = "ping", description = "Simple ping-pong command") // Sets the name and description of the command and registers it
    public void pingPongTextCommand(MessageReceivedEvent event) {
        event.getMessage().reply("Pong!").queue();
    }
}
