package org.powernukkitx.packet;

import org.powernukkitx.netty.HandleByteBuf;
import org.powernukkitx.packet.objects.BlockEntityData;
import org.powernukkitx.packet.objects.EntityData;

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
