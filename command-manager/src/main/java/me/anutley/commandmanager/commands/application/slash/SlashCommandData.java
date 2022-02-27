package me.anutley.commandmanager.commands.application.slash;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SlashCommandData {

    private final String guildId;
    private final CommandData commandData;

    /**
     * This is used to create {@link CommandData} which correlates to a specific guild. If this command is meant to be global
     * @param guildId guild id of the command data (set to null to specify the command data is supposed to be global)
     * @param commandData the command data of the application
     */
    public SlashCommandData(@Nullable String guildId, CommandData commandData) {
        this.guildId = guildId;
        this.commandData = commandData;
    }

    /**
     *
     * @return potentially null guild-id relating to the command data
     */
    public String getGuildId() {
        return guildId;
    }

    /**
     *
     * @return the command data of the application
     */
    public CommandData getCommandData() {
        return commandData;
    }

    /**
     *
     * @param commands a {@link List} of commands that should be sorted
     * @return A {@link HashMap} of commands, the key correlating to the guild id, and the {@link List} of command data correlating to all the command data related to that specific guild
     */
    public static HashMap<String, List<CommandData>> sortByGuildId(List<SlashCommandData> commands) {
        HashMap<String, List<CommandData>> sortedList = new HashMap<>();

        for (SlashCommandData command : commands) {
            if (sortedList.get(command.getGuildId()) == null) {
                sortedList.put(command.getGuildId(), new ArrayList<>() {{
                            add(command.getCommandData());
                        }}
                );
            }

            else {
                sortedList.get(command.getGuildId()).add(command.getCommandData());
            }
        }

        return sortedList;
    }
}
