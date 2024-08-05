package me.anutley.jdautils.commands;

import me.anutley.jdautils.commands.annotations.*;
import me.anutley.jdautils.commands.application.context.MessageContextCommand;
import me.anutley.jdautils.commands.application.context.UserContextCommand;
import me.anutley.jdautils.commands.application.slash.SlashCommand;
import me.anutley.jdautils.commands.events.*;
import me.anutley.jdautils.commands.text.TextCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

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
        // Check whether the user has the correct permissions
        if (manager.getPermissionPredicate() != null)
            if (!manager.getPermissionPredicate().test(event)) {
                if (manager.getNoPermissionConsumer() != null)
                    manager.getNoPermissionConsumer().accept(event);
                return false;
            }

        // Check whether this command is guild only, and if it's been run in an guild
        if (event.getCommand().getMethod().isAnnotationPresent(GuildOnly.class) && !event.isFromGuild()) {
            if (manager.getNotInGuildConsumer() != null)
                manager.getNotInGuildConsumer().accept(event);
            return false;
        }

        // Check whether this command is NSFW only, and if it's been run in an NSFW channel
        if (event.getCommand().getMethod().isAnnotationPresent(NSFW.class)) {
            if (event.getMessageChannel() instanceof StandardGuildMessageChannel) {
                if (!((StandardGuildMessageChannel) event.getMessageChannel()).isNSFW())
                    if (manager.getNotInNSFWChannelConsumer() != null) {
                        manager.getNotInNSFWChannelConsumer().accept(event);
                    }
                return false;
            }
        }

        // Check the various requirements needed to run the command, and whether they have been met
        if (event.getCommand().getMethod().isAnnotationPresent(RequireUser.class)) {
            String[] userIds = event.getCommand().getMethod().getAnnotation(RequireUser.class).value();
            if (!Arrays.asList(userIds).contains(event.getUser().getId())) {
                acceptRequirementConsumer(event, RequireUser.class);
                return false;
            }
        }

        if (event.getCommand().getMethod().isAnnotationPresent(RequireRole.class)) {
            String[] roleIds = event.getCommand().getMethod().getAnnotation(RequireRole.class).value();

            boolean isAllowed = false;
            for (String roleId : roleIds) {
                if (event.getMember() == null) break;
                if (event.getMember().getRoles().stream().anyMatch(r -> r.getId().equals(roleId)))
                    isAllowed = true;
            }

            if (!isAllowed) {
                acceptRequirementConsumer(event, RequireRole.class);
                return false;
            }
        }

        if (event.getCommand().getMethod().isAnnotationPresent(RequireChannel.class)) {
            String[] channelIds = event.getCommand().getMethod().getAnnotation(RequireChannel.class).value();
            if (!Arrays.asList(channelIds).contains(event.getMessageChannel().getId())) {
                acceptRequirementConsumer(event, RequireChannel.class);
                return false;
            }
        }

        if (event.getCommand().getMethod().isAnnotationPresent(RequireGuild.class)) {
            String[] guildIds = event.getCommand().getMethod().getAnnotation(RequireGuild.class).value();
            if (event.getGuild() == null || !Arrays.asList(guildIds).contains(event.getGuild().getId())) {
                acceptRequirementConsumer(event, RequireGuild.class);
                return false;
            }
        }

        // Check whether the bot has the permissions required
        if (event.getCommand().getMethod().isAnnotationPresent(BotPermission.class)) {
            Permission permission = event.getCommand().getMethod().getAnnotation(BotPermission.class).value();
            if (event.getGuild() != null && !event.getGuild().getSelfMember().hasPermission(permission)) {
                if (manager.getBotMissingPermissionConsumer() != null)
                    manager.getBotMissingPermissionConsumer().accept(event, permission);
                return false;
            }
        }
        return true;
    }

    private void acceptRequirementConsumer(CommandEvent<?, ?> event, Class<?> requirementClass) {
        if (manager.getDoesNotMeetRequirementsConsumer() != null)
            manager.getDoesNotMeetRequirementsConsumer().accept(event, requirementClass);
    }
}
