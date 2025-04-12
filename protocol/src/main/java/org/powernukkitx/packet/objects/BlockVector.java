package org.powernukkitx.packet.objects;

import lombok.ToString;
import org.powernukkitx.netty.HandleByteBuf;
import org.powernukkitx.packet.Codable;

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
