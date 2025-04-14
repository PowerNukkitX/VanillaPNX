package org.powernukkitx.vanillagen.packet.objects;

import org.powernukkitx.vanillagen.netty.HandleByteBuf;
import org.powernukkitx.vanillagen.packet.Codable;
import org.powernukkitx.vanillagen.packet.objects.entity.EntityExtra;

public class EntityData extends Codable {

    public double x;
    public double y;
    public double z;

    public String entity;

    public EntityExtra[] extras = new EntityExtra[0];

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeDoubleLE(x);
        byteBuf.writeDoubleLE(y);
        byteBuf.writeDoubleLE(z);
        byteBuf.writeString(entity);
        byteBuf.writeArray(extras, entityExtra -> entityExtra.encode(byteBuf));
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.x = byteBuf.readDoubleLE();
        this.y = byteBuf.readDoubleLE();
        this.z = byteBuf.readDoubleLE();
        this.entity = byteBuf.readString();
        this.extras = byteBuf.readArray(EntityExtra.class, EntityExtra::decodeExtra);
    }
}
