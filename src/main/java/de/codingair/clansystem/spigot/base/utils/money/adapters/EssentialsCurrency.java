package de.codingair.clansystem.spigot.base.utils.money.adapters;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import de.codingair.clansystem.spigot.base.utils.money.Adapter;
import net.ess3.api.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class EssentialsCurrency implements Adapter {

    @Override
    public synchronized double getMoney(Player player) {
        if(check(player)) return 0;

        try {
            return Economy.getMoneyExact(id(player)).doubleValue();
        } catch(com.earth2me.essentials.api.UserDoesNotExistException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public synchronized void withdraw(Player player, double amount) {
        if(check(player)) return;


        try {
            Economy.subtract(id(player), new BigDecimal(amount));
        } catch(com.earth2me.essentials.api.NoLoanPermittedException | com.earth2me.essentials.api.UserDoesNotExistException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void deposit(Player player, double amount) {
        if(check(player)) return;

        try {
            Economy.add(id(player), new BigDecimal(amount));
        } catch(NoLoanPermittedException | UserDoesNotExistException e) {
            e.printStackTrace();
        }
    }

    private boolean check(Player player) {
        if(!Bukkit.getPluginManager().isPluginEnabled("Essentials")) return true;

        if(!Economy.playerExists(player.getUniqueId())) Economy.createNPC(player.getName());
        return false;
    }
}
