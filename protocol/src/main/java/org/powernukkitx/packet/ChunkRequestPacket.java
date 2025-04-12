package org.powernukkitx.packet;

import org.powernukkitx.netty.HandleByteBuf;
import org.powernukkitx.packet.objects.ChunkInfo;

public class ChunkRequestPacket extends Packet {

    @Override
    public byte getPid() {
        return ProtocolInfo.CHUNK_REQUEST;
    }

    public String levelName;
    public ChunkInfo[] chunks;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(levelName);
        byteBuf.writeArray(chunks, chunkVector -> {
            chunkVector.encode(byteBuf);
        });
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        levelName = byteBuf.readString();
        chunks = byteBuf.readArray(ChunkInfo.class, HandleByteBuf::readChunkVector);
    }
}
