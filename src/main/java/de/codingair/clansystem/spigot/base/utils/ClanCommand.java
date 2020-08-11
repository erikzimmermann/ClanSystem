package de.codingair.clansystem.spigot.base.utils;

import de.codingair.clansystem.spigot.base.utils.lang.Lang;
import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import org.bukkit.command.CommandSender;

public abstract class ClanCommand extends BaseComponent {
    public ClanCommand() {
    }

    public ClanCommand(String permission) {
        super(permission);
    }

    @Override
    public void noPermission(CommandSender sender, String s, CommandComponent commandComponent) {
        Lang.exc(sender, "No_Permission");
    }

    @Override
    public void onlyFor(boolean b, CommandSender sender, String s, CommandComponent commandComponent) {
        Lang.exc(sender, "Only_For_Players");
    }
}
