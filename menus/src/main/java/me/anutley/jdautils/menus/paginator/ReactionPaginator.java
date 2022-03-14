package me.anutley.jdautils.menus.paginator;

import me.anutley.jdautils.eventwaiter.EventWaiter;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Just your average old reaction paginator
 */
public class ReactionPaginator extends Paginator<String> {

    public ReactionPaginator(EventWaiter eventWaiter, List<User> allowedUsers, List<Role> allowedRoles, long timeout, TimeUnit units, boolean recursive, boolean ephemeral, List<Message> pages) {
        super(eventWaiter, allowedUsers, allowedRoles, timeout, units, recursive, ephemeral, pages);
    }

    @Override
    public void show(MessageChannel channel) {
        channel.sendMessage(getCurrent()).queue(
                message -> {
                    getButtons().forEach(r -> message.addReaction(r).queue());
                    waitForClick(message);
                }
        );
    }

    @Override
    public void show(GenericCommandInteractionEvent event) {
        event.reply(
                new MessageBuilder(getCurrent())
                        .build()
        ).setEphemeral(ephemeral).queue(
                success -> success.retrieveOriginal().queue(m -> {
                    getButtons().forEach(r -> m.addReaction(r).queue());
                    waitForClick(m);
                })
        );
    }

    private void waitForClick(Message message) {
        eventWaiter.wait(
                MessageReactionAddEvent.class,
                event -> {

                    event.getReaction().removeReaction(event.getUser()).queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));

                    if (event.getReactionEmote().getEmoji().equals(getNextButton())) {
                        message.editMessage(getNext()).queue();
                    } else if (event.getReactionEmote().getEmoji().equals(getPrevButton()))
                        message.editMessage(getPrev()).queue();

                    else if (event.getReactionEmote().getEmoji().equals(getStopButton()))
                        message.clearReactions().queue();

                    else if (event.getReactionEmote().getEmoji().equals(getDeleteButton()))
                        message.delete().queue();

                    if (recursive) waitForClick(message);

                },
                event -> isAllowed(event.getUser(), event.getGuild()) && event.getMessageIdLong() == message.getIdLong(),
                getTimeRemainingInMs(),
                TimeUnit.MILLISECONDS,
                null
        );
    }

    @Override
    public String getNextButton() {
        return "\u27A1";
    }

    @Override
    public String getPrevButton() {
        return "\u2B05";
    }

    @Override
    public String getStopButton() {
        return "\u23F9";
    }

    @Override
    public String getDeleteButton() {
        return "\uD83D\uDEAE";
    }


    public static class Builder extends Paginator.Builder<ReactionPaginator.Builder, ReactionPaginator> {

        @Override
        public ReactionPaginator build() {

            if (eventWaiter == null) throw new IllegalStateException("The Event Waiter must be set!");
            if (pages.size() == 0) throw new IllegalStateException("There must be at least one page");

            return new ReactionPaginator(
                    super.eventWaiter,
                    super.allowedUsers,
                    super.allowedRoles,
                    super.timeout,
                    super.units,
                    super.recursive,
                    super.ephemeral,
                    super.pages
            );
        }

    }
}
