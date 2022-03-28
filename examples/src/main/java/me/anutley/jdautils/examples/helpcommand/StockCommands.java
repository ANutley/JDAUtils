package me.anutley.jdautils.examples.helpcommand;

import me.anutley.jdautils.commands.annotations.Command;
import me.anutley.jdautils.commands.events.TextCommandEvent;
import me.anutley.jdautils.commands.text.annotations.JDATextCommand;

@Command // The class must be annotated with this for the Command-Manager to pick it up
public class StockCommands {

    @JDATextCommand(name = "hello-world", description = "A simple hello world command!", category = "misc")
    public void helloWorldCommand(TextCommandEvent event) {
        event.getDiscordEvent().getMessage().reply("hello world!").queue();
    }

    @JDATextCommand(name = "ping", description = "A simple ping command", category = "misc")
    public void pingCommand(TextCommandEvent event) {
        event.getDiscordEvent().getMessage().reply("pong!").queue();
    }


    @JDATextCommand(name = "say", description = "Repeats what you say", category = "utility")
    public void sayCommand(TextCommandEvent event) {
        String args = event.getDiscordEvent().getMessage().getContentRaw();

        event.getDiscordEvent().getMessage().reply(args.substring(args.split(" ")[0].length())).queue();
    }
}
