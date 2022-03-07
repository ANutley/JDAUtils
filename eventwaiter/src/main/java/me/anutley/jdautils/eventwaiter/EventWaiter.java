package me.anutley.jdautils.eventwaiter;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A utility class which allows you to wait for specific events, and run actions that happen when this event fires (and set conditions to check before running)
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class EventWaiter implements EventListener {

    private final Map<Class<?>, Set<WaitingEvent>> eventsToWaitFor = new HashMap<>();
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();


    /**
     * Waits for a specific event
     *
     * @param eventClass       - The event to wait for
     * @param actionToRun      - The action to run when this event is fired
     * @param predicateToCheck - The conditions to check before running the chosen action
     * @param timeout          - How long before this event waiter will time out
     * @param units            - The units for the previous parameter
     * @param actionOnTimeout  - The action to run when this event waiter time-outs
     * @param <T>              - The event class
     */
    public <T extends Event> void wait(Class<T> eventClass,
                                       Consumer<T> actionToRun,
                                       Predicate<T> predicateToCheck,
                                       long timeout,
                                       TimeUnit units,
                                       Runnable actionOnTimeout
    ) {
        WaitingEvent waitingEvent = new WaitingEvent(actionToRun, predicateToCheck);
        Set<WaitingEvent> events = eventsToWaitFor.computeIfAbsent(eventClass, c -> ConcurrentHashMap.newKeySet());
        events.add(waitingEvent);

        if (timeout < 0 || units == null) return;

        scheduledExecutor.schedule(() -> {
            try {
                if (actionOnTimeout == null || !events.remove(waitingEvent)) return;
                actionOnTimeout.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, timeout, units);

    }

    /**
     * Waits for a specific event
     *
     * @param eventClass       - The event to wait for
     * @param actionToRun      - The action to run when this event is fired
     * @param predicateToCheck - The conditions to check before running the chosen action
     * @param <T>              - The event class
     */
    public <T extends Event> void wait(Class<T> eventClass,
                                       Consumer<T> actionToRun,
                                       Predicate<T> predicateToCheck
    ) {
        wait(eventClass, actionToRun, predicateToCheck, -1, null, null);
    }


    @Override
    public void onEvent(@NotNull GenericEvent event) {
        Set<WaitingEvent> events = eventsToWaitFor.get(event.getClass());

        if (events == null) return;

        events.removeIf(wEvent -> wEvent.run(event));

        if (event instanceof ShutdownEvent) scheduledExecutor.shutdown();
    }


    /**
     * POJO class to hold the consumer and predicate
     *
     * @param <E> The class of the event
     */
    private static class WaitingEvent<E extends GenericEvent> {
        private final Consumer<E> actionToRun;
        private final Predicate<E> predicate;

        WaitingEvent(Consumer<E> actionToRun, Predicate<E> predicate) {
            this.actionToRun = actionToRun;
            this.predicate = predicate;
        }

        public boolean run(E event) {
            if (!predicate.test(event)) return false;
            actionToRun.accept(event);

            return true;
        }
    }
}
