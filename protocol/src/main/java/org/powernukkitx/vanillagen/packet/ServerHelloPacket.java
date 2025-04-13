package org.powernukkitx.vanillagen.packet;

public class ServerHelloPacket extends Packet {

    @Override
    public byte getPid() {
        return ProtocolInfo.SERVER_HELLO;
    }

}
