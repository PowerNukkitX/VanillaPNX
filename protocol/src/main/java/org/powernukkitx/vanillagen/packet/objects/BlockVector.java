package org.powernukkitx.vanillagen.packet.objects;

import lombok.ToString;
import org.powernukkitx.vanillagen.netty.HandleByteBuf;
import org.powernukkitx.vanillagen.packet.Codable;

@ToString
public class BlockVector extends Codable {

    public int x;
    public int y;
    public int z;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeIntLE(x);
        byteBuf.writeIntLE(y);
        byteBuf.writeIntLE(z);
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.x = byteBuf.readIntLE();
        this.y = byteBuf.readIntLE();
        this.z = byteBuf.readIntLE();
    }
}
