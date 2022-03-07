package me.anutley.jdautils.commands.commands.application.slash;

import me.anutley.jdautils.commands.commands.application.slash.annotations.JDASlashCommand;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SlashCommand {

    private final JDASlashCommand command;
    private final Method commandMethod;

    /**
     * @param command       The annotation that the command method has, to retrieve information such as the name or description
     * @param commandMethod The method which correlates to this command
     */
    public SlashCommand(JDASlashCommand command, Method commandMethod) {
        this.command = command;
        this.commandMethod = commandMethod;
    }

    /**
     * @return the annotation that the command method has
     */
    public JDASlashCommand getCommand() {
        return command;
    }

    /**
     * @return the method which correlates to this command
     */
    public Method getCommandMethod() {
        return commandMethod;
    }

    /**
     * This method is used to invoke the command method with the correct arguments
     *
     * @param event The event which should be used to invoke the command method
     */
    public void execute(SlashCommandInteractionEvent event) {

        LinkedList<SlashCommandOption> options = new LinkedList<>(SlashCommandOption.getOptions(this));

        ArrayList<Object> objects = new ArrayList<>();
        objects.add(event);

        for (SlashCommandOption slashOption : options) {

            Object object = null;
            switch (slashOption.getOption().type()) {
                case STRING:
                    object = event.getOption(slashOption.getOption().name()).getAsString();
                    break;
                case INTEGER:
                    object = event.getOption(slashOption.getOption().name()).getAsLong();
                    break;
                case BOOLEAN:
                    object = event.getOption(slashOption.getOption().name()).getAsBoolean();
                    break;
                case USER:
                    if (slashOption.getOptionParameter().getType().equals(User.class))
                        object = event.getOption(slashOption.getOption().name()).getAsUser();
                    if (slashOption.getOptionParameter().getType().equals(Member.class))
                        object = event.getOption(slashOption.getOption().name()).getAsMember();
                    break;
                case ROLE:
                    object = event.getOption(slashOption.getOption().name()).getAsRole();
                    break;
                case MENTIONABLE:
                    object = event.getOption(slashOption.getOption().name()).getAsMentionable();
                    break;

                case CHANNEL: {
                    if (slashOption.getOptionParameter().getType().equals(TextChannel.class))
                        object = event.getOption(slashOption.getOption().name()).getAsTextChannel();

                    if (slashOption.getOptionParameter().getType().equals(GuildMessageChannel.class))
                        object = event.getOption(slashOption.getOption().name()).getAsGuildChannel();

                    if (slashOption.getOptionParameter().getType().equals(TextChannel.class))
                        object = event.getOption(slashOption.getOption().name()).getAsTextChannel();

                    if (slashOption.getOptionParameter().getType().equals(NewsChannel.class))
                        object = event.getOption(slashOption.getOption().name()).getAsNewsChannel();

                    if (slashOption.getOptionParameter().getType().equals(ThreadChannel.class))
                        object = event.getOption(slashOption.getOption().name()).getAsThreadChannel();

                    if (slashOption.getOptionParameter().getType().equals(VoiceChannel.class))
                        object = event.getOption(slashOption.getOption().name()).getAsVoiceChannel();

                    if (slashOption.getOptionParameter().getType().equals(StageChannel.class))
                        object = event.getOption(slashOption.getOption().name()).getAsStageChannel();

                }
            }

            objects.add(event.getOption(slashOption.getOption().name()) != null ? object : null);
        }
        try {
            commandMethod.invoke(Class.forName(commandMethod.getDeclaringClass().getName()).getConstructor().newInstance(), objects.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<String> getAllBaseCommands(List<SlashCommand> commands) {
        List<String> baseCommands = new ArrayList<>();

        for (SlashCommand command : commands) {
            String name = command.getCommand().name().split("/")[0];
            if (!baseCommands.contains(name))
                baseCommands.add(name);
        }
        return baseCommands;
    }

    public static List<SlashCommand> getCommandsFromBase(List<SlashCommand> commands, String base) {
        List<SlashCommand> slashCommands = new ArrayList<>();

        for (SlashCommand command : commands) {
            if (command.getCommand().name().split("/")[0].equals(base)) {
                slashCommands.add(command);
            }
        }

        return slashCommands;
    }
}
