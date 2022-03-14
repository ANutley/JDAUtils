package me.anutley.jdautils.menus;

import me.anutley.jdautils.eventwaiter.EventWaiter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The base Menu class, which all other menu-related classes extend
 */
public abstract class Menu {

    protected final EventWaiter eventWaiter;
    protected final List<User> allowedUsers;
    protected final List<Role> allowedRoles;
    protected long timeout;
    protected final TimeUnit units;
    protected final boolean recursive;
    protected final boolean ephemeral;

    private final long startTimestamp = System.currentTimeMillis();

    /**
     * @param eventWaiter  The event waiter used to wait for specific events
     * @param allowedUsers The users allowed to interact with the menu
     * @param allowedRoles The roles allowed to interact with the menu
     * @param timeout      How long until this menu times out
     * @param units        The units for the timeout
     * @param recursive    Whether the menu should recursively listen for actions
     * @param ephemeral    Whether menus that reply to interactions should be ephemeral
     */
    public Menu(EventWaiter eventWaiter, List<User> allowedUsers, List<Role> allowedRoles, long timeout, TimeUnit units, boolean recursive, boolean ephemeral) {
        this.eventWaiter = eventWaiter;
        this.allowedUsers = allowedUsers;
        this.allowedRoles = allowedRoles;
        this.timeout = timeout;
        this.units = units;
        this.recursive = recursive;
        this.ephemeral = ephemeral;
    }

    /**
     * Sends the built menu to the channel you provide
     *
     * @param channel The channel that the menu should be sent to
     */
    public abstract void show(MessageChannel channel);

    /**
     * Replies to the provided event with the built menu
     *
     * @param event The event that the menu should reply to
     */
    public abstract void show(GenericCommandInteractionEvent event);

    /**
     * Checks different conditions to see if the user is allowed to interact with the menu
     *
     * @param user  The user that should be checked
     * @param guild The guild that the user is a member in
     * @return Whether the user is allowed to interact with the menu
     */
    protected boolean isAllowed(User user, @Nullable Guild guild) {
        if (user.isBot())
            return false;
        if (allowedUsers.isEmpty() && allowedRoles.isEmpty())
            return true;
        if (allowedUsers.contains(user))
            return true;
        if (guild == null || !guild.isMember(user))
            return false;

        return guild.getMember(user).getRoles().stream().anyMatch(allowedRoles::contains);
    }

    /**
     * A utility method used for the event waiters to calculate how long is left until the menu timeouts
     *
     * @return How long until the menu times out
     */
    protected long getTimeRemainingInMs() {
        long timeRemainingInMillis = units.toMillis(timeout) - (System.currentTimeMillis() - startTimestamp);
        timeout = units.convert(timeout, units);
        return timeRemainingInMillis;
    }


    /**
     * A builder which is used to easily create a menu instance
     *
     * @param <B> The builder that you are creating
     * @param <M> The menu that you are creating the builder for
     */
    @SuppressWarnings("unchecked")
    public static abstract class Builder<B extends Builder<B, M>, M extends Menu> {

        protected EventWaiter eventWaiter;
        protected List<User> allowedUsers = new ArrayList<>();
        protected List<Role> allowedRoles = new ArrayList<>();
        protected long timeout = -1;
        protected TimeUnit units = TimeUnit.MINUTES;
        protected boolean recursive = true;
        protected boolean ephemeral = false;

        /**
         * @return The built menu
         */
        public abstract M build();

        /**
         * @param eventWaiter Sets the event waiter
         * @return Itself for chaining convenience
         */
        public B setEventWaiter(EventWaiter eventWaiter) {
            this.eventWaiter = eventWaiter;
            return (B) this;
        }

        /**
         * @param allowedUsers Sets the users that are allowed to interact with the menu
         * @return Itself for chaining convenience
         */
        public B setAllowedUsers(List<User> allowedUsers) {
            this.allowedUsers = allowedUsers;
            return (B) this;
        }

        /**
         * @param user Adds a user that can interact with the menu
         * @return Itself for chaining convenience
         */
        public B addAllowedUser(User user) {
            this.allowedUsers.add(user);
            return (B) this;
        }

        /**
         * @param allowedRoles Sets the roles that are allowed to interact with the menu
         * @return Itself for chaining convenience
         */
        public B setAllowedRoles(List<Role> allowedRoles) {
            this.allowedRoles = allowedRoles;
            return (B) this;
        }

        /**
         * @param role Adds a role that can interact with the menu
         * @return Itself for chaining convenience
         */
        public B addAllowedRole(Role role) {
            this.allowedRoles.add(role);
            return (B) this;
        }


        /**
         * @param timeout Sets how long until the menu times out
         * @return Itself for chaining convenience
         */
        public B setTimeout(long timeout) {
            this.timeout = timeout;
            return (B) this;
        }

        /**
         * @param units The units for {@link Builder#setTimeout(long)}
         * @return Itself for chaining convenience
         */
        public B setUnits(TimeUnit units) {
            this.units = units;
            return (B) this;
        }

        /**
         * @param recursive Sets whether the menu should listen recursively for clicks, or expire after the first one
         * @return Itself for chaining convenience
         */
        public B setRecursive(boolean recursive) {
            this.recursive = false;
            return (B) this;
        }

        /**
         * @param ephemeral Whether menus that reply to interactions should be ephemeral
         * @return Itself for chaining convenience
         */
        public B setEphemeral(boolean ephemeral) {
            this.ephemeral = ephemeral;
            return (B) this;
        }
    }
}
