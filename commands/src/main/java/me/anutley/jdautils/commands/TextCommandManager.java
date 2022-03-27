package me.anutley.jdautils.commands;

import me.anutley.jdautils.commands.text.TextCommand;
import me.anutley.jdautils.commands.text.annotations.JDATextCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TextCommandManager {

    private String prefix = "!"; // Default prefix
    private final Map<String, String> guildPrefix = new HashMap<>();
    private final List<TextCommand> commands = new ArrayList<>();
    private Consumer<MessageReceivedEvent> noCommandFoundConsumser;
    private Consumer<MessageReceivedEvent> notInGuildConsumer;
    private Predicate<MessageReceivedEvent> permissionPredicate;
    private Consumer<MessageReceivedEvent> noPermissionConsumer;

    /**
     * @param commandManager An instance of the command manager, used to retrieve things such as the commands package
     */
    public TextCommandManager(CommandManager commandManager) {

        for (Class<?> clazz : commandManager.getCommandClasses()) {
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(JDATextCommand.class)) {
                    commands.add(new TextCommand(method.getAnnotation(JDATextCommand.class), method));
                }
            }
        }
    }

    /**
     * @return the prefix that is used for text-based commands
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix The prefix that should be set for text-based commands
     * @return itself for chaining convenience
     */
    public TextCommandManager setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * @param guildId the id of the guild to get the prefix of
     * @return the guild's prefix
     */
    public String getGuildsPrefix(String guildId) {
        return guildPrefix.get(guildId);
    }

    /**
     * @param guild the guild to get the prefix of
     * @return the guild's prefix
     */
    public String getGuildsPrefix(Guild guild) {
        return getGuildsPrefix(guild.getId());
    }

    /**
     * @param guildId the id of the guild to set the prefix in
     * @param prefix  the new prefix the guild should have
     * @return itself for chaining convenience
     */
    public TextCommandManager setGuildPrefix(String guildId, String prefix) {
        if (guildPrefix.get(guildId) != null) guildPrefix.replace(guildId, prefix);
        else guildPrefix.put(guildId, prefix);
        return this;
    }

    /**
     * @param guild  the guild to set the prefix in
     * @param prefix the new prefix the guild should have
     * @return itself for chaining convenience
     */
    public TextCommandManager setGuildPrefix(Guild guild, String prefix) {
        setGuildPrefix(guild.getId(), prefix);
        return this;
    }

    /**
     * @return a list of all the registered text-based commands
     */
    public List<TextCommand> getCommands() {
        return commands;
    }


    /**
     * @return The consumer which will be accepted if no command is found
     */
    public Consumer<MessageReceivedEvent> getNoCommandFoundConsumser() {
        return noCommandFoundConsumser;
    }

    /**
     * @param consumer The consumer which should be accepted if no command is found
     * @return itself for chaining convenience
     */
    public TextCommandManager setNoCommandFoundConsumer(Consumer<MessageReceivedEvent> consumer) {
        this.noCommandFoundConsumser = consumer;
        return this;
    }

    /**
     * @return The consumer which will be accepted if a command is guild-only, and the command is not ran in a Guild
     */
    public Consumer<MessageReceivedEvent> getNotInGuildConsumer() {
        return notInGuildConsumer;
    }

    /**
     * @param notInGuildConsumer The consumer which should be accepted if a command is guild-only, and the command is not ran in a Guild
     * @return itself for chaining convenience
     */
    public TextCommandManager setNotInGuildConsumer(Consumer<MessageReceivedEvent> notInGuildConsumer) {
        this.notInGuildConsumer = notInGuildConsumer;
        return this;
    }

    /**
     * @return The predicate which will be tested before a command is run
     */
    public Predicate<MessageReceivedEvent> getPermissionPredicate() {
        return permissionPredicate;
    }

    /**
     * @param permissionPredicate The predicate which should be tested before a command is run
     * @return itself for chaining convenience
     */
    public TextCommandManager setPermissionPredicate(Predicate<MessageReceivedEvent> permissionPredicate) {
        this.permissionPredicate = permissionPredicate;
        return this;
    }

    /**
     * @return The predicate which will be tested before a command is run
     */
    public Consumer<MessageReceivedEvent> getNoPermissionConsumer() {
        return noPermissionConsumer;
    }

    /**
     * @param noPermissionConsumer The predicate which should be tested before a command is run
     * @return itself for chaining convenience
     */
    public TextCommandManager setNoPermissionConsumer(Consumer<MessageReceivedEvent> noPermissionConsumer) {
        this.noPermissionConsumer = noPermissionConsumer;
        return this;
    }


    /**
     * @return the text-command which has been found
     */
    public TextCommand getCommandFromEvent(MessageReceivedEvent event) {

        String[] args = event.getMessage().getContentRaw().split(" ");
        String prefix = this.getPrefix();
        if (event.isFromGuild()) {
            if (this.getGuildsPrefix(event.getGuild()) != null)
                prefix = this.getGuildsPrefix(event.getGuild());
        }

        for (TextCommand command : this.getCommands()) {

            if (!args[0].equals(prefix + command.getAnnotation().name())) continue;

            return command;
        }
        return null;
    }
}
