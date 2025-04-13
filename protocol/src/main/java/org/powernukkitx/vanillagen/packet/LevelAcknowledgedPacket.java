package org.powernukkitx.vanillagen.packet;

import org.powernukkitx.vanillagen.netty.HandleByteBuf;

public class LevelAcknowledgedPacket extends Packet {
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
