package org.powernukkitx.vanillagen.packet;

import org.powernukkitx.vanillagen.netty.HandleByteBuf;
import org.powernukkitx.vanillagen.packet.objects.BlockEntityData;

public class BlockEntityDataPacket extends Packet {

    public String levelName;
    public long chunkHash;
    public BlockEntityData[] blockEntities = new BlockEntityData[0];

    @Override
    public byte getPid() {
        return ProtocolInfo.BLOCK_ENTITY_DATA;
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(levelName);
        byteBuf.writeLongLE(chunkHash);
        byteBuf.writeArray(blockEntities, blockEntity -> blockEntity.encode(byteBuf));
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        levelName = byteBuf.readString();
        chunkHash = byteBuf.readLongLE();
        blockEntities = byteBuf.readArray(BlockEntityData.class, HandleByteBuf::readBlockEntityData);
    }
}
