package org.powernukkitx.packet;

import org.powernukkitx.netty.HandleByteBuf;

public class ChunkThrowaway extends Packet {

    public String levelName;
    public long chunkHash;

    @Override
    public byte getPid() {
        return ProtocolInfo.CHUNK_THROWAWAY;
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(levelName);
        byteBuf.writeLongLE(chunkHash);
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        levelName = byteBuf.readString();
        chunkHash = byteBuf.readLongLE();
    }
}
