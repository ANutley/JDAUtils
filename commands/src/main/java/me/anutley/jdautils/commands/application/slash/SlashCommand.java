package me.anutley.jdautils.commands.application.slash;

import me.anutley.jdautils.commands.Command;
import me.anutley.jdautils.commands.annotations.CommandMeta;
import me.anutley.jdautils.commands.application.slash.annotations.JDASlashCommand;
import me.anutley.jdautils.commands.events.SlashCommandEvent;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SlashCommand extends Command<JDASlashCommand, SlashCommandInteractionEvent> {

    public SlashCommand(JDASlashCommand command, Method commandMethod) {
        super(command, commandMethod);
    }

    @Override
    public String getName() {
        return getAnnotation().name();
    }

    @Override
    public String getDescription() {
        return getAnnotation().description();
    }

    @Override
    public String getCategory() {
        return getAnnotation().category();
    }

    @Override
    public String getUsage() {
        return getAnnotation().usage();
    }

    @Override
    public HashMap<String, String> getMetaTags() {
        HashMap<String, String> metaTags = new HashMap<>();

        for (CommandMeta commandMeta : getAnnotation().meta())
            metaTags.put(commandMeta.key(), commandMeta.value());

        return metaTags;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        LinkedList<SlashCommandOption> options = new LinkedList<>(SlashCommandOption.getOptions(this));

        ArrayList<Object> objects = new ArrayList<>();
        objects.add(new SlashCommandEvent(event, this));

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

                    break;
                }

                case ATTACHMENT:
                    object = event.getOption(slashOption.getOption().name()).getAsAttachment();
            }

            objects.add(event.getOption(slashOption.getOption().name()) != null ? object : null);
        }
        try {
            getMethod().invoke(Class.forName(getMethod().getDeclaringClass().getName()).getConstructor().newInstance(), objects.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This is a utility method used to return <em> just </em> the base commands
     * For example if the list contains commands with the paths '/foo bar1', '/foo bar2', '/foo bar3', '/bar foo', this method will return a list containing 'foo' and 'bar'
     *
     * @param commands - The commands to search through
     * @return - All the base commands the list contains
     */
    public static List<String> getAllBaseCommands(List<SlashCommand> commands) {
        List<String> baseCommands = new ArrayList<>();

        for (SlashCommand command : commands) {
            String name = command.getAnnotation().name().split("/")[0];
            if (!baseCommands.contains(name))
                baseCommands.add(name);
        }
        return baseCommands;
    }

    /**
     * This method returns the commands from the base, almost the opposite of {@link SlashCommand#getAllBaseCommands(List)}
     * It will return the {@link SlashCommand}'s which has the base command given
     * @param commands - The commands to search through
     * @param base     - The base that you want to search for
     * @return - A list of slash commands from the specific base command
     */
    public static List<SlashCommand> getCommandsFromBase(List<SlashCommand> commands, String base) {
        List<SlashCommand> slashCommands = new ArrayList<>();

        for (SlashCommand command : commands) {
            if (command.getAnnotation().name().split("/")[0].equals(base)) {
                slashCommands.add(command);
            }
        }

        return slashCommands;
    }
}
