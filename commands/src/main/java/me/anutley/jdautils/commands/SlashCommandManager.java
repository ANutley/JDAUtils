package me.anutley.jdautils.commands;

import me.anutley.jdautils.commands.application.annotations.GuildCommand;
import me.anutley.jdautils.commands.application.slash.SlashCommand;
import me.anutley.jdautils.commands.application.slash.SlashCommandData;
import me.anutley.jdautils.commands.application.slash.SlashCommandOption;
import me.anutley.jdautils.commands.application.slash.annotations.JDASlashCommand;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SlashCommandManager {

    private final CommandManager commandManager;
    private final List<SlashCommand> commands = new ArrayList<>();
    private Consumer<SlashCommandInteractionEvent> notInGuildConsumer;
    private Predicate<SlashCommandInteractionEvent> permissionPredicate;
    private Consumer<SlashCommandInteractionEvent> noPermissionConsumer;

    /**
     * @param commandManager An instance of the command manager, used to retrieve things such as the commands package
     */
    public SlashCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;

        for (Class<?> clazz : commandManager.getCommandClasses()) {
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(JDASlashCommand.class)) {
                    commands.add(new SlashCommand(method.getAnnotation(JDASlashCommand.class), method));
                }
            }
        }
    }

    /**
     * @return The consumer which will be accepted if a command is guild-only, and the command is not ran in a Guild
     */
    public Consumer<SlashCommandInteractionEvent> getNotInGuildConsumer() {
        return notInGuildConsumer;
    }

    /**
     * @param notInGuildConsumer The consumer which should be accepted if a command is guild-only, and the command is not ran in a Guild
     * @return itself for chaining convenience
     */
    public SlashCommandManager setNotInGuildConsumer(Consumer<SlashCommandInteractionEvent> notInGuildConsumer) {
        this.notInGuildConsumer = notInGuildConsumer;
        return this;
    }

    /**
     * @return The predicate which will be tested before a command is run
     */
    public Predicate<SlashCommandInteractionEvent> getPermissionPredicate() {
        return permissionPredicate;
    }

    /**
     * @param permissionPredicate The predicate which should be tested before a command is run
     * @return itself for chaining convenience
     */
    public SlashCommandManager setPermissionPredicate(Predicate<SlashCommandInteractionEvent> permissionPredicate) {
        this.permissionPredicate = permissionPredicate;
        return this;
    }

    /**
     * @return The predicate which will be tested before a command is run
     */
    public Consumer<SlashCommandInteractionEvent> getNoPermissionConsumer() {
        return noPermissionConsumer;
    }

    /**
     * @param noPermissionConsumer The predicate which should be tested before a command is run
     * @return itself for chaining convenience
     */
    public SlashCommandManager setNoPermissionConsumer(Consumer<SlashCommandInteractionEvent> noPermissionConsumer) {
        this.noPermissionConsumer = noPermissionConsumer;
        return this;
    }


    /**
     * @return Returns a list of {@link me.anutley.jdautils.commands.application.slash.SlashCommandData} which contains all the command data
     */
    public List<me.anutley.jdautils.commands.application.slash.SlashCommandData> getCommandData() {
        List<me.anutley.jdautils.commands.application.slash.SlashCommandData> commandData = new ArrayList<>();

        for (String base : SlashCommand.getAllBaseCommands(commands)) {

            List<SubcommandGroupData> subcommandGroupDataList = new ArrayList<>();
            List<SubcommandData> subcommandDataMap = new ArrayList<>();
            LinkedList<OptionData> optionDataList = new LinkedList<>();

            String description = "No base command description";
            String guildId = null;

            for (SlashCommand slashCommand : SlashCommand.getCommandsFromBase(commands, base)) {
                if (slashCommand.getMethod().isAnnotationPresent(GuildCommand.class))
                    guildId = slashCommand.getMethod().getAnnotation(GuildCommand.class).value();

                LinkedList<SlashCommandOption> options = new LinkedList<>(SlashCommandOption.getOptions(slashCommand));

                for (SlashCommandOption option : options) {
                    OptionData optionData = new OptionData(option.getOption().type(), option.getOption().name(), option.getOption().description(), option.getOption().required());

                    if (option.getOption().type().equals(OptionType.STRING) || option.getOption().type().equals(OptionType.INTEGER))
                        if (option.getOption().choices().length != 0)
                            optionData.addChoices(Arrays.stream(option.getOption().choices())
                                    .map(slashChoice -> new Command.Choice(slashChoice.name(), slashChoice.value()))
                                    .collect(Collectors.toList()));


                    if (option.getOption().type().equals(OptionType.CHANNEL))
                        if (option.getOption().channelTypes().length != 0)
                            optionData.setChannelTypes(Arrays.stream(option.getOption().channelTypes())
                                    .filter(channelType -> channelType != ChannelType.UNKNOWN)
                                    .collect(Collectors.toList()));

                    optionDataList.add(optionData);
                }

                if (!slashCommand.getAnnotation().baseDescription().equals("No base command description"))
                    description = slashCommand.getAnnotation().baseDescription();


                String[] nameArgs = slashCommand.getAnnotation().name().split("/");

                if (nameArgs.length == 1) {
                    description = slashCommand.getAnnotation().description();
                }

                if (nameArgs.length == 2) {
                    if (subcommandDataMap.stream().anyMatch(subcommandData -> subcommandData.getName().equals(nameArgs[0] + "/" + nameArgs[1])))
                        continue;
                    subcommandDataMap.add(new SubcommandData(nameArgs[1], slashCommand.getAnnotation().description()).addOptions(optionDataList));
                }

                if (nameArgs.length == 3) {

                    if (subcommandGroupDataList.stream().noneMatch(subcommandGroupData -> subcommandGroupData.getName().equals(nameArgs[1]))) {
                        subcommandGroupDataList.add(new SubcommandGroupData(nameArgs[1], slashCommand.getAnnotation().description()));
                    }

                    subcommandGroupDataList.stream().filter(groupData -> groupData.getName().equals(nameArgs[1]))
                            .findFirst().ifPresent(subcommandGroupData -> subcommandGroupData.addSubcommands(new SubcommandData(nameArgs[2], slashCommand.getAnnotation().description()).addOptions(optionDataList)));

                }

            }

            CommandData data;
            if (subcommandDataMap.isEmpty() && subcommandGroupDataList.isEmpty()) {
                data = (Commands.slash(base, description)
                        .addOptions(optionDataList));
            } else {
                data = (Commands.slash(base, description)
                        .addSubcommandGroups(subcommandGroupDataList)
                        .addSubcommands(subcommandDataMap));
            }

            if (guildId == null)
                commandData.add(new me.anutley.jdautils.commands.application.slash.SlashCommandData(guildId, data));
            else commandData.add(new SlashCommandData(guildId, data));

        }

        return commandData;
    }

    /**
     * @param event used to get the command path, which then searches through the list of {@link SlashCommand}s and finds the correct command
     * @return The slash command for the event
     */
    public SlashCommand getCommandFromEvent(SlashCommandInteractionEvent event) {

        return this.commands.stream().filter(slashCommand -> slashCommand.getAnnotation().name().equals(event.getCommandPath()))
                .findFirst()
                .orElse(null);
    }
}
