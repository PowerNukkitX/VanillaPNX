package org.powernukkitx.vanillagen.packet.objects;

import org.powernukkitx.vanillagen.netty.HandleByteBuf;
import org.powernukkitx.vanillagen.packet.Codable;


public class ChunkData extends Codable {

    public long chunkHash;
    public BlockData[] blockData;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeLongLE(chunkHash);
        byteBuf.writeArray(blockData, data -> {
            data.encode(byteBuf);
        });
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        chunkHash = byteBuf.readLongLE();
        blockData = byteBuf.readArray(BlockData.class, HandleByteBuf::readBlockData);
    }

}
