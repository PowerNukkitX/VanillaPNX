package org.powernukkitx.vanillagen.packet.objects.entity;

import org.powernukkitx.vanillagen.netty.HandleByteBuf;
import org.powernukkitx.vanillagen.packet.Codable;

public abstract class EntityExtra extends Codable {

    public abstract byte getEEid();

    public static final byte UNKNOWN = 0;
    public static final byte FRAME = 1;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte(getEEid());
    }

    public static EntityExtra decodeExtra(HandleByteBuf byteBuf) {
        EntityExtra extra = switch (byteBuf.readByte()) {
            case FRAME -> new ItemFrameData();
            default -> null;
        };
        extra.decode(byteBuf);
        return extra;
    }
}
