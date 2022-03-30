package me.anutley.jdautils.commands;

import me.anutley.jdautils.commands.annotations.GuildOnly;
import me.anutley.jdautils.commands.annotations.NSFW;
import me.anutley.jdautils.commands.application.context.MessageContextCommand;
import me.anutley.jdautils.commands.application.context.UserContextCommand;
import me.anutley.jdautils.commands.application.slash.SlashCommand;
import me.anutley.jdautils.commands.events.*;
import me.anutley.jdautils.commands.text.TextCommand;
import net.dv8tion.jda.api.entities.BaseGuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandListener extends ListenerAdapter {

    private final CommandManager manager;

    public CommandListener(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        TextCommand command = manager.getTextCommandManager().getCommandFromEvent(event);

        if (command == null) {
            if (manager.getTextCommandManager().getNoCommandFoundConsumer() != null) {
                manager.getTextCommandManager().getNoCommandFoundConsumer().accept(event);
            }
            return;
        }

        String messageContent = event.getMessage().getContentRaw().trim();
        String[] args = messageContent.split(" ");

        // Determine which args to pass on to the event
        if (command.getUsedMentionAsPrefix()) {
            // Add 1 to account for the space between the command and the first argument
            messageContent = messageContent.substring(args[0].length() + args[1].length() + 1);
        } else {
            messageContent = messageContent.substring(args[0].length());
        }

        args = messageContent.replaceAll("\\s\\s+", " ").trim().split(" "); // Remove extra whitespace and split into an array
        TextCommandEvent textCommandEvent = new TextCommandEvent(event, command, args);

        if (!check(textCommandEvent)) return;

        command.execute(textCommandEvent);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        SlashCommand slashCommand = manager.getSlashCommandManager().getCommandFromEvent(event);
        SlashCommandEvent slashCommandEvent = new SlashCommandEvent(event, slashCommand);

        if (slashCommand == null) return;
        if (!check(slashCommandEvent)) return;

        slashCommand.execute(slashCommandEvent);
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        UserContextCommand command = this.manager.getContextCommandManager().getUserCommandFromEvent(event);
        UserContextCommandEvent contextEvent = new UserContextCommandEvent(event, command);

        if (command == null) return;
        if (!check(contextEvent)) return;


        command.execute(contextEvent);
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        MessageContextCommand command = this.manager.getContextCommandManager().getMessageCommandFromEvent(event);
        MessageContextCommandEvent contextEvent = new MessageContextCommandEvent(event, command);

        if (command == null) return;
        if (!check(contextEvent)) return;

        command.execute(contextEvent);
    }


    private boolean check(CommandEvent<?, ?> event) {
        if (manager.getPermissionPredicate() != null)
            if (!manager.getPermissionPredicate().test(event)) {
                if (manager.getNoPermissionConsumer() != null)
                    manager.getNoPermissionConsumer().accept(event);
                return false;
            }

        if (event.getCommand().getMethod().isAnnotationPresent(GuildOnly.class) && !event.isFromGuild()) {
            if (manager.getNotInGuildConsumer() != null)
                manager.getNotInGuildConsumer().accept(event);
            return false;
        }

        if (event.getCommand().getMethod().isAnnotationPresent(NSFW.class)) {
            if (event.getMessageChannel() instanceof BaseGuildMessageChannel) {
                if (!((BaseGuildMessageChannel) event.getMessageChannel()).isNSFW())
                    if (manager.getNotInNSFWChannelConsumer() != null) {
                        manager.getNotInNSFWChannelConsumer().accept(event);
                    }
                return false;
            }
        }

        return true;
    }

}
