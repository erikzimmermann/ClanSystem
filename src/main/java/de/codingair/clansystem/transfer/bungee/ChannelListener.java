package de.codingair.clansystem.transfer.bungee;

import de.codingair.clansystem.bungee.ClanSystem;
import de.codingair.clansystem.transfer.DataHandler;
import de.codingair.clansystem.transfer.packets.PacketType;
import de.codingair.clansystem.transfer.packets.utils.Packet;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ChannelListener implements Listener {
    private final BungeeDataHandler bungeeDataHandler;

    public ChannelListener(BungeeDataHandler bungeeDataHandler) {
        this.bungeeDataHandler = bungeeDataHandler;
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        if(e.getTag().equals("BungeeCord")) {

            ServerInfo server = ClanSystem.proxy().getPlayer(e.getReceiver().toString()).getServer().getInfo();
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));

            try {
                if(!in.readUTF().equals(DataHandler.REQUEST_CHANNEL)) return;

                PacketType type = PacketType.getById(in.readInt());
                Packet packet = (Packet) type.getPacketClass().newInstance();

                packet.read(in);
                this.bungeeDataHandler.onReceive(packet, server);
            } catch(IOException | IllegalAccessException | InstantiationException e1) {
                e1.printStackTrace();
            }
        }
    }

}
