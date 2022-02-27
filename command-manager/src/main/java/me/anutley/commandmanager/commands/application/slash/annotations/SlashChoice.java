package me.anutley.commandmanager.commands.application.slash.annotations;

public @interface SlashChoice {

    /**
     *
     * @return the name of the slash command option choice
     */
    String name();

    /**
     *
     * @return the value which the slash command option choice will return
     */
    String value();

}
