package me.anutley.jdautils.menus.paginator;

import me.anutley.jdautils.eventwaiter.EventWaiter;
import me.anutley.jdautils.menus.Menu;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Allows you to use the full power of paginators in your applications
 *
 * @param <T> The entity which is used for 'clicking' the paginator.
 *            E.G. {@link Button}s are used for {@link ButtonPaginator}s and {@link String}'s are used to hold the unicode of the emoji's for {@link ReactionPaginator}
 */
public abstract class Paginator<T> extends Menu {

    protected List<MessageCreateData> pages;
    protected int index = 0;

    /**
     * @param eventWaiter  The event waiter used to wait for specific events
     * @param allowedUsers The users allowed to interact with the menu
     * @param allowedRoles The roles allowed to interact with the menu
     * @param timeout      How long until this menu times out
     * @param units        The units for the timeout
     * @param recursive    Whether the menu should recursively listen for actions
     * @param ephemeral    Whether menus that reply to interactions should be ephemeral
     * @param pages        A list of Message's which the paginator should use for its content
     */
    public Paginator(EventWaiter eventWaiter, List<User> allowedUsers, List<Role> allowedRoles, long timeout, TimeUnit units, boolean recursive, boolean ephemeral, List<MessageCreateData> pages) {
        super(eventWaiter, allowedUsers, allowedRoles, timeout, units, recursive, ephemeral);
        this.pages = pages;
    }

    /**
     * @return The entity that is used for the next button
     */
    public abstract T getNextButton();

    /**
     * @return The entity that is used for the previous button
     */
    public abstract T getPrevButton();

    /**
     * @return The entity that is used for the stop button
     */
    public abstract T getStopButton();

    /**
     * @return The entity that is used for the delete button
     */
    public abstract T getDeleteButton();

    /**
     * @return The entity that is used for the next button
     */
    public List<T> getButtons() {
        return new LinkedList<T>() {{
            add(getPrevButton());
            add(getNextButton());
            add(getStopButton());
            add(getDeleteButton());
        }};
    }


    /**
     * @return The next page in the paginator
     */
    public MessageCreateData getNext() {
        return isEnd() ? pages.get(index) : pages.get(++index);
    }

    /**
     * @return The previous page in the paginator
     */
    public MessageCreateData getPrev() {
        return isStart() ? pages.get(0) : pages.get(--index);
    }

    /**
     * @return The current page in the paginator
     */
    public MessageCreateData getCurrent() {
        return pages.get(index);
    }

    /**
     * @return Whether the current page is the beginning page
     */
    public boolean isStart() {
        return index == 0;
    }

    /**
     * @return Whether the current page is the end page
     */
    public boolean isEnd() {
        return index == pages.size() - 1;
    }


    /**
     * A common builder for paginators
     *
     * @param <B> The paginator builder that you want to create
     * @param <M> The paginator type you want to create
     */
    @SuppressWarnings("unchecked")
    public static abstract class Builder<B extends Paginator.Builder<B, M>, M extends Paginator<?>> extends Menu.Builder<B, M> {

        protected final List<MessageCreateData> pages = new ArrayList<>();

        /**
         * @param message The {@link MessageCreateData} to add to the paginator
         * @return Itself for chaining convenience
         */
        public B addPage(MessageCreateData message) {
            this.pages.add(message);
            return (B) this;
        }

        /**
         * Takes the provided content, builds an {@link MessageCreateData} and adds it to the paginator
         *
         * @param messageContent The content of the message you want to add
         * @return Itself for chaining convenience
         */
        public B addPage(String messageContent) {
            this.pages.add(new MessageCreateBuilder().addContent(messageContent).build());
            return (B) this;
        }

        /**
         * Takes the provided embed, builds an {@link MessageCreateData} and adds it to the paginator
         *
         * @param messageEmbed The {@link MessageEmbed} you want to add
         * @return Itself for chaining convenience
         */
        public B addPage(MessageEmbed messageEmbed) {
            this.pages.add(new MessageCreateBuilder().addEmbeds(messageEmbed).build());
            return (B) this;
        }

    }
}
