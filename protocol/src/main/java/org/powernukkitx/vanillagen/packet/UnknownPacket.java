package org.powernukkitx.vanillagen.packet;

public class UnknownPacket extends Packet {

    public UnknownPacket(int pid) {
        System.out.println("Received unknown packet: " + pid);
    }

    @Override
    public byte getPid() {
        return ProtocolInfo.UNKNOWN;
    }
}
