package me.anutley.jdautils.menus;

import me.anutley.jdautils.eventwaiter.EventWaiter;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class SelectionMenu extends Menu {


    private final Message initialMessage;
    private final Consumer<SelectMenuInteractionEvent> action;
    private final List<ActionRow> actionRows;

    public SelectionMenu(EventWaiter eventWaiter, List<User> allowedUsers, List<Role> allowedRoles, long timeout, TimeUnit units, boolean recursive, boolean ephemeral, Message initialMessage, Consumer<SelectMenuInteractionEvent> action, List<ActionRow> actionRows) {
        super(eventWaiter, allowedUsers, allowedRoles, timeout, units, recursive, ephemeral);
        this.initialMessage = initialMessage;
        this.action = action;
        this.actionRows = actionRows;
    }

    @Override
    public void show(MessageChannel channel) {
        MessageBuilder builder = new MessageBuilder(initialMessage)
                .setActionRows(actionRows);
        channel.sendMessage(builder.build()).queue(
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
                SelectMenuInteractionEvent.class,
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

    public static class Builder extends Menu.Builder<SelectionMenu.Builder, SelectionMenu> {

        private Message initialMessage = null;
        private Consumer<SelectMenuInteractionEvent> action = null;
        private List<ActionRow> actionRows = new ArrayList<>();

        @Override
        public SelectionMenu build() {
            return new SelectionMenu(
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
        public SelectionMenu.Builder setInitialMessage(Message initialMessage) {
            this.initialMessage = initialMessage;
            return this;
        }

        /**
         * @param action The consumer that will be accepted after a button is clicked
         * @return Itself for chaining convenience
         */
        public SelectionMenu.Builder setAction(Consumer<SelectMenuInteractionEvent> action) {
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
