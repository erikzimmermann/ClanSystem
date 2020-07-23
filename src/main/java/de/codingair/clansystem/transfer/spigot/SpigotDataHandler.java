package de.codingair.clansystem.transfer.spigot;

import de.codingair.clansystem.transfer.DataHandler;
import de.codingair.clansystem.transfer.PacketListener;
import de.codingair.clansystem.transfer.packets.PacketType;
import de.codingair.clansystem.transfer.packets.utils.AnswerPacket;
import de.codingair.clansystem.transfer.packets.utils.Packet;
import de.codingair.clansystem.transfer.packets.utils.RequestPacket;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.time.TimeMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpigotDataHandler implements DataHandler {
    private final List<PacketListener> listeners = new ArrayList<>();
    private final JavaPlugin plugin;
    private final ChannelListener listener = new ChannelListener(this);
    private final TimeMap<UUID, Callback<Object>> callbacks = new TimeMap<>();

    public SpigotDataHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this.plugin, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(this.plugin, GET_CHANNEL, this.listener);
    }

    @Override
    public void onDisable() {
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this.plugin, "BungeeCord");
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this.plugin, GET_CHANNEL, this.listener);

        this.listeners.clear();
    }

    public void send(Packet packet) {
        send(packet, -1);
    }

    public void send(Packet packet, int timeOut) {
        if(!Bukkit.getOnlinePlayers().isEmpty()) {
            Player player = Bukkit.getOnlinePlayers().toArray(new Player[0])[0];

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            PacketType type = PacketType.getByObject(packet);

            if(type == PacketType.ERROR) throw new IllegalArgumentException("Could not find a PacketType! [" + packet.getClass().getName() + "]");

            if(packet instanceof RequestPacket) {
                RequestPacket<Object> rp = (RequestPacket<Object>) packet;
                if(rp.getCallback() != null) {
                    if(callbacks.containsKey(rp.getUniqueId())) rp.checkUUID(this.callbacks.keySet());

                    if(timeOut > 0) callbacks.put(rp.getUniqueId(), rp.getCallback(), timeOut);
                    else callbacks.put(rp.getUniqueId(), rp.getCallback());
                }
            }

            try {
                out.writeUTF(REQUEST_CHANNEL);
                out.writeInt(type.getId());
                packet.write(out);
            } catch(IOException e) {
                e.printStackTrace();
            }

            for(PacketListener listener : this.listeners) {
                if(listener.onSend(packet)) return;
            }

            player.sendPluginMessage(this.plugin, "BungeeCord", b.toByteArray());
        }
    }

    public void onReceive(Packet packet) {
        if(packet instanceof AnswerPacket) {
            AnswerPacket<Object> ap = (AnswerPacket<Object>) packet;
            UUID uniqueId = ap.getUniqueId();
            Callback<Object> callback;

            if((callback = this.callbacks.remove(uniqueId)) == null) return;
            callback.accept(ap.getValue());
        }

        for(PacketListener listener : this.listeners) {
            listener.onReceive(packet, null);
        }
    }

    public void register(PacketListener listener) {
        if(!this.listeners.contains(listener)) this.listeners.add(listener);
    }

    public void unregister(PacketListener listener) {
        this.listeners.remove(listener);
    }
}
