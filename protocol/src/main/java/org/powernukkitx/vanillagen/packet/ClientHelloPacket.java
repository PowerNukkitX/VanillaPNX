package org.powernukkitx.vanillagen.packet;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.powernukkitx.vanillagen.netty.HandleByteBuf;

@NoArgsConstructor
@AllArgsConstructor
public class ClientHelloPacket extends Packet {

    public int port;
    public long processID;

    @Override
    public byte getPid() {
        return ProtocolInfo.CLIENT_HELLO;
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeIntLE(port);
        byteBuf.writeLongLE(processID);
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.port = byteBuf.readIntLE();
        this.processID = byteBuf.readLongLE();
    }
}
