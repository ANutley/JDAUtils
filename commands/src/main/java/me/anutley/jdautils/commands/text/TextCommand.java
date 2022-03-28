package me.anutley.jdautils.commands.text;

import me.anutley.jdautils.commands.Command;
import me.anutley.jdautils.commands.annotations.CommandMeta;
import me.anutley.jdautils.commands.events.TextCommandEvent;
import me.anutley.jdautils.commands.text.annotations.JDATextCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.lang.reflect.Method;
import java.util.HashMap;

public class TextCommand extends Command<JDATextCommand, MessageReceivedEvent> {

    public TextCommand(JDATextCommand command, Method commandMethod) {
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
    public void execute(MessageReceivedEvent event) {
        try {
            getMethod().invoke(Class.forName(getMethod().getDeclaringClass().getName()).getConstructor().newInstance(),
                    new TextCommandEvent(
                            event,
                            this
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
