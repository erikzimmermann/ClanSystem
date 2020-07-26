package de.codingair.clansystem.utils.clan;

import java.util.HashSet;
import java.util.Set;

/**
 * Unsorted permission tags.
 */
public enum Permission {
    INVITE,
    DEPOSIT,
    WITHDRAW,
    KICK,
    PROMOTE,
    DEMOTE,
    RENAME,
    TRANSFER,   //only for president
    DELETE;   //only for president

    public final static Permission[] VALUES = values();

    public void applyToByteMask(byte[] mask) {
        mask[ordinal() / 8] |= (1 << (ordinal() % 8));
    }

    public static Set<Permission> byByteMask(byte[] mask) {
        Set<Permission> permissions = new HashSet<>();

        for(int i = 0; i < VALUES.length; i++) {
            if((mask[i / 8] & (1 << (i % 8))) != 0) permissions.add(VALUES[i]);
        }

        return permissions;
    }
}
