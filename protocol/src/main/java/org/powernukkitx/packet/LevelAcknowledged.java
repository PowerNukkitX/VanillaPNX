package org.powernukkitx.packet;

import org.powernukkitx.netty.HandleByteBuf;

public class LevelAcknowledged extends Packet {
    @Override
    public byte getPid() {
        return ProtocolInfo.LEVEL_ACKNOWLEDGED;
    }

    public String levelName;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(levelName);
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.levelName = byteBuf.readString();
    }
}
