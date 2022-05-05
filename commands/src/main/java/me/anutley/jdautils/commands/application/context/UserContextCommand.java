package me.anutley.jdautils.commands.application.context;

import me.anutley.jdautils.commands.Command;
import me.anutley.jdautils.commands.annotations.CommandMeta;
import me.anutley.jdautils.commands.application.context.annotations.JDAUserContextCommand;
import me.anutley.jdautils.commands.events.UserContextCommandEvent;

import java.lang.reflect.Method;
import java.util.HashMap;

public class UserContextCommand extends Command<JDAUserContextCommand, UserContextCommandEvent> {

    public <T> UserContextCommand(JDAUserContextCommand command, Method commandMethod, T instance) {
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
    public void execute(UserContextCommandEvent event) {
        try {
            getMethod().invoke(getInstance(), event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

