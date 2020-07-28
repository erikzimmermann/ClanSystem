package de.codingair.clansystem.bungee.listeners;

import de.codingair.clansystem.bungee.ClanSystem;
import de.codingair.clansystem.transfer.PacketListener;
import de.codingair.clansystem.transfer.packets.PacketType;
import de.codingair.clansystem.transfer.packets.general.bungee.SendUUIDPacket;
import de.codingair.clansystem.transfer.packets.general.spigot.RequestUUIDPacket;
import de.codingair.clansystem.transfer.packets.utils.Packet;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class GeneralPacketReader implements PacketListener {
    @Override
    public void onReceive(Packet packet, ServerInfo server) {
        if(packet.getType() == PacketType.RequestUUIDPacket) {
            RequestUUIDPacket rp = (RequestUUIDPacket) packet;
            ProxiedPlayer pp = ClanSystem.getInstance().getProxy().getPlayer(rp.getPlayer());

            SendUUIDPacket send = new SendUUIDPacket(pp == null ? null : pp.getUniqueId());
            rp.applyAsAnswer(send);
            ClanSystem.getInstance().getDataHandler().send(send, server);
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
