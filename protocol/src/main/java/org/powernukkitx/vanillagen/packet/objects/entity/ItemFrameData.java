package org.powernukkitx.vanillagen.packet.objects.entity;

import org.powernukkitx.vanillagen.netty.HandleByteBuf;

public class ItemFrameData extends EntityExtra {

    public String face;
    public String item;

    @Override
    public byte getEEid() {
        return EntityExtra.FRAME;
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        super.encode(byteBuf);
        byteBuf.writeString(face);
        byteBuf.writeString(item);
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        super.decode(byteBuf);
        this.face = byteBuf.readString();
        this.item = byteBuf.readString();
    }
}
