package de.codingair.clansystem.transfer.bungee;

import de.codingair.clansystem.bungee.ClanSystem;
import de.codingair.clansystem.transfer.DataHandler;
import de.codingair.clansystem.transfer.PacketListener;
import de.codingair.clansystem.transfer.packets.PacketType;
import de.codingair.clansystem.transfer.packets.utils.AnswerPacket;
import de.codingair.clansystem.transfer.packets.utils.Packet;
import de.codingair.clansystem.transfer.packets.utils.RequestPacket;
import de.codingair.codingapi.tools.Callback;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BungeeDataHandler implements DataHandler {
    private final Plugin plugin;
    private final ChannelListener listener = new ChannelListener(this);
    private final HashMap<UUID, Callback<Object>> callbacks = new HashMap<>();
    private final List<PacketListener> listeners = new ArrayList<>();

    public BungeeDataHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        ClanSystem.proxy().getPluginManager().registerListener(this.plugin, this.listener);
        ClanSystem.proxy().registerChannel(GET_CHANNEL);
    }

    @Override
    public void onDisable() {
        ClanSystem.proxy().getPluginManager().unregisterListener(this.listener);
        ClanSystem.proxy().unregisterChannel(GET_CHANNEL);
        this.listeners.clear();
    }

    public void send(Packet packet, ServerInfo server) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        PacketType type = PacketType.getByObject(packet);

        if(type == PacketType.ERROR) throw new IllegalArgumentException("Could not find a PacketType! [" + packet.getClass().getName() + "]");

        if(packet instanceof RequestPacket) {
            RequestPacket<Object> rp = (RequestPacket<Object>) packet;
            if(rp.getCallback() != null) {
                if(callbacks.containsKey(rp.getUniqueId())) rp.checkUUID(this.callbacks.keySet());
                callbacks.put(rp.getUniqueId(), rp.getCallback());
            }
        }

        try {
            out.writeByte(type.ordinal());
            packet.write(out);
        } catch(IOException e) {
            e.printStackTrace();
        }

        for(PacketListener listener : listeners) {
            if(listener.onSend(packet)) return;
        }

        server.sendData(GET_CHANNEL, stream.toByteArray());
    }

    public void onReceive(Packet packet, ServerInfo server) {
        if(packet instanceof AnswerPacket) {
            AnswerPacket<Object> ap = (AnswerPacket<Object>) packet;
            UUID uniqueId = ap.getUniqueId();
            Callback<Object> callback;

            if((callback = this.callbacks.remove(uniqueId)) == null) return;
            callback.accept(ap.getValue());
        }

        for(PacketListener listener : this.listeners) {
            listener.onReceive(packet, server);
        }
    }

    public void register(PacketListener listener) {
        this.listeners.add(listener);
    }

    public void unregister(PacketListener listener) {
        this.listeners.remove(listener);
    }
}
