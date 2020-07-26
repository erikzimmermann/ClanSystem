package de.codingair.clansystem.spigot.base.utils.money;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface Adapter {
    double getMoney(Player player);

    void withdraw(Player player, double amount);

    void deposit(Player player, double amount);

    default UUID id(Player player) {
        return player.getUniqueId();
    }
}
