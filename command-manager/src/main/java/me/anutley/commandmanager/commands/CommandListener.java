package me.anutley.commandmanager.commands;

import me.anutley.commandmanager.CommandManager;
import me.anutley.commandmanager.commands.annotations.GuildOnly;
import me.anutley.commandmanager.commands.application.context.MessageContextCommand;
import me.anutley.commandmanager.commands.application.context.UserContextCommand;
import me.anutley.commandmanager.commands.application.slash.SlashCommand;
import me.anutley.commandmanager.commands.text.TextCommand;
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
            if (manager.getTextCommandManager().getNoCommandFoundConsumser() != null) {
                manager.getTextCommandManager().getNoCommandFoundConsumser().accept(event);
            }
            return;
        }

        if (manager.getTextCommandManager().getPermissionPredicate() != null)
            if (!manager.getTextCommandManager().getPermissionPredicate().test(event)) {
                if (manager.getTextCommandManager().getNoPermissionConsumer() != null)
                    manager.getTextCommandManager().getNoPermissionConsumer().accept(event);
                return;
            }

        if (command.getCommandMethod().isAnnotationPresent(GuildOnly.class) && !event.isFromGuild()) {
            if (manager.getTextCommandManager().getNotInGuildConsumer() != null)
                manager.getTextCommandManager().getNotInGuildConsumer().accept(event);
            return;
        }


        command.execute(event);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        SlashCommand slashCommand = manager.getSlashCommandManager().getCommandFromEvent(event);

        if (slashCommand == null) return;

        if (manager.getSlashCommandManager().getPermissionPredicate() != null)
            if (!manager.getSlashCommandManager().getPermissionPredicate().test(event)) {
                if (manager.getSlashCommandManager().getNoPermissionConsumer() != null)
                    manager.getSlashCommandManager().getNoPermissionConsumer().accept(event);
                return;
            }

        if (slashCommand.getCommandMethod().isAnnotationPresent(GuildOnly.class) && !event.isFromGuild()) {
            if (manager.getSlashCommandManager().getNotInGuildConsumer() != null)
                manager.getSlashCommandManager().getNotInGuildConsumer().accept(event);
            return;
        }

        slashCommand.execute(event);
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        UserContextCommand command = this.manager.getContextCommandManager().getUserCommandFromEvent(event);

        if (command == null) return;

        if (manager.getContextCommandManager().getPermissionPredicate() != null)
            if (!manager.getContextCommandManager().getPermissionPredicate().test(event)) {
                if (manager.getContextCommandManager().getNoPermissionConsumer() != null)
                    manager.getContextCommandManager().getNoPermissionConsumer().accept(event);
                return;
            }

        if (command.getCommandMethod().isAnnotationPresent(GuildOnly.class) && !event.isFromGuild()) {
            if (manager.getContextCommandManager().getNotInGuildConsumer() != null)
                manager.getContextCommandManager().getNotInGuildConsumer().accept(event);
            return;
        }

        command.execute(event);
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        MessageContextCommand command = this.manager.getContextCommandManager().getMessageCommandFromEvent(event);

        if (command == null) return;
        
        if (manager.getContextCommandManager().getPermissionPredicate() != null)
            if (!manager.getContextCommandManager().getPermissionPredicate().test(event)) {
                if (manager.getContextCommandManager().getNoPermissionConsumer() != null)
                    manager.getContextCommandManager().getNoPermissionConsumer().accept(event);
                return;
            }

        if (command.getCommandMethod().isAnnotationPresent(GuildOnly.class) && !event.isFromGuild()) {
            if (manager.getContextCommandManager().getNotInGuildConsumer() != null)
                manager.getContextCommandManager().getNotInGuildConsumer().accept(event);
            return;
        }


        command.execute(event);
    }
}
