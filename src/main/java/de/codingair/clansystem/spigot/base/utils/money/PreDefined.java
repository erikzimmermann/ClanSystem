package de.codingair.clansystem.spigot.base.utils.money;

import de.codingair.clansystem.spigot.base.utils.money.adapters.EssentialsCurrency;
import de.codingair.clansystem.spigot.base.utils.money.adapters.ExpCurrency;
import de.codingair.clansystem.spigot.base.utils.money.adapters.VaultCurrency;
import org.bukkit.Bukkit;

public enum PreDefined {
    ESSENTIALS(Bukkit.getPluginManager().isPluginEnabled("Essentials") ? new EssentialsCurrency() : null),
    VAULT(Bukkit.getPluginManager().isPluginEnabled("Vault") ? new VaultCurrency() : null),
    EXP(new ExpCurrency()),
    ;

    private final Adapter adapter;

    PreDefined(Adapter adapter) {
        this.adapter = adapter;
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public static PreDefined getByName(String name) {
        name = name.toUpperCase();
        for(PreDefined value : values()) {
            if(value.name().equals(name)) return value;
        }

        return null;
    }
}
