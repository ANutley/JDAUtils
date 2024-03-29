package me.anutley.jdautils.commands.application.context;

import me.anutley.jdautils.commands.Command;
import me.anutley.jdautils.commands.annotations.CommandMeta;
import me.anutley.jdautils.commands.application.context.annotations.JDAMessageContextCommand;
import me.anutley.jdautils.commands.events.MessageContextCommandEvent;

import java.lang.reflect.Method;
import java.util.HashMap;

public class MessageContextCommand extends Command<JDAMessageContextCommand, MessageContextCommandEvent> {

    public <T> MessageContextCommand(JDAMessageContextCommand command, Method commandMethod, T instance) {
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
    public void execute(MessageContextCommandEvent event) {
        try {
            getMethod().invoke(getInstance(), event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
