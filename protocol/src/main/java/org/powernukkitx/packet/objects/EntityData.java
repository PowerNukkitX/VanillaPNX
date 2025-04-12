package org.powernukkitx.packet.objects;

import org.powernukkitx.netty.HandleByteBuf;
import org.powernukkitx.packet.Codable;

public class EntityData extends Codable {

    public double x;
    public double y;
    public double z;

    public String entity;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeDoubleLE(x);
        byteBuf.writeDoubleLE(y);
        byteBuf.writeDoubleLE(z);
        byteBuf.writeString(entity);
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.x = byteBuf.readDoubleLE();
        this.y = byteBuf.readDoubleLE();
        this.z = byteBuf.readDoubleLE();
        this.entity = byteBuf.readString();
    }
}
