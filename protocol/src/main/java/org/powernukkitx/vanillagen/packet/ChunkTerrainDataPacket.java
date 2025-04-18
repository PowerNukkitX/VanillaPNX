package org.powernukkitx.vanillagen.packet;

import org.powernukkitx.vanillagen.netty.HandleByteBuf;
import org.powernukkitx.vanillagen.packet.objects.ChunkData;

public class ChunkTerrainDataPacket extends Packet {

    @Override
    public byte getPid() {
        return ProtocolInfo.CHUNK_TERRAIN_DATA;
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
