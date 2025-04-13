package org.powernukkitx.vanillagen.packet.objects;

import lombok.ToString;
import org.powernukkitx.vanillagen.netty.HandleByteBuf;
import org.powernukkitx.vanillagen.packet.Codable;

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
