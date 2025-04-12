package org.powernukkitx.socket;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.powernukkitx.NettySocketServer;
import org.powernukkitx.packet.*;
import org.powernukkitx.packet.objects.ChunkInfo;


public class PaperNettyImpl extends NettySocketServer {

    @Getter
    private static Long heartbeatTime = System.currentTimeMillis();
    
    @Getter
    protected PNXServer server;

    public PaperNettyImpl(int port) {
        super(port, -1);
    }

    @Override
    protected void onPacket(Packet packet) {
        if(packet instanceof ClientHello hello) {
            destinationPort = hello.port;
            server = new PNXServer(hello.port);
            send(new ServerHello());
        } else if(packet instanceof ClientHeartbeat) {
            heartbeatTime = System.currentTimeMillis();
        } else if(packet instanceof WorldInfo info) {
            server.addWorldInfo(new org.powernukkitx.utils.WorldInfo(info.name, info.seed, info.dimension, server));
        } else if(packet instanceof ChunkRequest request) {
            for(ChunkInfo element : request.chunks) {
                server.getWorlds().get(request.levelName).queueChunk(element.chunkHash, element.priority);
            }
        }
    }
}
