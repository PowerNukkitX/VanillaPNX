package org.powernukkitx.packet;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.powernukkitx.netty.HandleByteBuf;

@NoArgsConstructor
@AllArgsConstructor
public class ClientHello extends Packet {

    public int port;

    @Override
    public byte getPid() {
        return ProtocolInfo.CLIENT_HELLO;
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeIntLE(port);
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.port = byteBuf.readIntLE();
    }
}
