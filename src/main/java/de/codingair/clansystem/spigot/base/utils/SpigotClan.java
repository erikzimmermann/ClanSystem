package de.codingair.clansystem.spigot.base.utils;

import de.codingair.clansystem.utils.clan.Clan;
import de.codingair.clansystem.utils.clan.Rank;
import de.codingair.clansystem.utils.clan.exceptions.*;
import de.codingair.clansystem.utils.statistics.Statistic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpigotClan extends Clan {
    public SpigotClan() {
    }

    public SpigotClan(int id, String name, Player president, HashMap<Integer, Rank> ranks, Rank pres) {
        super(id, name, president.getUniqueId(), ranks, pres);
    }

    public SpigotClan(int id, String name, HashMap<Integer, Rank> ranks, HashMap<UUID, Rank> members, Rank president, HashMap<Statistic, Long> statistics, int level, float exp, int money) {
        super(id, name, ranks, members, president, statistics, level, exp, money);
    }

    //start specifying Clan.java

    public void invite(Player executor, String player) throws AlreadyInvitedException, NotAMemberException, PermissionException {
        Player invite = Bukkit.getPlayer(player);

        if(invite == null) {
            //todo: BungeeCord -> send invite packet
            return;
        }

        //send formal invitation
        invite(executor.getUniqueId(), invite.getUniqueId());

        //todo: send messages
    }

    @Override
    public void acceptInvite(UUID executor) throws NotInvitedException {
        super.acceptInvite(executor);
    }

    @Override
    public void denyInvite(UUID executor) throws NotInvitedException {
        super.denyInvite(executor);
    }

    @Override
    public void deposit(UUID executor, int money) throws PermissionException, NotAMemberException {
        super.deposit(executor, money);
    }

    @Override
    public void withdraw(UUID executor, int money) throws PermissionException, NotAMemberException {
        super.withdraw(executor, money);
    }

    @Override
    public void kick(UUID executor, UUID player) throws PermissionException, NotAMemberException {
        super.kick(executor, player);
    }

    @Override
    public void leave(UUID executor) throws HighestRankException, NotAMemberException {
        super.leave(executor);
    }

    @Override
    public void promote(UUID executor, UUID promote) throws PermissionException, NotAMemberException {
        super.promote(executor, promote);
    }

    @Override
    public void demote(UUID executor, UUID demote) throws PermissionException, NotAMemberException {
        super.demote(executor, demote);
    }

    @Override
    public void rename(UUID executor, String name) throws PermissionException, NotAMemberException {
        super.rename(executor, name);
    }

    @Override
    public void transfer(UUID executor, UUID owner) throws NotAMemberException, PermissionException {
        super.transfer(executor, owner);
    }

    @Override
    public String list() {
        return super.list();
    }


    //end


    public Set<Player> getOnlinePlayers() {
        Set<Player> players = new HashSet<>();

        for(UUID uuid : members.keySet()) {
            Player p = Bukkit.getPlayer(uuid);
            if(p != null && p.isOnline()) players.add(p);
        }

        return players;
    }

    public boolean isSomebodyOnline() {
        return isSomebodyOnline(null);
    }

    public boolean isSomebodyOnline(Player except) {
        Set<Player> players = getOnlinePlayers();
        if(except != null) players.remove(except);

        boolean online = !players.isEmpty();
        players.clear();

        return online;
    }
}
