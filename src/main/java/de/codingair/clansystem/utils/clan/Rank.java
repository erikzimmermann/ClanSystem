package de.codingair.clansystem.utils.clan;

import de.codingair.clansystem.transfer.Serializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;

public class Rank implements Serializable {
    private int id;
    private int trace;  //permission successor, inheritance depth, -1 to ignore.
    private String name;  //display name including '&'-color-codes
    private Set<Permission> permissions;

    public Rank() {
    }

    public Rank(int id, String name, Set<Permission> permissions) {
        this.id = id;
        this.trace = -1;
        this.name = name;
        this.permissions = permissions;
    }

    public Rank(int id, int trace, String name, Set<Permission> permissions) {
        this.id = id;
        this.trace = trace;
        this.name = name;
        this.permissions = permissions;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeByte(id);
        out.writeByte(trace); //max 512 ranks
        out.writeUTF(name);

        byte[] permissions = new byte[2];
        this.permissions.forEach(p -> p.applyToByteMask(permissions));
        out.writeByte(permissions[0]);
        out.writeByte(permissions[1]);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        id = in.readUnsignedByte();
        trace = in.readUnsignedByte();
        name = in.readUTF();

        byte[] permissions = new byte[] {in.readByte(), in.readByte()};
        this.permissions = Permission.byByteMask(permissions);
    }

    public int getId() {
        return id;
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
