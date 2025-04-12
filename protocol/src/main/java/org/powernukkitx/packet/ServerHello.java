package org.powernukkitx.packet;

public class ServerHello extends Packet {

    @Override
    public byte getPid() {
        return ProtocolInfo.SERVER_HELLO;
    }

}
