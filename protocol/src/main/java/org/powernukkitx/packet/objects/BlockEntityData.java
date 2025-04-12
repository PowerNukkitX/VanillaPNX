package org.powernukkitx.packet.objects;

import org.powernukkitx.netty.HandleByteBuf;
import org.powernukkitx.packet.Codable;

public class BlockEntityData extends Codable {

    public BlockVector vector;
    public String state;

    public ItemData[] items = new ItemData[0];

    @Override
    public void encode(HandleByteBuf byteBuf) {
        vector.encode(byteBuf);
        byteBuf.writeString(state);
        byteBuf.writeArray(items, itemData -> itemData.encode(byteBuf));
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        vector = new BlockVector();
        vector.decode(byteBuf);
        state = byteBuf.readString();
        items = byteBuf.readArray(ItemData.class, HandleByteBuf::readItemData);
    }
}
