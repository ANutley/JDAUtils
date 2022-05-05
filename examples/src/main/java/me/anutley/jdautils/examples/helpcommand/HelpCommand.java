package me.anutley.jdautils.examples.helpcommand;

import me.anutley.jdautils.commands.annotations.Command;
import me.anutley.jdautils.commands.events.TextCommandEvent;
import me.anutley.jdautils.commands.text.annotations.JDATextCommand;
import net.dv8tion.jda.api.EmbedBuilder;

@Command // The class must be annotated with this for the Command-Manager to pick it up
public class HelpCommand {

    @JDATextCommand(name = "help", description = "A help command", category = "utility")
    public void helpCommand(TextCommandEvent event) {

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Help Commands!")
                .setColor(0x008aff);

        for (me.anutley.jdautils.commands.Command<?, ?> command : HelpBot.getCommandManager().getCommands()) {
            String description = "`Description:` " + command.getDescription() + "\n`Category:` " + command.getCategory();
            builder.addField(command.getName(), description, false);
        }

        event.getDiscordEvent().getMessage().replyEmbeds(builder.build()).queue();
    }
}
