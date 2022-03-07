package me.anutley.jdautils.commands.commands.application.slash;

import me.anutley.jdautils.commands.commands.application.slash.annotations.SlashOption;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SlashCommandOption {

    private final SlashOption option;
    private final Parameter optionParameter;

    /**
     * @param option          contains the option related data
     * @param optionParameter the parameter which is passed into the method
     */
    public SlashCommandOption(SlashOption option, Parameter optionParameter) {
        this.option = option;
        this.optionParameter = optionParameter;
    }

    /**
     * @return the option related data
     */
    public SlashOption getOption() {
        return option;
    }

    /**
     * @return the parameter which is passed into the method
     */
    public Parameter getOptionParameter() {
        return optionParameter;
    }

    /**
     * @param slashCommand The slash command to search for options in
     * @return a list of options retrieved from the {@link SlashCommand}
     */
    public static List<SlashCommandOption> getOptions(SlashCommand slashCommand) {
        return Arrays.stream(slashCommand.getCommandMethod().getParameters()).filter(parameter -> parameter.isAnnotationPresent(SlashOption.class))
                .map(parameter -> new SlashCommandOption(parameter.getAnnotation(SlashOption.class), parameter))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}