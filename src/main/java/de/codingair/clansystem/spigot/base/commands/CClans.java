package de.codingair.clansystem.spigot.base.commands;

import de.codingair.clansystem.spigot.ClanSystem;
import de.codingair.clansystem.spigot.base.utils.ClanCommand;
import de.codingair.clansystem.spigot.base.utils.SpigotClan;
import de.codingair.clansystem.spigot.base.utils.lang.Lang;
import de.codingair.clansystem.utils.Permissions;
import de.codingair.clansystem.utils.clan.Clan;
import de.codingair.clansystem.utils.clan.exceptions.ClanNameNotAvailableException;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.server.commands.builder.special.MultiCommandComponent;
import de.codingair.codingapi.tools.Callback;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CClans extends CommandBuilder {
    public CClans() {
        super(ClanSystem.getInstance(), "clans", "The main clan command", new ClanCommand(Permissions.clans) {
            @Override
            public void unknownSubCommand(CommandSender commandSender, String s, String[] strings) {
                //todo
            }

            @Override
            public boolean runCommand(CommandSender commandSender, String s, String[] strings) {
                return false;
            }
        }.setOnlyPlayers(true), true);

        getBaseComponent().addChild(new CommandComponent("create") {
            @Override
            public boolean runCommand(CommandSender sender, String s, String[] strings) {
                Lang.cu(sender, "clans.create");
                return false;
            }
        });

        getComponent("create").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender commandSender, String[] strings, List<String> list) {
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                if(ClanSystem.man().check(sender)) return false;

                if(ClanSystem.man().hasClan((Player) sender)) {
                    Lang.exc(sender, "Already_in_Clan");
                    return false;
                }

                //todo check account (#28)

                try {
                    ClanSystem.man().create((Player) sender, argument, new Callback<SpigotClan>() {
                        @Override
                        public void accept(SpigotClan clan) {
                            Lang.suc(sender, "Clan_created");
                        }
                    });
                } catch(ClanNameNotAvailableException e) {
                    Lang.exc(sender, "Input_not_available", s -> s.replace("%INPUT%", argument));
                }
                return false;
            }
        });
    }
}
