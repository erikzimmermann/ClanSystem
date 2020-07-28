package de.codingair.clansystem.transfer.packets.general.spigot;

import de.codingair.clansystem.transfer.packets.utils.RequestPacket;
import de.codingair.codingapi.tools.Callback;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class RequestUUIDPacket extends RequestPacket<UUID> {
    private String player;

    public RequestUUIDPacket() {
    }

    public RequestUUIDPacket(String player, Callback<UUID> callback) {
        super(callback);
        this.player = player;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        super.write(out);
        out.writeUTF(player);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        super.read(in);
        player = in.readUTF();
    }

    public String getPlayer() {
        return player;
    }
}
