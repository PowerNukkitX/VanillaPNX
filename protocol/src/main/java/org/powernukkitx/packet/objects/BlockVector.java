package org.powernukkitx.packet.objects;

import org.powernukkitx.netty.HandleByteBuf;
import org.powernukkitx.packet.Codable;


public class BlockVector extends Codable {

    public byte x;
    public int y;
    public byte z;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte(x);
        byteBuf.writeIntLE(y);
        byteBuf.writeByte(z);
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.x = byteBuf.readByte();
        this.y = byteBuf.readIntLE();
        this.z = byteBuf.readByte();
    }
}
