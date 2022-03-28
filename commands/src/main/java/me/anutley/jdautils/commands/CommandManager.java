package me.anutley.jdautils.commands;

import me.anutley.jdautils.commands.annotations.Command;
import me.anutley.jdautils.commands.annotations.GuildOnly;
import me.anutley.jdautils.commands.application.ApplicationCommandData;
import me.anutley.jdautils.commands.events.CommandEvent;
import me.anutley.jdautils.commands.utils.ReflectionsUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CommandManager {

    private JDA jda;
    private ShardManager shardManager;

    private final List<Class<?>> commandClasses;
    private final Predicate<CommandEvent<?, ?>> permissionPredicate;
    private final Consumer<CommandEvent<?, ?>> noPermissionConsumer;
    private final Consumer<CommandEvent<?, ?>> notInGuildConsumer;

    private final TextCommandManager textCommandManager;
    private final SlashCommandManager slashCommandManager;
    private final ContextCommandManager contextCommandManager;


    public CommandManager(JDA jda,
                          List<Class<?>> commandClasses,
                          Predicate<CommandEvent<?, ?>> permissionPredicate,
                          Consumer<CommandEvent<?, ?>> noPermissionConsumer,
                          Consumer<CommandEvent<?, ?>> notInGuildConsumer,
                          TextCommandManager.Builder textCommandManager,
                          SlashCommandManager.Builder slashCommandManager,
                          ContextCommandManager.Builder contextCommandManager

    ) {
        this.jda = jda;
        this.commandClasses = commandClasses;
        this.permissionPredicate = permissionPredicate;
        this.noPermissionConsumer = noPermissionConsumer;
        this.notInGuildConsumer = notInGuildConsumer;
        this.textCommandManager = textCommandManager.build(this);
        this.slashCommandManager = slashCommandManager.build(this);
        this.contextCommandManager = contextCommandManager.build(this);
    }

    public CommandManager(ShardManager shardManager,
                          List<Class<?>> commandClasses,
                          Predicate<CommandEvent<?, ?>> permissionPredicate,
                          Consumer<CommandEvent<?, ?>> noPermissionConsumer,
                          Consumer<CommandEvent<?, ?>> notInGuildConsumer,
                          TextCommandManager.Builder textCommandManager,
                          SlashCommandManager.Builder slashCommandManager,
                          ContextCommandManager.Builder contextCommandManager

    ) {
        this.shardManager = shardManager;
        this.commandClasses = commandClasses;
        this.permissionPredicate = permissionPredicate;
        this.noPermissionConsumer = noPermissionConsumer;
        this.notInGuildConsumer = notInGuildConsumer;
        this.textCommandManager = textCommandManager.build(this);
        this.slashCommandManager = slashCommandManager.build(this);
        this.contextCommandManager = contextCommandManager.build(this);
    }

    /**
     * This <em>WILL</em> return null if the builder was built using a {@link ShardManager} instance instead
     * @return The JDA instance
     */
    public JDA getJda() {
        return jda;
    }

    /**
     * This <em>WILL</em> return null if the builder was built using a {@link JDA} instance instead
     * @return The Shard Manager instance
     */
    public ShardManager getShardManager() {
        return shardManager;
    }

    /**
     * @return A list of all classes which commands are searched in
     */
    public List<Class<?>> getCommandClasses() {
        return commandClasses;
    }

    /**
     * @return The predicate which will be tested before running the command
     */
    public Predicate<CommandEvent<?, ?>> getPermissionPredicate() {
        return permissionPredicate;
    }

    /**
     * @return The consumer which will be accepted if {@link CommandManager#getPermissionPredicate()} returns false
     */
    public Consumer<CommandEvent<?, ?>> getNoPermissionConsumer() {
        return noPermissionConsumer;
    }

    /**
     * @return The consumer which will be accepted if the command is not ran in a guild, and it is {@link GuildOnly} command
     */
    public Consumer<CommandEvent<?, ?>> getNotInGuildConsumer() {
        return notInGuildConsumer;
    }

    /**
     * @return The text command manager
     */
    public TextCommandManager getTextCommandManager() {
        return textCommandManager;
    }

    /**
     * @return The slash command manager
     */
    public SlashCommandManager getSlashCommandManager() {
        return slashCommandManager;
    }

    /**
     * @return The context command manager
     */
    public ContextCommandManager getContextCommandManager() {
        return contextCommandManager;
    }

    /**
     * Registers all the interactions that you have created
     *
     * @return itself for chaining convenience
     */
    public CommandManager registerInteractions() {
        List<ApplicationCommandData> slashCommands = slashCommandManager.getCommandData();
        List<ApplicationCommandData> contextCommands = contextCommandManager.getCommandData();

        List<ApplicationCommandData> allCommands = new ArrayList<ApplicationCommandData>() {{
            addAll(slashCommands);
            addAll(contextCommands);
        }};

        List<ApplicationCommandData> globalCommands = new ArrayList<>();
        List<ApplicationCommandData> guildCommands = new ArrayList<>();


        for (ApplicationCommandData commandData : allCommands) {
            if (commandData.getGuildId() != null) guildCommands.add(commandData);
            else globalCommands.add(commandData);
        }


        List<CommandData> data = new ArrayList<>();
        globalCommands.forEach(applicationCommandData -> data.add(applicationCommandData.getCommandData()));

        if (!data.isEmpty()) {
            if (getJda() == null) {
                getShardManager().getShards().forEach(jda ->
                        jda.updateCommands().addCommands(data).queue()
                );
            } else getJda().updateCommands().addCommands(data).queue();
        }


        for (Map.Entry<String, List<CommandData>> entry : ApplicationCommandData.sortByGuildId(guildCommands).entrySet()) {

            Guild guild = getJda() == null ? getShardManager().getGuildById(entry.getKey()) : getJda().getGuildById(entry.getKey());
            if (guild == null) continue;

            guild.updateCommands().addCommands(entry.getValue()).queue();
        }
        return this;
    }


    public static class Builder {

        private final List<String> searchPaths = new ArrayList<>();
        private final List<Class<?>> commandClasses = new ArrayList<>();
        private Predicate<CommandEvent<?, ?>> permissionPredicate;
        private Consumer<CommandEvent<?, ?>> noPermissionConsumer;
        private Consumer<CommandEvent<?, ?>> notInGuildConsumer;

        private final TextCommandManager.Builder textCommandManager = new TextCommandManager.Builder();
        private final SlashCommandManager.Builder slashCommandManager = new SlashCommandManager.Builder();
        private final ContextCommandManager.Builder contextCommandManager = new ContextCommandManager.Builder();


        /**
         * JDAUtils uses reflections to search for classes, instead of adding classes manually (which can be done {@link Builder#addCommandClass(Class)})
         * you can specify a package name for JDAUtils to search in.
         * @param packageName The package name to search in for classes which contain commands
         * @return Itself for chaining convenience
         */
        public Builder addSearchPath(String packageName) {
            this.searchPaths.add(packageName);
            return this;
        }

        /**
         * Manually add a class for JDAUtils to search for commands in
         * @param commandClass The class to search in
         * @return Itself for chaining convenience
         */
        public Builder addCommandClass(Class<?> commandClass) {
            this.commandClasses.add(commandClass);
            return this;
        }

        /**
         * @param permissionPredicate The predicate to be tested before running the command
         * @return Itself for chaining convenience
         */
        public Builder setPermissionPredicate(Predicate<CommandEvent<?, ?>> permissionPredicate) {
            this.permissionPredicate = permissionPredicate;
            return this;
        }

        /**
         * @param noPermissionConsumer The consumer to be accepted if the {@link CommandManager#getPermissionPredicate()} returns false
         * @return Itself for chaining convenience
         */
        public Builder setNoPermissionConsumer(Consumer<CommandEvent<?, ?>> noPermissionConsumer) {
            this.noPermissionConsumer = noPermissionConsumer;
            return this;
        }

        /**
         * @param notInGuildConsumer  The consumer which will be accepted if the command is not ran in a guild, and it is {@link GuildOnly} command
         * @return Itself for chaining convenience
         */
        public Builder setNotInGuildConsumer(Consumer<CommandEvent<?, ?>> notInGuildConsumer) {
            this.notInGuildConsumer = notInGuildConsumer;
            return this;
        }

        /**
         * @param consumer The consumer which modifies the text command manager
         * @return Itself for chaining convenience
         */
        public Builder textCommandManager(Consumer<TextCommandManager.Builder> consumer) {
            consumer.accept(textCommandManager);
            return this;
        }

        /**
         * @param consumer The consumer which modifies the slash command manager
         * @return Itself for chaining convenience
         */
        public Builder slashCommandManager(Consumer<SlashCommandManager.Builder> consumer) {
            consumer.accept(slashCommandManager);
            return this;
        }

        /**
         * @param consumer The consumer which modifies the context command manager
         * @return Itself for chaining convenience
         */
        public Builder contextCommandManager(Consumer<ContextCommandManager.Builder> consumer) {
            consumer.accept(contextCommandManager);
            return this;
        }


        /**
         * @param jda The JDA instance which should be used to register event listeners
         * @return The built command manager instance
         */
        public CommandManager build(JDA jda) {
            List<Class<?>> classes = new ArrayList<>(commandClasses);

            for (String path : searchPaths) {
                classes.addAll(ReflectionsUtil.getClassesWithAnnotationsByPackage(path, Command.class));
            }

            if (!jda.getStatus().equals(JDA.Status.CONNECTED)) {
                throw new IllegalStateException("Your JDA instance needs to be ready before passing it to the CommandBuilder!");
            }

            CommandManager commandManager = new CommandManager(
                    jda,
                    classes,
                    permissionPredicate,
                    noPermissionConsumer,
                    notInGuildConsumer,
                    textCommandManager,
                    slashCommandManager,
                    contextCommandManager
            );

            jda.addEventListener(new CommandListener(commandManager)); // Add the command listener

            return commandManager;
        }

        /**
         * @param shardManager The Shard Manager instance which should be used to register event listeners
         * @return The built command manager instance
         */
        public CommandManager build(ShardManager shardManager) {
            List<Class<?>> classes = new ArrayList<>(commandClasses);

            for (String path : searchPaths) {
                classes.addAll(ReflectionsUtil.getClassesWithAnnotationsByPackage(path, Command.class));
            }

            if (!shardManager.getStatuses().values().stream().allMatch(status -> status.equals(JDA.Status.CONNECTED))) {
                throw new IllegalStateException("Your Shard Manager instance needs to be ready before passing it to the CommandBuilder!");
            }

            CommandManager commandManager = new CommandManager(
                    shardManager,
                    classes,
                    permissionPredicate,
                    noPermissionConsumer,
                    notInGuildConsumer,
                    textCommandManager,
                    slashCommandManager,
                    contextCommandManager
            );

            shardManager.addEventListener(new CommandListener(commandManager)); // Add the command listener

            return commandManager;
        }
    }

}
