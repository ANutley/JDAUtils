package me.anutley.jdautils.commands;

import me.anutley.jdautils.commands.annotations.GuildOnly;
import me.anutley.jdautils.commands.application.context.MessageContextCommand;
import me.anutley.jdautils.commands.application.context.UserContextCommand;
import me.anutley.jdautils.commands.application.slash.SlashCommand;
import me.anutley.jdautils.commands.events.*;
import me.anutley.jdautils.commands.text.TextCommand;
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
        TextCommandEvent textCommandEvent = new TextCommandEvent(event, command);

        if (command == null) {
            if (manager.getTextCommandManager().getNoCommandFoundConsumer() != null) {
                manager.getTextCommandManager().getNoCommandFoundConsumer().accept(event);
            }
            return;
        }

        if (!check(textCommandEvent)) return;

        command.execute(event);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        SlashCommand slashCommand = manager.getSlashCommandManager().getCommandFromEvent(event);
        SlashCommandEvent slashCommandEvent = new SlashCommandEvent(event, slashCommand);

        if (slashCommand == null) return;
        if (!check(slashCommandEvent)) return;

        slashCommand.execute(event);
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        UserContextCommand command = this.manager.getContextCommandManager().getUserCommandFromEvent(event);
        UserContextCommandEvent contextEvent = new UserContextCommandEvent(event, command);

        if (command == null) return;
        if (!check(contextEvent)) return;


        command.execute(event);
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        MessageContextCommand command = this.manager.getContextCommandManager().getMessageCommandFromEvent(event);
        MessageContextCommandEvent contextEvent = new MessageContextCommandEvent(event, command);

        if (command == null) return;
        if (!check(contextEvent)) return;

        command.execute(event);
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

        return true;
    }

}
