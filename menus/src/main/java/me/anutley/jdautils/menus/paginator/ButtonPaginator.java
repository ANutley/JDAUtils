package me.anutley.jdautils.menus.paginator;

import me.anutley.jdautils.eventwaiter.EventWaiter;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A paginator which uses {@link Button}s to operate instead of the traditional reactions
 */
public class ButtonPaginator extends Paginator<Button> {

    public ButtonPaginator(EventWaiter eventWaiter, List<User> allowedUsers, List<Role> allowedRoles, long timeout, TimeUnit units, boolean recursive, boolean ephemeral, MessageCreateData initialMessage, List<MessageCreateData> pages) {
        super(eventWaiter, allowedUsers, allowedRoles, timeout, units, recursive, ephemeral, initialMessage, pages);
    }

    @Override
    public void show(MessageChannel channel) {
        channel.sendMessage(getCurrent()).setComponents(ActionRow.of(getButtons())).queue(
                message -> waitForClick(message.getIdLong())
        );
    }


    @Override
    public void show(GenericCommandInteractionEvent event) {
        event.reply(
                MessageCreateBuilder.from(getCurrent())
                        .setComponents(ActionRow.of(getButtons()))
                        .build()
        ).setEphemeral(ephemeral).queue(
                success -> success.retrieveOriginal().queue(m -> waitForClick(m.getIdLong()))
        );
    }

    private void waitForClick(long messageId) {
        eventWaiter.wait(
                ButtonInteractionEvent.class,
                event -> {

                    event.deferEdit().queue();

                    if (event.getButton().equals(getNextButton())) {
                        event.getInteraction().getHook().editOriginal(MessageEditData.fromCreateData(getNext()))
                                .setComponents(ActionRow.of(getButtons()))
                                .queue();
                    } else if (event.getButton().equals(getPrevButton()))
                        event.getInteraction().getHook().editOriginal(MessageEditData.fromCreateData(getPrev()))
                                .setComponents(ActionRow.of(getButtons()))
                                .queue();

                    else if (event.getButton().equals(getStopButton()))
                        event.getInteraction().getMessage().editMessage(MessageEditData.fromCreateData(getCurrent())).queue();

                    if (event.getButton().equals(getDeleteButton()))
                        event.getMessage().delete().queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));

                    if (recursive) waitForClick(messageId);
                },
                event -> isAllowed(event.getUser(), event.getGuild()) && event.getMessage().getIdLong() == messageId,
                getTimeRemainingInMs(),
                TimeUnit.MILLISECONDS,
                null
        );
    }


    @Override
    public Button getNextButton() {
        return Button.primary("next", Emoji.fromUnicode("➡")).withDisabled(isEnd());
    }

    @Override
    public Button getPrevButton() {
        return Button.primary("prev", Emoji.fromUnicode("⬅")).withDisabled(isStart());
    }

    @Override
    public Button getStopButton() {
        return Button.secondary("stop", Emoji.fromUnicode("⏹"));
    }

    @Override
    public Button getDeleteButton() {
        return Button.danger("delete", Emoji.fromUnicode("\uD83D\uDEAE"));
    }


    public static class Builder extends Paginator.Builder<Builder, ButtonPaginator> {

        @Override
        public ButtonPaginator build() {

            if (eventWaiter == null) throw new IllegalStateException("The Event Waiter must be set!");
            if (pages.isEmpty()) throw new IllegalStateException("There must be at least one page");

            return new ButtonPaginator(
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
