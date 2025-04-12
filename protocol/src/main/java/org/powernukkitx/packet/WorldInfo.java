package org.powernukkitx.packet;

import org.powernukkitx.netty.HandleByteBuf;

public class WorldInfo extends Packet {

    @Override
    public byte getPid() {
        return ProtocolInfo.WORLD_INFO;
    }

    public String name;
    public long seed;
    public int dimension;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(name);
        byteBuf.writeLongLE(seed);
        byteBuf.writeByte(dimension);
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.name = byteBuf.readString();
        this.seed = byteBuf.readLongLE();
        this.dimension = byteBuf.readByte();
    }
}
