package org.powernukkitx.vanillagen.packet.objects;

import org.powernukkitx.vanillagen.netty.HandleByteBuf;
import org.powernukkitx.vanillagen.packet.Codable;

public class BlockData extends Codable {

    public BlockVector vector;
    public String blockState;
    public String biome;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        vector.encode(byteBuf);
        byteBuf.writeString(blockState);
        byteBuf.writeString(biome);
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        vector = new BlockVector();
        vector.decode(byteBuf);
        blockState = byteBuf.readString();
        biome = byteBuf.readString();
    }
}
