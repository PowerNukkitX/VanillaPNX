package org.powernukkitx.vanillagen.packet;

import org.powernukkitx.vanillagen.netty.HandleByteBuf;

public class WorldInfoPacket extends Packet {

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
