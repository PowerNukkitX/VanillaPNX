package org.powernukkitx.vanillagen.packet;

import org.powernukkitx.vanillagen.netty.HandleByteBuf;
import org.powernukkitx.vanillagen.packet.objects.LevelPlayerPosition;

public class PlayerPositionUpdatePacket extends Packet {

    public LevelPlayerPosition[] positions;

    @Override
    public byte getPid() {
        return ProtocolInfo.PLAYER_POSITION_UPDATE;
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(positions, positions -> {
            positions.encode(byteBuf);
        });
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        positions = byteBuf.readArray(LevelPlayerPosition.class, HandleByteBuf::readLevelPlayerPosition);
    }
}
