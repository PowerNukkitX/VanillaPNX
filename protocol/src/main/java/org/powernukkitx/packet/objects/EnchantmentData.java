package org.powernukkitx.packet.objects;

import org.powernukkitx.netty.HandleByteBuf;
import org.powernukkitx.packet.Codable;

public class EnchantmentData extends Codable {

    public String id;
    public int level;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(id);
        byteBuf.writeIntLE(level);
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.id = byteBuf.readString();
        this.level = byteBuf.readIntLE();
    }
}
