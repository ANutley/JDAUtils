package me.anutley.examples.helpcommand;

import me.anutley.commandmanager.commands.text.TextCommand;
import me.anutley.commandmanager.commands.text.annotations.JDATextCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpCommand  {

    @JDATextCommand(name = "help", description = "A help command", category = "utility")
    public void helpCommand(MessageReceivedEvent event) {

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Help Commands!")
                .setColor(0x008aff);

        for (TextCommand textCommand : HelpBot.getCommandManager().getTextCommandManager().getCommands()) {
            String description = "`Description:` " + textCommand.getCommand().description() + "\n`Category:` " + textCommand.getCommand().category();
            builder.addField(textCommand.getCommand().name(), description, false);
        }

        event.getMessage().replyEmbeds(builder.build()).queue();
    }
}
