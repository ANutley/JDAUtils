package me.anutley.jdautils.commands.application.context;

import me.anutley.jdautils.commands.Command;
import me.anutley.jdautils.commands.annotations.CommandMeta;
import me.anutley.jdautils.commands.application.context.annotations.JDAUserContextCommand;
import me.anutley.jdautils.commands.events.UserContextCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

import java.lang.reflect.Method;
import java.util.HashMap;

public class UserContextCommand extends Command<JDAUserContextCommand, UserContextInteractionEvent> {

    public UserContextCommand(JDAUserContextCommand command, Method commandMethod) {
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
    public void execute(UserContextInteractionEvent event) {
        try {
            getMethod().invoke(Class.forName(getMethod().getDeclaringClass().getName()).getConstructor().newInstance(),
                    new UserContextCommandEvent(event, this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

