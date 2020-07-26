package de.codingair.clansystem.transfer.packets;

import de.codingair.clansystem.transfer.packets.general.BooleanPacket;
import de.codingair.clansystem.transfer.packets.general.IntegerPacket;
import de.codingair.clansystem.transfer.packets.general.LongPacket;
import de.codingair.clansystem.transfer.packets.utils.AnswerPacket;

//max 255 packets
public enum PacketType {
    ERROR(null),
    AnswerPacket(AnswerPacket.class),

    BooleanPacket(BooleanPacket.class),
    IntegerPacket(IntegerPacket.class),
    LongPacket(LongPacket.class),
    ;

    public static final PacketType[] VALUES = values();
    private final Class<?> packetClass;

    PacketType(Class<?> packetClass) {
        this.packetClass = packetClass;
    }

    public static PacketType getByObject(Object packet) {
        if(packet == null) return ERROR;

        for(PacketType packetType : VALUES) {
            if(packet.getClass().equals(packetType.getPacketClass())) return packetType;
        }

        return ERROR;
    }

    public Class<?> getPacketClass() {
        return packetClass;
    }
}
