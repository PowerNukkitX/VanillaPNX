package org.powernukkitx.packet;

public class ClientHeartbeat extends Packet {

    @Override
    public byte getPid() {
        return ProtocolInfo.CLIENT_HEARTBEAT;
    }

}
