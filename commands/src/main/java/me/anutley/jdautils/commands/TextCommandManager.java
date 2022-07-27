package me.anutley.jdautils.commands;

import me.anutley.jdautils.commands.text.TextCommand;
import me.anutley.jdautils.commands.text.annotations.JDATextCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This is a manager for various <em> text based command</em> settings. Although JDAUtils does provide functionality for text based commands, slash commands are recommended to be used instead
 */
public class TextCommandManager {

    private final CommandManager commandManager;
    private final List<TextCommand> commands;
    private final String defaultPrefix;
    private final Map<String, String> guildPrefixes;
    private final Consumer<MessageReceivedEvent> noCommandFoundConsumer;
    private final boolean allowMentionAsPrefix;

    public TextCommandManager(
            CommandManager commandManager,
            List<TextCommand> commands,
            String defaultPrefix,
            Map<String, String> guildPrefixes,
            Consumer<MessageReceivedEvent> noCommandFoundConsumer,
            boolean allowMentionAsPrefix
    ) {
        this.commandManager = commandManager;
        this.commands = commands;
        this.defaultPrefix = defaultPrefix;
        this.guildPrefixes = guildPrefixes;
        this.noCommandFoundConsumer = noCommandFoundConsumer;
        this.allowMentionAsPrefix = allowMentionAsPrefix;
    }

    /**
     * @return The base command manager
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * @return A list of all {@link TextCommand}s that have been registered
     */
    public List<TextCommand> getCommands() {
        return commands;
    }

    /**
     * @return The default text-based command prefix
     */
    public String getDefaultPrefix() {
        return defaultPrefix;
    }

    /**
     * @return A hashmap containing the guild-specific prefixes
     */
    public Map<String, String> getGuildPrefixes() {
        return guildPrefixes;
    }

    /**
     * @param guildId the id of the guild to get the prefix of
     * @return the guild's prefix
     */
    public String getGuildsPrefix(String guildId) {
        return guildPrefixes.get(guildId);
    }

    /**
     * @param guild the guild to get the prefix of
     * @return the guild's prefix
     */
    public String getGuildsPrefix(Guild guild) {
        return getGuildsPrefix(guild.getId());
    }

    /**
     * @return The consumer which will be accepted when no command is found
     */
    public Consumer<MessageReceivedEvent> getNoCommandFoundConsumer() {
        return noCommandFoundConsumer;
    }


    /**
     * @return Whether mentions are allowed to be used instead of a normal prefix
     */
    public boolean isAllowMentionAsPrefix() {
        return allowMentionAsPrefix;
    }

    /**
     * @return the text-command which has been found
     */
    public TextCommand getCommandFromEvent(MessageReceivedEvent event) {

        String[] args = event.getMessage().getContentRaw().split(" ");
        String prefix = this.getDefaultPrefix();
        if (event.isFromGuild()) {
            if (this.getGuildsPrefix(event.getGuild()) != null)
                prefix = this.getGuildsPrefix(event.getGuild());
        }

        for (TextCommand command : this.getCommands()) {

            if (args[0].equals(prefix + command.getAnnotation().name())) {
                return command.setUsedMentionAsPrefix(false);
            }

            if (this.isAllowMentionAsPrefix()) {
                if (args[0].matches("<@(!)?" + event.getJDA().getSelfUser().getId() + ">") && args[1].equals(command.getName())) {
                    return command.setUsedMentionAsPrefix(true);
                }
            }

        }
        return null;
    }


    public static class Builder {

        private String defaultPrefix = "!";
        private Map<String, String> guildPrefixes = new HashMap<>();
        private Consumer<MessageReceivedEvent> noCommandFoundConsumer;
        private boolean allowMentionAsPrefix = false;


        /**
         * Please keep in mind, this prefix is volatile, and do need to be re-set after a bot restart
         *
         * @param defaultPrefix the default prefix which is used for text based commands
         * @return itself for chaining convenience
         */
        public Builder setDefaultPrefix(String defaultPrefix) {
            this.defaultPrefix = defaultPrefix;
            return this;
        }

        /**
         * Please keep in mind, these prefixes are volatile, and do need to be re-set after a bot restart
         *
         * @param guildId the id of the guild to set the prefix in
         * @param prefix  the new prefix the guild should have
         * @return itself for chaining convenience
         */
        public Builder setGuildPrefix(String guildId, String prefix) {
            if (guildPrefixes.get(guildId) != null) guildPrefixes.replace(guildId, prefix);
            else guildPrefixes.put(guildId, prefix);
            return this;
        }

        /**
         * Please keep in mind, these prefixes are volatile, and do need to be re-set after a bot restart
         *
         * @param guild  the guild to set the prefix in
         * @param prefix the new prefix the guild should have
         * @return itself for chaining convenience
         */
        public Builder setGuildPrefix(Guild guild, String prefix) {
            setGuildPrefix(guild.getId(), prefix);
            return this;
        }

        /**
         * Please be warned, this will <em>override</em> all of your old guild prefixes
         * Also, please keep in mind, these prefixes are volatile, and do need to be re-set after a bot restart
         *
         * @param guildPrefixes the map of guild prefixes to set
         * @return itself for chaining convenience
         */
        public Builder setGuildPrefixes(Map<String, String> guildPrefixes) {
            this.guildPrefixes = guildPrefixes;
            return this;
        }

        /**
         * @param noCommandFoundConsumer the consumer which will be accepted if no command is found with the name that was inputted
         * @return itself for chaining convenience
         */
        public Builder setNoCommandFoundConsumer(Consumer<MessageReceivedEvent> noCommandFoundConsumer) {
            this.noCommandFoundConsumer = noCommandFoundConsumer;
            return this;
        }

        /**
         * @param allowMentionAsPrefix whether mentions can be used instead of prefixes
         * @return itself for chaining convenience
         */
        public Builder setAllowMentionAsPrefix(boolean allowMentionAsPrefix) {
            this.allowMentionAsPrefix = allowMentionAsPrefix;
            return this;
        }

        public TextCommandManager build(CommandManager commandManager) {
            List<Command<?, ?>> commands = new ArrayList<>(commandManager.getCommandsByType(JDATextCommand.class));
            List<TextCommand> textCommands = new ArrayList<TextCommand>() {{
               for (Command<?, ?> command : commands)
                   add((TextCommand) command);
            }};


            return new TextCommandManager(
                    commandManager,
                    textCommands,
                    defaultPrefix,
                    guildPrefixes,
                    noCommandFoundConsumer,
                    allowMentionAsPrefix
            );
        }
    }


}
