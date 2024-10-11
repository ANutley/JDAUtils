package me.anutley.jdautils.menus.paginator;

import me.anutley.jdautils.eventwaiter.EventWaiter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Just your average old reaction paginator
 */
public class ReactionPaginator extends Paginator<String> {

    public ReactionPaginator(EventWaiter eventWaiter, List<User> allowedUsers, List<Role> allowedRoles, long timeout, TimeUnit units, boolean recursive, boolean ephemeral, MessageCreateData initialMessage, List<MessageCreateData> pages) {
        super(eventWaiter, allowedUsers, allowedRoles, timeout, units, recursive, ephemeral, initialMessage, pages);
    }

    @Override
    public void show(MessageChannel channel) {
        channel.sendMessage(getCurrent()).queue(
                message -> {
                    getButtons().forEach(r -> message.addReaction(Emoji.fromUnicode(r)).queue());
                    waitForClick(message);
                }
        );
    }

    @Override
    public void show(GenericCommandInteractionEvent event) {
        event.reply(
                MessageCreateBuilder.from(getCurrent())
                        .build()
        ).setEphemeral(ephemeral).queue(
                success -> success.retrieveOriginal().queue(m -> {
                    getButtons().forEach(r -> m.addReaction(Emoji.fromUnicode(r)).queue());
                    waitForClick(m);
                })
        );
    }

    private void waitForClick(Message message) {
        eventWaiter.wait(
                MessageReactionAddEvent.class,
                event -> {
                    event.retrieveUser().queue(user -> event.getReaction().removeReaction(user).queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE)));

                    if (event.getEmoji().getName().equals(getNextButton())) {
                        message.editMessage(MessageEditData.fromCreateData(getNext())).queue();
                    } else if (event.getEmoji().getName().equals(getPrevButton()))
                        message.editMessage(MessageEditData.fromCreateData(getPrev())).queue();

                    else if (event.getEmoji().getName().equals(getStopButton()))
                        message.clearReactions().queue();

                    else if (event.getEmoji().getName().equals(getDeleteButton()))
                            message.delete().queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));

                    if (recursive) waitForClick(message);

                },
                event -> isAllowed(event.retrieveUser().complete(), event.getGuild()) && event.getMessageIdLong() == message.getIdLong(),
                getTimeRemainingInMs(),
                TimeUnit.MILLISECONDS,
                null
        );
    }

    @Override
    public String getNextButton() {
        return "➡";
    }

    @Override
    public String getPrevButton() {
        return "⬅";
    }

    @Override
    public String getStopButton() {
        return "⏹";
    }

    @Override
    public String getDeleteButton() {
        return "\uD83D\uDEAE";
    }


    public static class Builder extends Paginator.Builder<ReactionPaginator.Builder, ReactionPaginator> {

        @Override
        public ReactionPaginator build() {

            if (eventWaiter == null) throw new IllegalStateException("The Event Waiter must be set!");
            if (pages.isEmpty()) throw new IllegalStateException("There must be at least one page");

            return new ReactionPaginator(
                    super.eventWaiter,
                    super.allowedUsers,
                    super.allowedRoles,
                    super.timeout,
                    super.units,
                    super.recursive,
                    super.ephemeral,
                    super.initialMessage,
                    super.pages
            );
        }

    }
}
