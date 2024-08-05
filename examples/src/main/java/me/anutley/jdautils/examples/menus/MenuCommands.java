package me.anutley.jdautils.examples.menus;

import me.anutley.jdautils.commands.annotations.Command;
import me.anutley.jdautils.commands.application.annotations.GuildCommand;
import me.anutley.jdautils.commands.application.slash.annotations.JDASlashCommand;
import me.anutley.jdautils.commands.events.SlashCommandEvent;
import me.anutley.jdautils.menus.ButtonMenu;
import me.anutley.jdautils.menus.StringSelectionMenu;
import me.anutley.jdautils.menus.paginator.ButtonPaginator;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.util.concurrent.TimeUnit;

@Command
public class MenuCommands {

    @GuildCommand("833042350850441216")
    @JDASlashCommand(name = "button-paginator", description = "A button paginator!")
    public void buttonPaginator(SlashCommandEvent event) {
        new ButtonPaginator.Builder()
                .setEventWaiter(MenuBot.waiter())
                .setTimeout(20)
                .setUnits(TimeUnit.SECONDS)
                .addPage(new MessageCreateBuilder().setContent("test").build())
                .addPage(new MessageCreateBuilder().setContent("test2").build())
                .build().show(event.getDiscordEvent());
    }

    @GuildCommand("833042350850441216")
    @JDASlashCommand(name = "confirm-menu", description = "A confirm menu!")
    public void buttonMenu(SlashCommandEvent event) {
        new ButtonMenu.Builder()
                .setEventWaiter(MenuBot.waiter())
                .addActionRows(ActionRow.of(
                        Button.success("confirm", "Confirm"),
                        Button.danger("cancel", "Cancel")
                ))
                .setAction(aEvent -> {
                    aEvent.deferEdit().queue();

                    if (aEvent.getButton().getId().equals("confirm")) {
                        aEvent.getMessage().editMessage(new MessageEditBuilder()
                                .setContent("Confirmed")
                                .setComponents() // clear confirm / deny buttons
                                .build())
                                .queue();
//                         Do some things here
                    } else if (aEvent.getButton().getId().equals("cancel")) {
                        aEvent.getMessage().editMessage(new MessageEditBuilder()
                                        .setContent("Cancelled")
                                        .setComponents() // clear confirm / deny buttons
                                        .build())
                                .queue();
//                         Do some other stuff here
                    }
                })
                .setInitialMessage(
                        new MessageCreateBuilder().setContent("Do you want to confirm or deny?").build()
                )
                .setRecursive(false)
                .build()
                .show(event.getDiscordEvent());
    }

    @GuildCommand("833042350850441216")
    @JDASlashCommand(name = "select-menu", description = "A select menu!")
    public void selectMenu(SlashCommandEvent event) {
        new StringSelectionMenu.Builder()
                .setEventWaiter(MenuBot.waiter())
                .addActionRows(ActionRow.of(
                                StringSelectMenu.create("test")
                                        .addOption("Cool option 1", "Cool Option 1")
                                        .addOption("Even cooler option 2", "Even Cooler Option 2")
                                        .build()
                        )
                ).setAction(aEvent -> aEvent.reply(aEvent.getUser().getName() + " picked " + aEvent.getValues()).queue())
                .setInitialMessage(new MessageCreateBuilder().setContent("test").build())
                .setEphemeral(true)
                .build().show(event.getDiscordEvent());
    }
}
