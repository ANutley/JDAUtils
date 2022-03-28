package me.anutley.jdautils.examples.interactions;

import me.anutley.jdautils.commands.CommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class InteractionsBot {

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA jda = JDABuilder.createDefault(args[0])
                .setEnabledIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS, GatewayIntent.DIRECT_MESSAGES)
                .disableCache(CacheFlag.VOICE_STATE)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();

        jda.awaitReady();

        CommandManager commandManager = new CommandManager.Builder()
                .addSearchPath("me.anutley.jdautils.examples.interactions")
                        .build(jda);

        commandManager.registerInteractions(); // This must be done to register all the interactions

    }

}
