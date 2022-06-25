package me.anutley.jdautils.menus;

import me.anutley.jdautils.eventwaiter.EventWaiter;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ReactionMenu extends Menu {

    protected final Message initialMessage;
    protected final Consumer<MessageReactionAddEvent> action;
    protected final List<String> reactions;

    public ReactionMenu(EventWaiter eventWaiter, List<User> allowedUsers, List<Role> allowedRoles, long timeout, TimeUnit units, boolean recursive, boolean ephemeral, Message initialMessage, Consumer<MessageReactionAddEvent> action, List<String> reactions) {
        super(eventWaiter, allowedUsers, allowedRoles, timeout, units, recursive, ephemeral);
        this.initialMessage = initialMessage;
        this.action = action;
        this.reactions = reactions;
    }

    @Override
    public void show(MessageChannel channel) {
        channel.sendMessage(new MessageBuilder(initialMessage).build()).queue(
                message -> {
                    reactions.forEach(r -> message.addReaction(r).queue());
                    waitForClick(message.getIdLong());
                }
        );
    }

    @Override
    public void show(GenericCommandInteractionEvent event) {
        event.reply(
                new MessageBuilder((initialMessage))
                        .build()
        ).setEphemeral(ephemeral).queue(
                success -> success.retrieveOriginal().queue(m -> {
                    reactions.forEach(r -> m.addReaction(r).queue());
                    waitForClick(m.getIdLong());
                })
        );
    }

    private void waitForClick(long messageId) {
        eventWaiter.wait(
                MessageReactionAddEvent.class,
                event -> {
                    event.getReaction().removeReaction(event.getUser()).queue();
                    action.accept(event);
                    if (recursive) waitForClick(messageId);
                },
                event -> isAllowed(event.getUser(), event.getGuild()) && event.getMessageIdLong() == messageId,
                getTimeRemainingInMs(),
                TimeUnit.MILLISECONDS,
                null
        );
    }

    public static class Builder extends Menu.Builder<ReactionMenu.Builder, ReactionMenu> {

        protected Message initialMessage = null;
        protected Consumer<MessageReactionAddEvent> action = null;
        protected List<String> reactions = new ArrayList<>();

        @Override
        public ReactionMenu build() {
            return new ReactionMenu(
                    super.eventWaiter,
                    super.allowedUsers,
                    super.allowedRoles,
                    super.timeout,
                    super.units,
                    super.recursive,
                    super.ephemeral,
                    initialMessage,
                    action,
                    reactions
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
        public Builder setAction(Consumer<MessageReactionAddEvent> action) {
            this.action = action;
            return this;
        }

        /**
         * @param reactions Sets the reactions that are in the message
         * @return Itself for chaining convenience
         */
        public ReactionMenu.Builder setReactions(String... reactions) {
            this.reactions = Arrays.asList(reactions);
            return this;
        }

        /**
         * @param reactions A vararg containing the reactions which should be added to the message
         * @return Itself for chaining convenience
         */
        public ReactionMenu.Builder addReactions(String... reactions) {
            this.reactions.addAll(Arrays.asList(reactions));
            return this;
        }
    }
}
