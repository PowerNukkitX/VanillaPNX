package org.powernukkitx.packet.objects;

import org.powernukkitx.netty.HandleByteBuf;
import org.powernukkitx.packet.Codable;

public class LevelPlayerPosition extends Codable {

    public String levelName;
    public Long[] chunks;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(levelName);
        byteBuf.writeArray(chunks, byteBuf::writeLongLE);
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        levelName = byteBuf.readString();
        chunks = byteBuf.readArray(Long.class, HandleByteBuf::readLongLE);
    }
}
