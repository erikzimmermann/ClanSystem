package de.codingair.clansystem.transfer.packets.general.bungee;

import de.codingair.clansystem.transfer.packets.utils.AnswerPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class SendUUIDPacket extends AnswerPacket<UUID> {
    public SendUUIDPacket() {
    }

    public SendUUIDPacket(UUID value) {
        super(value);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        super.write(out);
        if(getValue() != null) {
            out.writeBoolean(true);
            out.writeLong(getValue().getMostSignificantBits());
            out.writeLong(getValue().getLeastSignificantBits());
        } else out.writeBoolean(false);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        super.read(in);
        if(in.readBoolean()) setValue(new UUID(in.readLong(), in.readLong()));
    }
}
