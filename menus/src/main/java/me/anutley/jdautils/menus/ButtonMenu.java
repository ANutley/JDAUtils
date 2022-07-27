package me.anutley.jdautils.menus;

import me.anutley.jdautils.eventwaiter.EventWaiter;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ButtonMenu extends Menu {

    protected final Message initialMessage;
    protected final Consumer<ButtonInteractionEvent> action;
    protected final List<ActionRow> actionRows;

    public ButtonMenu(EventWaiter eventWaiter, List<User> allowedUsers, List<Role> allowedRoles, long timeout, TimeUnit units, boolean recursive, boolean ephemeral, Message initialMessage, Consumer<ButtonInteractionEvent> action, List<ActionRow> actionRows) {
        super(eventWaiter, allowedUsers, allowedRoles, timeout, units, recursive, ephemeral);
        this.initialMessage = initialMessage;
        this.action = action;
        this.actionRows = actionRows;
    }

    @Override
    public void show(MessageChannel channel) {
        channel.sendMessage(
                new MessageBuilder(initialMessage)
                        .setActionRows(actionRows)
                        .build()
        ).queue(
                success -> waitForClick(success.getIdLong())
        );
    }

    @Override
    public void show(GenericCommandInteractionEvent event) {
        event.reply(
                new MessageBuilder(initialMessage)
                        .setActionRows(actionRows)
                        .build()
        ).setEphemeral(ephemeral).queue(
                success -> success.retrieveOriginal().queue(m -> waitForClick(m.getIdLong()))
        );
    }

    private void waitForClick(long messageId) {
        eventWaiter.wait(
                ButtonInteractionEvent.class,
                event -> {
                    action.accept(event);
                    if (recursive) waitForClick(messageId);
                },
                event -> isAllowed(event.getUser(), event.getGuild()) && event.getMessage().getIdLong() == messageId,
                getTimeRemainingInMs(),
                TimeUnit.MILLISECONDS,
                null
        );
    }

    public static class Builder extends Menu.Builder<Builder, ButtonMenu> {

        protected Message initialMessage = null;
        protected Consumer<ButtonInteractionEvent> action = null;
        protected List<ActionRow> actionRows = new ArrayList<>();

        @Override
        public ButtonMenu build() {

            if (eventWaiter == null) throw new IllegalStateException("The Event Waiter must be set!");
            if (actionRows.size() == 0) throw new IllegalStateException("There must be at least one action row");
            if (action == null) throw new IllegalStateException("There must be a callback action");
            if (initialMessage == null) throw new IllegalStateException("There must be an initial message");

            return new ButtonMenu(
                    super.eventWaiter,
                    super.allowedUsers,
                    super.allowedRoles,
                    super.timeout,
                    super.units,
                    super.recursive,
                    super.ephemeral,
                    initialMessage,
                    action,
                    actionRows
            );
        }

        /**
         * @param initialMessage Sets the initial message that should be sent with the components
         * @return Itself for chaining convenience
         */
        public Builder setInitialMessage(Message initialMessage) {
            this.initialMessage = initialMessage;
            return this;
        }

        /**
         * @param action The consumer that will be accepted after a button is clicked
         * @return Itself for chaining convenience
         */
        public Builder setAction(Consumer<ButtonInteractionEvent> action) {
            this.action = action;
            return this;
        }

        /**
         * @param actionRows Sets the action rows that are in the message
         * @return Itself for chaining convenience
         */
        public Builder setActionRows(ActionRow... actionRows) {
            this.actionRows = Arrays.asList(actionRows);
            return this;
        }

        /**
         * @param actionRows A vararg containing the action rows which should be added to the message
         * @return Itself for chaining convenience
         */
        public Builder addActionRows(ActionRow... actionRows) {
            this.actionRows.addAll(Arrays.asList(actionRows));
            return this;
        }
    }
}
