package de.codingair.clansystem.transfer.packets.utils;

import de.codingair.clansystem.transfer.packets.PacketType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Packet {
    void write(DataOutputStream out) throws IOException;

    void read(DataInputStream in) throws IOException;

    default PacketType getType() {
        return PacketType.getByObject(this);
    }
}
