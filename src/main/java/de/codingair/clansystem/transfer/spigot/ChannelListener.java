package de.codingair.clansystem.transfer.spigot;

import de.codingair.clansystem.transfer.DataHandler;
import de.codingair.clansystem.transfer.packets.PacketType;
import de.codingair.clansystem.transfer.packets.utils.Packet;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ChannelListener implements PluginMessageListener {
    private final SpigotDataHandler spigotDataHandler;

    public ChannelListener(SpigotDataHandler spigotDataHandler) {
        this.spigotDataHandler = spigotDataHandler;
    }

    @Override
    public void onPluginMessageReceived(String tag, Player player, byte[] bytes) {
        if(tag.equals(DataHandler.GET_CHANNEL)) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));

            try {
                PacketType type = PacketType.VALUES[in.readUnsignedByte()];
                Packet packet = (Packet) type.getPacketClass().newInstance();

                packet.read(in);
                this.spigotDataHandler.onReceive(packet);
            } catch(IOException | IllegalAccessException | InstantiationException e1) {
                e1.printStackTrace();
            }
        }
    }
}
