package de.codingair.clansystem.utils.clan;

import java.util.Set;

public class Rank {
    private int trace;  //permission successor, inheritance depth, -1 to ignore.
    private String name;  //display name including '&'-color-codes
    private final Set<Permission> permissions;

    public Rank(String name, Set<Permission> permissions) {
        this.trace = -1;
        this.name = name;
        this.permissions = permissions;
    }

    public Rank(int trace, String name, Set<Permission> permissions) {
        this.trace = trace;
        this.name = name;
        this.permissions = permissions;
    }

    public int getTrace() {
        return trace;
    }

    public void setTrace(int trace) {
        this.trace = trace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public boolean hasPermission(Permission permission) {
        return this.permissions.contains(permission);
    }
}
