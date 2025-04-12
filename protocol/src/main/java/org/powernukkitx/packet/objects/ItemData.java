package org.powernukkitx.packet.objects;

import lombok.ToString;
import org.powernukkitx.netty.HandleByteBuf;
import org.powernukkitx.packet.Codable;

@ToString
public class ItemData extends Codable {

    public String id;
    public int count;
    public int damage;
    public EnchantmentData[] enchantments;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(id);
        byteBuf.writeIntLE(count);
        byteBuf.writeIntLE(damage);
        byteBuf.writeArray(enchantments, enchantment -> enchantment.encode(byteBuf));
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        id = byteBuf.readString();
        count = byteBuf.readIntLE();
        damage = byteBuf.readIntLE();
        enchantments = byteBuf.readArray(EnchantmentData.class, HandleByteBuf::readEnchantmentData);
    }
}
