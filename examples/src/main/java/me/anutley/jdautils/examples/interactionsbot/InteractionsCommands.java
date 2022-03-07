package me.anutley.jdautils.examples.interactionsbot;

import me.anutley.jdautils.commands.commands.annotations.Command;
import me.anutley.jdautils.commands.commands.application.annotations.GuildCommand;
import me.anutley.jdautils.commands.commands.application.context.annotations.JDAMessageContextCommand;
import me.anutley.jdautils.commands.commands.application.context.annotations.JDAUserContextCommand;
import me.anutley.jdautils.commands.commands.application.slash.annotations.JDASlashCommand;
import me.anutley.jdautils.commands.commands.application.slash.annotations.SlashOption;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.TimeFormat;

@Command // The class must be annotated with this for the Command-Manager to pick it up
public class InteractionsCommands extends ListenerAdapter {

    /*
    The @GuildCommand annotation is used here to register this as a guild-specific command. This is only done here as global interactions
    take around 1 hour to be registered everywhere by Discord. If you want to create a global command, omit this annotation.
     */
    @GuildCommand(guildId = "833042350850441216")
    @JDASlashCommand(name = "avatar", description = "Get the avatar of a user!")
    public void slashCommand(
            SlashCommandInteractionEvent event,
            @SlashOption(name = "user", description = "The user you want to find the avatar of", type = OptionType.USER) User user
    ) {
        event.reply(user.getEffectiveAvatarUrl()).queue();
    }

    @GuildCommand(guildId = "833042350850441216")
    @JDAUserContextCommand(name = "User Info", description = "Gets some basic user related information")
    public void userContextCommand(UserContextInteractionEvent event) {
        User user = event.getTarget();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(user.getName() + " Info!")
                .setColor(0x008aff)
                .setThumbnail(user.getEffectiveAvatarUrl())
                .addField("Id", user.getId(), false)
                .addField("Name", user.getName(), false)
                .addField("Discriminator", user.getDiscriminator(), false)
                .addField("Is User Bot", String.valueOf(user.isBot()), false);

        event.replyEmbeds(embedBuilder.build()).queue();
    }

    @GuildCommand(guildId = "833042350850441216")
    @JDAMessageContextCommand(name = "Message Info", description = "Gets some basic message related information")
    public void messageContextCommand(MessageContextInteractionEvent event) {
        Message message = event.getTarget();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Message Info!")
                .setColor(0x008aff)
                .addField("Id", message.getId(), false)
                .addField("Author", message.getAuthor().getName(), false)
                .addField("Created Timestamp", TimeFormat.DATE_TIME_SHORT.format(message.getTimeCreated()), true)
                .addField("Is Pinned", String.valueOf(message.isPinned()), false)
                .addField("Is Webhook Message", String.valueOf(message.isWebhookMessage()), false);

        event.replyEmbeds(embedBuilder.build()).queue();
    }


}
