package me.anutley.jdautils.commands;

import me.anutley.jdautils.commands.application.ApplicationCommandData;
import me.anutley.jdautils.commands.application.annotations.GuildCommand;
import me.anutley.jdautils.commands.application.slash.SlashCommand;
import me.anutley.jdautils.commands.application.slash.SlashCommandOption;
import me.anutley.jdautils.commands.application.slash.annotations.JDASlashCommand;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SlashCommandManager {

    private final CommandManager commandManager;
    private final List<SlashCommand> commands;

    public SlashCommandManager(
            CommandManager commandManager,
            List<SlashCommand> commands
    ) {
        this.commandManager = commandManager;
        this.commands = commands;
    }

    /**
     * @return The base command manager
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * @return A list of all the {@link SlashCommand}s that have been registered
     */
    public List<SlashCommand> getCommands() {
        return commands;
    }

    /**
     * @return Returns a list of {@link ApplicationCommandData} which contains all the command data
     */
    public List<ApplicationCommandData> getCommandData() {
        List<ApplicationCommandData> commandData = new ArrayList<>();

        for (String base : SlashCommand.getAllBaseCommands(commands)) { // Get all the BASE commands that are registered

            List<SubcommandGroupData> subcommandGroupDataList = new ArrayList<>();
            List<SubcommandData> subcommandDataList = new ArrayList<>();
            LinkedList<OptionData> optionDataList = new LinkedList<>();

            String description = "No base command description";
            String guildId = null;

            for (SlashCommand slashCommand : SlashCommand.getCommandsFromBase(commands, base)) { // Get all the slash command that are registered with the specific base
                if (slashCommand.getMethod().isAnnotationPresent(GuildCommand.class))
                    // If it's a guild command, set the guild id, so we can register it guild-wide instead of globally
                    guildId = slashCommand.getMethod().getAnnotation(GuildCommand.class).value();

                if (commandManager.getTestingGuildId() != null) guildId = commandManager.getTestingGuildId();

                LinkedList<SlashCommandOption> options = new LinkedList<>(SlashCommandOption.getOptions(slashCommand));

                // An arraylist of options just for this specific command, this is done to ensure only the data for this command is added
                List<OptionData> optionList = new ArrayList<>();

                for (SlashCommandOption option : options) { // Loop through all the options
                    // Convert it to a JDA option data
                    OptionData optionData = new OptionData(option.getOption().type(), option.getOption().name(), option.getOption().description(), option.getOption().required());

                    // Set the pre-defined choices if they exist
                    if (option.getOption().type().equals(OptionType.STRING) || option.getOption().type().equals(OptionType.INTEGER))
                        if (option.getOption().choices().length != 0)
                            optionData.addChoices(Arrays.stream(option.getOption().choices())
                                    .map(slashChoice -> new Command.Choice(slashChoice.name(), slashChoice.value()))
                                    .collect(Collectors.toList()));


                    if (option.getOption().type().equals(OptionType.CHANNEL))
                        if (option.getOption().channelTypes().length != 0)
                            optionData.setChannelTypes(Arrays.stream(option.getOption().channelTypes())
                                    .filter(channelType -> channelType != ChannelType.UNKNOWN) // Filter out the ones which are default
                                    .collect(Collectors.toList()));

                    optionList.add(optionData);
                }

                if (!slashCommand.getAnnotation().baseDescription().equals("No base command description"))
                    description = slashCommand.getAnnotation().baseDescription();


                String[] nameArgs = slashCommand.getAnnotation().name().split("/");

                if (nameArgs.length == 1) { // It is just a single command
                    description = slashCommand.getAnnotation().description();
                }

                if (nameArgs.length == 2) { // It is a base command and a sub command
                    if (subcommandDataList.stream().anyMatch(subcommandData -> subcommandData.getName().equals(nameArgs[0] + "/" + nameArgs[1])))
                        continue; // If this subcommand already exists skip it, else add it
                    subcommandDataList.add(new SubcommandData(nameArgs[1], slashCommand.getAnnotation().description()).addOptions(optionList));
                }

                if (nameArgs.length == 3) { // It is a base command, a sub command group and a sub command

                    // If this subcommand group doesn't already exist, add it
                    if (subcommandGroupDataList.stream().noneMatch(subcommandGroupData -> subcommandGroupData.getName().equals(nameArgs[1]))) {
                        subcommandGroupDataList.add(new SubcommandGroupData(nameArgs[1], slashCommand.getAnnotation().description()));
                    }

                    // Search through the group data, if it exists, add the subcommand data
                    subcommandGroupDataList.stream().filter(groupData -> groupData.getName().equals(nameArgs[1]))
                            .findFirst().ifPresent(subcommandGroupData -> subcommandGroupData.addSubcommands(new SubcommandData(nameArgs[2], slashCommand.getAnnotation().description()).addOptions(optionList)));

                }

                optionDataList.addAll(optionList);
            }

            CommandData data;
            if (subcommandDataList.isEmpty() && subcommandGroupDataList.isEmpty()) { // It is just a single command
                data = (Commands.slash(base, description)
                        .addOptions(optionDataList));
            } else {
                data = (Commands.slash(base, description) // There are sub commands and/or sub command groups
                        .addSubcommandGroups(subcommandGroupDataList)
                        .addSubcommands(subcommandDataList));
            }

            commandData.add(new ApplicationCommandData(guildId, data));

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


    public static class Builder {

        /**
         * @param commandManager The base command manager
         * @return The built command manager
         */
        public SlashCommandManager build(CommandManager commandManager) {
            List<me.anutley.jdautils.commands.Command<?, ?>> commands = new ArrayList<>(commandManager.getCommandsByType(JDASlashCommand.class));

            List<SlashCommand> slashCommands = new ArrayList<SlashCommand>() {{
                for (me.anutley.jdautils.commands.Command<?, ?> command : commands)
                    add((SlashCommand) command);
            }};

            return new SlashCommandManager(
                    commandManager,
                    slashCommands
            );
        }
    }

}
