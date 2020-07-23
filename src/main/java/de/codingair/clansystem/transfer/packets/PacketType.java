package de.codingair.clansystem.transfer.packets;

import de.codingair.clansystem.transfer.packets.general.*;
import de.codingair.clansystem.transfer.packets.utils.AnswerPacket;

public enum PacketType {
    ERROR(0, null),
    AnswerPacket(1, AnswerPacket.class),

    BooleanPacket(100, BooleanPacket.class),
    IntegerPacket(101, IntegerPacket.class),
    LongPacket1(102, LongPacket.class),
    ;

    private int id;
    private Class<?> packet;

    PacketType(int id, Class<?> packet) {
        this.id = id;
        this.packet = packet;
    }

    public static PacketType getById(int id) {
        for(PacketType packetType : values()) {
            if(packetType.getId() == id) return packetType;
        }

        return ERROR;
    }

    public static PacketType getByObject(Object packet) {
        if(packet == null) return ERROR;

        for(PacketType packetType : values()) {
            if(packetType.equals(ERROR)) continue;

            if(packetType.getPacket().equals(packet.getClass())) return packetType;
        }

        return ERROR;
    }

    public int getId() {
        return id;
    }

    public Class<?> getPacket() {
        return packet;
    }
}
