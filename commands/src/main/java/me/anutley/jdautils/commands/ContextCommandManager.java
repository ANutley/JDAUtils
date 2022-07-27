package me.anutley.jdautils.commands;

import me.anutley.jdautils.commands.application.ApplicationCommandData;
import me.anutley.jdautils.commands.application.annotations.GuildCommand;
import me.anutley.jdautils.commands.application.context.MessageContextCommand;
import me.anutley.jdautils.commands.application.context.UserContextCommand;
import me.anutley.jdautils.commands.application.context.annotations.JDAMessageContextCommand;
import me.anutley.jdautils.commands.application.context.annotations.JDAUserContextCommand;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;

public class ContextCommandManager {

    private final CommandManager commandManager;
    private final List<MessageContextCommand> messageContextCommands;
    private final List<UserContextCommand> userContextCommands;

    public ContextCommandManager(
            CommandManager commandManager,
            List<MessageContextCommand> messageContextCommands,
            List<UserContextCommand> userContextCommands
    ) {
        this.commandManager = commandManager;
        this.messageContextCommands = messageContextCommands;
        this.userContextCommands = userContextCommands;
    }

    /**
     * @return The base command manager
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * @return A list of the {@link MessageContextCommand}s have been registered
     */
    public List<MessageContextCommand> getMessageContextCommands() {
        return messageContextCommands;
    }

    /**
     * @return A list of the {@link UserContextCommand}s
     */
    public List<UserContextCommand> getUserContextCommands() {
        return userContextCommands;
    }

    /**
     * @return Returns a list of {@link ApplicationCommandData} which contains all the command data
     */
    public List<ApplicationCommandData> getCommandData() {
        List<ApplicationCommandData> commandData = new ArrayList<>();

        List<Command<?, ?>> allContextCommands = new ArrayList<Command<?, ?>>() {{
            addAll(userContextCommands);
            addAll(messageContextCommands);
        }};

        for (Command<?, ?> command : allContextCommands) {

            String guildId = null;

            if (command.getMethod().isAnnotationPresent(GuildCommand.class))
                guildId = command.getMethod().getAnnotation(GuildCommand.class).value();

            if (commandManager.getTestingGuildId() != null) guildId = commandManager.getTestingGuildId();

            CommandData data = null;
            if (command instanceof MessageContextCommand) data = Commands.message(command.getName());
            if (command instanceof UserContextCommand) data = Commands.user(command.getName());

            commandData.add(new ApplicationCommandData(guildId, data));
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

    public static class Builder {

        /**
         * @param commandManager The base command manager
         * @return The built command manager
         */
        protected ContextCommandManager build(CommandManager commandManager) {

            List<Command<?, ?>> genericMessageContextCommands = new ArrayList<>(commandManager.getCommandsByType(JDAMessageContextCommand.class));

            List<MessageContextCommand> messageContextCommands = new ArrayList<MessageContextCommand>() {{
                for (me.anutley.jdautils.commands.Command<?, ?> command : genericMessageContextCommands)
                    add((MessageContextCommand) command);
            }};


            List<Command<?, ?>> genericUserContextCommands = new ArrayList<>(commandManager.getCommandsByType(JDAUserContextCommand.class));

            List<UserContextCommand> userContextCommands = new ArrayList<UserContextCommand>() {{
                for (me.anutley.jdautils.commands.Command<?, ?> command : genericUserContextCommands)
                    add((UserContextCommand) command);
            }};

            return new ContextCommandManager(
                    commandManager,
                    messageContextCommands,
                    userContextCommands
            );
        }
    }

}
