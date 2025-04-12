package org.powernukkitx.packet;

import org.powernukkitx.netty.HandleByteBuf;
import org.powernukkitx.packet.objects.ChunkData;

public class ChunkCompletion extends Packet {

    @Override
    public byte getPid() {
        return ProtocolInfo.CHUNK_COMPLETION;
    }

    public String levelName;
    public ChunkData[] chunks;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(levelName);
        byteBuf.writeArray(chunks, chunkData -> chunkData.encode(byteBuf));
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.levelName = byteBuf.readString();
        this.chunks = byteBuf.readArray(ChunkData.class, HandleByteBuf::readChunkData);
    }
}
