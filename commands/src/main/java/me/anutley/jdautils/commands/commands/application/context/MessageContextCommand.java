package me.anutley.jdautils.commands.commands.application.context;

import me.anutley.jdautils.commands.commands.application.context.annotations.JDAMessageContextCommand;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

import java.lang.reflect.Method;

public class MessageContextCommand {

    private final JDAMessageContextCommand command;
    private final Method commandMethod;

    /**
     * @param command       The annotation that the command method has, to retrieve information such as the name
     * @param commandMethod The method which correlates to this command
     */
    public MessageContextCommand(JDAMessageContextCommand command, Method commandMethod) {
        this.command = command;
        this.commandMethod = commandMethod;
    }

    /**
     * @return the annotation that the command method has
     */
    public JDAMessageContextCommand getCommand() {
        return command;
    }

    /**
     * @return the method which correlates to this command
     */
    public Method getCommandMethod() {
        return commandMethod;
    }

    /**
     * This method is used to invoke the command method with the correct arguments
     *
     * @param event The event which should be used to invoke the command method
     */
    public void execute(MessageContextInteractionEvent event) {
        try {
            commandMethod.invoke(Class.forName(commandMethod.getDeclaringClass().getName()).getConstructor().newInstance(), event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
