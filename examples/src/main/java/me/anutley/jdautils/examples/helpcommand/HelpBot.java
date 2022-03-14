package me.anutley.jdautils.examples.helpcommand;

import me.anutley.jdautils.commands.CommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class HelpBot {

    public static CommandManager commandManager;

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA jda = JDABuilder.createDefault(args[0])
                .setEnabledIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS, GatewayIntent.DIRECT_MESSAGES)
                .disableCache(CacheFlag.VOICE_STATE)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();

        jda.awaitReady();

        commandManager = new CommandManager(jda, "me.anutley.jdautils.examples.helpcommand") // The package the commands are in
                .textCommandManager(textCommandManager ->
                        textCommandManager.setPrefix("!!"));

    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }
}
