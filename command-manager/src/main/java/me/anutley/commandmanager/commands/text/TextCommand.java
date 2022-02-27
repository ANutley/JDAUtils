package me.anutley.commandmanager.commands.text;

import me.anutley.commandmanager.commands.text.annotations.JDATextCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.lang.reflect.Method;

public class TextCommand {

    private final JDATextCommand command;
    private final Method commandMethod;

    /**
     *
     * @param command The annotation that the command method has, to retrieve information such as the name or description
     * @param commandMethod The method which correlates to this command
     */
    public TextCommand(JDATextCommand command, Method commandMethod) {
        this.command = command;
        this.commandMethod = commandMethod;
    }

    /**
     *
     * @return the annotation that the command method has
     */
    public JDATextCommand getCommand() {
        return command;
    }

    /**
     *
     * @return the method which correlates to this command
     */
    public Method getCommandMethod() {
        return commandMethod;
    }

    /**
     * This method is used to invoke the command method with the correct arguments
     * @param event The event which should be used to invoke the command method
     */
    public void execute(MessageReceivedEvent event) {
        try {
            commandMethod.invoke(Class.forName(commandMethod.getDeclaringClass().getName()).getConstructor().newInstance(), event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
