package me.anutley.jdautils.commands;

import me.anutley.jdautils.commands.commands.CommandListener;
import me.anutley.jdautils.commands.commands.annotations.Command;
import me.anutley.jdautils.commands.commands.application.slash.SlashCommandData;
import me.anutley.jdautils.commands.utils.ReflectionsUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CommandManager {

    private final JDA jda;
    private final String commandsPackage;
    private final List<Class<?>> commandClasses;
    private final TextCommandManager textCommandManager;
    private final SlashCommandManager slashCommandManager;
    private final ContextCommandManager contextCommandManager;


    public CommandManager(JDA jda, String commandsPackage) {
        this.jda = jda;
        if (!jda.getStatus().equals(JDA.Status.CONNECTED)) {
            throw new IllegalStateException("Your JDA instance needs to be ready before passing it to the CommandBuilder!");
        }

        this.jda.addEventListener(new CommandListener(this));

        this.commandsPackage = commandsPackage;
        commandClasses = ReflectionsUtil.getClassesWithAnnotationsByPackage(getCommandsPackage(), Command.class);
        this.textCommandManager = new TextCommandManager(this);
        this.slashCommandManager = new SlashCommandManager(this);
        this.contextCommandManager = new ContextCommandManager(this);
    }

    /**
     * @return The JDA instance which the command manager uses
     */
    public JDA getJda() {
        return jda;
    }

    /**
     * @return The location of the package which contains all the commands
     */
    public String getCommandsPackage() {
        return commandsPackage;
    }

    /**
     * @return The list of classes in the commands package
     */
    public List<Class<?>> getCommandClasses() {
        return commandClasses;
    }

    /**
     * @return The text command manager instance
     */
    public TextCommandManager getTextCommandManager() {
        return textCommandManager;
    }

    /**
     * @param consumer Allows you to modify the {@link TextCommandManager}
     * @return itself for chaining convenience
     */
    public CommandManager textCommandManager(Consumer<TextCommandManager> consumer) {
        consumer.accept(textCommandManager);

        return this;
    }

    /**
     * @return The slash command manager instance
     */
    public SlashCommandManager getSlashCommandManager() {
        return slashCommandManager;
    }

    /**
     * @param consumer Allows you to modify the {@link SlashCommandManager}
     * @return itself for chaining convenience
     */
    public CommandManager slashCommandManager(Consumer<SlashCommandManager> consumer) {
        consumer.accept(slashCommandManager);

        return this;
    }

    /**
     * @return The context command manager instance
     */
    public ContextCommandManager getContextCommandManager() {
        return contextCommandManager;
    }

    /**
     * @param consumer Allows you to modify the {@link ContextCommandManager}
     * @return itself for chaining convenience
     */
    public CommandManager contextCommandManager(Consumer<ContextCommandManager> consumer) {
        consumer.accept(contextCommandManager);

        return this;
    }

    /**
     * Registers all the interactions that you have created
     *
     * @return itself for chaining convenience
     */
    public CommandManager registerInteractions() {
        List<SlashCommandData> slashCommands = slashCommandManager.getCommandData();
        List<SlashCommandData> contextCommands = contextCommandManager.getCommandData();

        List<SlashCommandData> allCommands = new ArrayList<SlashCommandData>() {{
            addAll(slashCommands);
            addAll(contextCommands);
        }};

        List<SlashCommandData> globalCommands = new ArrayList<>();
        List<SlashCommandData> guildCommands = new ArrayList<>();


        for (SlashCommandData commandData : allCommands) {
            if (commandData.getGuildId() != null) guildCommands.add(commandData);
            else globalCommands.add(commandData);
        }


        List<CommandData> data = new ArrayList<>();
        globalCommands.forEach(slashCommandData -> data.add(slashCommandData.getCommandData()));

        if (!data.isEmpty()) {
            getJda().updateCommands().addCommands(data).queue();
        }


        for (Map.Entry<String, List<CommandData>> entry : SlashCommandData.sortByGuildId(guildCommands).entrySet()) {

            Guild guild = getJda().getGuildById(entry.getKey());
            if (guild == null) continue;

            guild.updateCommands().addCommands(entry.getValue()).queue();
        }
        return this;
    }

}
