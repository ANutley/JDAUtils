package me.anutley.jdautils.commands.text;

import me.anutley.jdautils.commands.Command;
import me.anutley.jdautils.commands.annotations.CommandMeta;
import me.anutley.jdautils.commands.events.TextCommandEvent;
import me.anutley.jdautils.commands.text.annotations.JDATextCommand;

import java.lang.reflect.Method;
import java.util.HashMap;

public class TextCommand extends Command<JDATextCommand, TextCommandEvent> {

    private boolean usedMentionAsPrefix;

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

    /**
     * @return Whether a mention was used as a prefix, rather than the original prefix
     */
    public boolean getUsedMentionAsPrefix() {
        return usedMentionAsPrefix;
    }

    /**
     An internal method only used to determine what args are sent to the user via the event. This should not be modified by the user
     @return Itself for chaining convenience
     */
    public TextCommand setUsedMentionAsPrefix(boolean usedMentionAsPrefix) {
        this.usedMentionAsPrefix = usedMentionAsPrefix;
        return this;
    }

    @Override
    public void execute(TextCommandEvent event) {
        try {
            getMethod().invoke(Class.forName(getMethod().getDeclaringClass().getName()).getConstructor().newInstance(),
                    event
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
