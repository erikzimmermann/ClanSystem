package de.codingair.clansystem.transfer;

import de.codingair.clansystem.transfer.packets.utils.Packet;
import net.md_5.bungee.api.config.ServerInfo;

public interface PacketListener {
    void onReceive(Packet packet, ServerInfo server); //server is null if packet comes from BungeeCord

    boolean onSend(Packet packet);
}
