package org.powernukkitx.packet;

import org.powernukkitx.netty.HandleByteBuf;
import org.powernukkitx.packet.objects.LevelPlayerPosition;

public class PlayerPositionUpdate extends Packet {

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
