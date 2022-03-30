package me.anutley.jdautils.examples.pingpong;

import me.anutley.jdautils.commands.CommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class PingPongBot {

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA jda = JDABuilder.createDefault(args[0])
                .setEnabledIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS, GatewayIntent.DIRECT_MESSAGES)
                .disableCache(CacheFlag.VOICE_STATE)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();

        jda.awaitReady();

        new CommandManager.Builder()
                .addSearchPath("me.anutley.jdautils.examples.pingpong") // The package the commands are in
                .textCommandManager(textCommandManager -> { // Allows you to modify the text-command related settings
                            textCommandManager.setDefaultPrefix("!"); // Sets the global prefix for the bot
                            textCommandManager.setGuildPrefix("833042350850441216", "??"); // This is volatile, it needs to be reset after every restart
                            textCommandManager.setAllowMentionAsPrefix(true); // Allow mentions to be used as prefixes
                        }
                )
                .build(jda);
    }
}
