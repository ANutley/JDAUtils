package me.anutley.jdautils.commands;

import me.anutley.jdautils.commands.application.annotations.GuildCommand;
import me.anutley.jdautils.commands.application.context.MessageContextCommand;
import me.anutley.jdautils.commands.application.context.UserContextCommand;
import me.anutley.jdautils.commands.application.context.annotations.JDAMessageContextCommand;
import me.anutley.jdautils.commands.application.context.annotations.JDAUserContextCommand;
import me.anutley.jdautils.commands.application.slash.SlashCommandData;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ContextCommandManager {

    private final List<MessageContextCommand> messageContextCommands = new ArrayList<>();
    private final List<UserContextCommand> userContextCommands = new ArrayList<>();
    private Consumer<GenericContextInteractionEvent<?>> notInGuildConsumer;
    private Predicate<GenericContextInteractionEvent<?>> permissionPredicate;
    private Consumer<GenericContextInteractionEvent<?>> noPermissionConsumer;

    /**
     * @param commandManager An instance of the command manager, used to retrieve things such as the commands package
     */
    public ContextCommandManager(CommandManager commandManager) {

        for (Class<?> clazz : commandManager.getCommandClasses()) {
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(JDAMessageContextCommand.class)) {
                    messageContextCommands.add(new MessageContextCommand(method.getAnnotation(JDAMessageContextCommand.class), method));
                } else if (method.isAnnotationPresent(JDAUserContextCommand.class)) {
                    userContextCommands.add(new UserContextCommand(method.getAnnotation(JDAUserContextCommand.class), method));
                }
            }
        }
    }

    /**
     * @return The consumer which will be accepted if a command is guild-only, and the command is not ran in a Guild
     */
    public Consumer<GenericContextInteractionEvent<?>> getNotInGuildConsumer() {
        return notInGuildConsumer;
    }

    /**
     * @param notInGuildConsumer The consumer which should be accepted if a command is guild-only, and the command is not ran in a Guild
     * @return itself for chaining convenience
     */
    public ContextCommandManager setNotInGuildConsumer(Consumer<GenericContextInteractionEvent<?>> notInGuildConsumer) {
        this.notInGuildConsumer = notInGuildConsumer;
        return this;
    }

    /**
     * @return The predicate which will be tested before a command is run
     */
    public Predicate<GenericContextInteractionEvent<?>> getPermissionPredicate() {
        return permissionPredicate;
    }

    /**
     * @param permissionPredicate The predicate which should be tested before a command is run
     * @return itself for chaining convenience
     */
    public ContextCommandManager setPermissionPredicate(Predicate<GenericContextInteractionEvent<?>> permissionPredicate) {
        this.permissionPredicate = permissionPredicate;
        return this;
    }

    /**
     * @return The predicate which will be tested before a command is run
     */
    public Consumer<GenericContextInteractionEvent<?>> getNoPermissionConsumer() {
        return noPermissionConsumer;
    }

    /**
     * @param noPermissionConsumer The predicate which should be tested before a command is run
     * @return itself for chaining convenience
     */
    public ContextCommandManager setNoPermissionConsumer(Consumer<GenericContextInteractionEvent<?>> noPermissionConsumer) {
        this.noPermissionConsumer = noPermissionConsumer;
        return this;
    }

    /**
     * @return Returns a list of {@link SlashCommandData} which contains all the command data
     */
    public List<SlashCommandData> getCommandData() {
        List<SlashCommandData> commandData = new ArrayList<>();

        for (MessageContextCommand messageContextCommand : messageContextCommands) {

            String guildId = null;

            if (messageContextCommand.getMethod().isAnnotationPresent(GuildCommand.class))
                guildId = messageContextCommand.getMethod().getAnnotation(GuildCommand.class).value();

            CommandData data = Commands.message(messageContextCommand.getAnnotation().name());

            if (guildId == null) commandData.add(new SlashCommandData(guildId, data));
            else commandData.add(new SlashCommandData(guildId, data));
        }

        for (UserContextCommand userContextCommand : userContextCommands) {

            String guildId = null;

            if (userContextCommand.getMethod().isAnnotationPresent(GuildCommand.class))
                guildId = userContextCommand.getMethod().getAnnotation(GuildCommand.class).value();

            CommandData data = Commands.user(userContextCommand.getAnnotation().name());

            if (guildId == null) commandData.add(new SlashCommandData(guildId, data));
            else commandData.add(new SlashCommandData(guildId, data));
        }

        return commandData;
    }


    /**
     * @param event used to get the command name, which then searches through the list of {@link MessageContextCommand}s and finds the correct command
     * @return The message context command for the event
     */
    public MessageContextCommand getMessageCommandFromEvent(MessageContextInteractionEvent event) {
        return this.messageContextCommands.stream().filter(messageContextCommand -> messageContextCommand.getAnnotation().name().equals(event.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * @param event used to get the command name, which then searches through the list of {@link UserContextCommand}s and finds the correct command
     * @return The user context command for the event
     */
    public UserContextCommand getUserCommandFromEvent(UserContextInteractionEvent event) {
        return this.userContextCommands.stream().filter(userContextCommand -> userContextCommand.getAnnotation().name().equals(event.getName()))
                .findFirst()
                .orElse(null);
    }
}
