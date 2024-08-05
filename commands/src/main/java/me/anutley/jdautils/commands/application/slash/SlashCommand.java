package me.anutley.jdautils.commands.application.slash;

import me.anutley.jdautils.commands.Command;
import me.anutley.jdautils.commands.annotations.CommandMeta;
import me.anutley.jdautils.commands.application.slash.annotations.JDASlashCommand;
import me.anutley.jdautils.commands.events.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SlashCommand extends Command<JDASlashCommand, SlashCommandEvent> {

    public <T> SlashCommand(JDASlashCommand command, Method commandMethod, T instance) {
        super(command, commandMethod, instance);
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
    public void execute(SlashCommandEvent event) {

        LinkedList<SlashCommandOption> options = new LinkedList<>(SlashCommandOption.getOptions(this));

        ArrayList<Object> objects = new ArrayList<>();
        objects.add(event);

        SlashCommandInteractionEvent discordEvent = event.getDiscordEvent();

        for (SlashCommandOption slashOption : options) {

            Object object = null;
            OptionMapping optionMapping = discordEvent.getOption(slashOption.getOption().name());
            Class<?> optionClassType = slashOption.getOptionParameter().getType();

            switch (slashOption.getOption().type()) {
                case STRING:
                    object = optionMapping.getAsString();
                    break;

                case INTEGER:
                    object = optionMapping.getAsLong();
                    break;

                case BOOLEAN:
                    object = optionMapping.getAsBoolean();
                    break;

                case USER:
                    if (optionClassType.equals(User.class)) object = optionMapping.getAsUser();
                    if (optionClassType.equals(Member.class)) object = optionMapping.getAsMember();
                    break;

                case ROLE:
                    object = optionMapping.getAsRole();
                    break;

                case MENTIONABLE:
                    object = optionMapping.getAsMentionable();
                    break;

                case CHANNEL: {

                    GuildChannelUnion channelUnion = optionMapping.getAsChannel();

                    if (optionClassType.equals(Category.class)) object = channelUnion.asCategory();
                    if (optionClassType.equals(TextChannel.class)) object = channelUnion.asTextChannel();
                    if (optionClassType.equals(GuildMessageChannel.class))
                        object = channelUnion.asGuildMessageChannel();
                    if (optionClassType.equals(NewsChannel.class)) object = channelUnion.asNewsChannel();
                    if (optionClassType.equals(ThreadChannel.class)) object = channelUnion.asThreadChannel();
                    if (optionClassType.equals(VoiceChannel.class)) object = channelUnion.asVoiceChannel();
                    if (optionClassType.equals(StageChannel.class)) object = channelUnion.asStageChannel();

                    break;
                }

                case ATTACHMENT:
                    object = optionMapping.getAsAttachment();
            }

            objects.add(discordEvent.getOption(slashOption.getOption().name()) != null ? object : null);
        }
        try {
            getMethod().invoke(getInstance(), objects.toArray());
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
     *
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
