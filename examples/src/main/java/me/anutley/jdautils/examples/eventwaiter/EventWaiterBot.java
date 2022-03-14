package me.anutley.jdautils.examples.eventwaiter;

import me.anutley.jdautils.eventwaiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.concurrent.TimeUnit;

public class EventWaiterBot {

    public static void main(String[] args) throws LoginException, InterruptedException {

        EventWaiter waiter = new EventWaiter();

        JDA jda = JDABuilder.createDefault(args[0])
                .setEnabledIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .disableCache(CacheFlag.VOICE_STATE)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(waiter)
                .build();

        jda.awaitReady();

        waiter.wait(
                MessageReceivedEvent.class, // Listens for the message received event
                event -> event.getMessage().reply("Hi " + event.getAuthor().getName()).queue(), // The action that will run when this event is fired
                event -> !event.getAuthor().isBot(), // Any conditions
                10, // The timeout
                TimeUnit.MINUTES, // The unit for the timeout
                null // A runnable that will fire when the waiter times out
        );

    }
}
