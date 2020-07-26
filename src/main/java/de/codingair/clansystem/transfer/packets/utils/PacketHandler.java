package de.codingair.clansystem.transfer.packets.utils;

public interface PacketHandler {
    void handle(Packet packet, String... extra);
}
