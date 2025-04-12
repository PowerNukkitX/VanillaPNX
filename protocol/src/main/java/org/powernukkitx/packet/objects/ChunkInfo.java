package org.powernukkitx.packet.objects;

import org.powernukkitx.netty.HandleByteBuf;
import org.powernukkitx.packet.Codable;

public class ChunkInfo extends Codable {

    public long chunkHash;

    public long priority;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeLongLE(chunkHash);
        byteBuf.writeLongLE(priority);
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        chunkHash = byteBuf.readLongLE();
        priority = byteBuf.readLongLE();
    }

}
