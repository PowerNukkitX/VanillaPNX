package org.powernukkitx.vanillagen.packet;

import org.powernukkitx.vanillagen.netty.HandleByteBuf;
import org.powernukkitx.vanillagen.packet.objects.EntityData;

public class PopulationPacket extends Packet {

    public String levelName;
    public EntityData[] entityData;

    @Override
    public byte getPid() {
        return ProtocolInfo.POPULATION;
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(levelName);
        byteBuf.writeArray(entityData, entity -> entity.encode(byteBuf));
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.levelName = byteBuf.readString();
        this.entityData = byteBuf.readArray(EntityData.class, HandleByteBuf::readEntityData);
    }
}
