package me.anutley.jdautils.examples.menus;

import me.anutley.jdautils.commands.commands.annotations.Command;
import me.anutley.jdautils.commands.commands.application.annotations.GuildCommand;
import me.anutley.jdautils.commands.commands.application.slash.annotations.JDASlashCommand;
import me.anutley.jdautils.menus.ButtonMenu;
import me.anutley.jdautils.menus.SelectionMenu;
import me.anutley.jdautils.menus.paginator.ButtonPaginator;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

import java.util.concurrent.TimeUnit;

@Command
public class MenuCommands {

    @GuildCommand("833042350850441216")
    @JDASlashCommand(name = "button-paginator", description = "A button paginator!")
    public void buttonPaginator(SlashCommandInteractionEvent event) {
        new ButtonPaginator.Builder()
                .setEventWaiter(MenuBot.waiter())
                .setTimeout(20)
                .setUnits(TimeUnit.SECONDS)
                .addPage(new MessageBuilder().setContent("test").build())
                .addPage(new MessageBuilder().setContent("test2").build())
                .build().show(event);
    }

    @GuildCommand("833042350850441216")
    @JDASlashCommand(name = "confirm-menu", description = "A confirm menu!")
    public void buttonMenu(SlashCommandInteractionEvent event) {
        new ButtonMenu.Builder()
                .setEventWaiter(MenuBot.waiter())
                .addActionRows(ActionRow.of(
                        Button.success("confirm", "Confirm"),
                        Button.danger("cancel", "Cancel")
                ))
                .setAction(aEvent -> {
                    if (aEvent.getButton().getId().equals("confirm")) {
                        aEvent.getMessage().editMessage("Confirmed!").override(true).queue();
//                         Do some things here
                    } else if (aEvent.getButton().getId().equals("cancel")) {
                        aEvent.getMessage().editMessage("Cancelled!").override(true).queue();
//                         Do some other stuff here
                    }
                })
                .setInitialMessage(
                        new MessageBuilder().setContent("Do you want to confirm or deny?").build()
                )
                .setRecursive(false)
                .build()
                .show(event);
    }

    @GuildCommand("833042350850441216")
    @JDASlashCommand(name = "select-menu", description = "A select menu!")
    public void selectMenu(SlashCommandInteractionEvent event) {
        new SelectionMenu.Builder()
                .setEventWaiter(MenuBot.waiter())
                .addActionRows(ActionRow.of(
                                SelectMenu.create("test")
                                        .addOption("Cool option 1", "Cool Option 1")
                                        .addOption("Even cooler option 2", "Even Cooler Option 2")
                                        .build()
                        )
                ).setAction(aEvent -> {
                    aEvent.reply(aEvent.getUser().getName() + " picked " + aEvent.getValues()).queue();
                })
                .setInitialMessage(new MessageBuilder().setContent("test").build())
                .setEphemeral(true)
                .build().show(event);
    }
}
