package me.anutley.jdautils.commands.application.context;

import me.anutley.jdautils.commands.Command;
import me.anutley.jdautils.commands.annotations.CommandMeta;
import me.anutley.jdautils.commands.application.context.annotations.JDAMessageContextCommand;
import me.anutley.jdautils.commands.events.MessageContextCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

import java.lang.reflect.Method;
import java.util.HashMap;

public class MessageContextCommand extends Command<JDAMessageContextCommand, MessageContextInteractionEvent> {

    public MessageContextCommand(JDAMessageContextCommand command, Method commandMethod) {
        super(command, commandMethod);
    }

    @Override
    public HashMap<String, String> getMetaTags() {
        HashMap<String, String> metaTags = new HashMap<>();

        for (CommandMeta commandMeta : getAnnotation().meta())
            metaTags.put(commandMeta.key(), commandMeta.value());

        return metaTags;
    }

    @Override
    public void execute(MessageContextInteractionEvent event) {
        try {
            getMethod().invoke(Class.forName(getMethod().getDeclaringClass().getName()).getConstructor().newInstance(),
                    new MessageContextCommandEvent(event, this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
