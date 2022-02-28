package me.anutley.examples.helpcommand;

import me.anutley.commandmanager.commands.annotations.Command;
import me.anutley.commandmanager.commands.text.annotations.JDATextCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Command // The class must be annotated with this for the Command-Manager to pick it up
public class StockCommands {

    @JDATextCommand(name = "hello-world", description = "A simple hello world command!", category = "misc")
    public void helloWorldCommand(MessageReceivedEvent event) {
        event.getMessage().reply("hello world!").queue();
    }

    @JDATextCommand(name = "ping", description = "A simple ping command")
    public void pingCommand(MessageReceivedEvent event) {
        event.getMessage().reply("pong!").queue();
    }


    @JDATextCommand(name = "say", description = "Repeats what you say", category = "utility")
    public void sayCommand(MessageReceivedEvent event) {
        String args = event.getMessage().getContentRaw();

        event.getMessage().reply(args.substring(args.split(" ")[0].length())).queue();
    }
}
