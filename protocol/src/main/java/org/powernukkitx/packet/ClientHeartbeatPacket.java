package org.powernukkitx.packet;

public class ClientHeartbeatPacket extends Packet {

    @Override
    public byte getPid() {
        return ProtocolInfo.CLIENT_HEARTBEAT;
    }

}
