package org.powernukkitx.socket;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.joml.Vector2L;
import org.powernukkitx.NettySocketServer;
import org.powernukkitx.packet.*;
import org.powernukkitx.packet.objects.ChunkInfo;
import org.powernukkitx.packet.objects.LevelPlayerPosition;
import org.powernukkitx.utils.ChunkHash;


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
        } else if(packet instanceof PlayerPositionUpdate update) {
            for(LevelPlayerPosition position : update.positions) {
                Long2LongOpenHashMap chunks = server.getWorlds().get(position.levelName).getChunkQueue();
                for(Long hash : chunks.keySet()) {
                    int x = ChunkHash.getHashX(hash);
                    int z = ChunkHash.getHashZ(hash);
                    Vector2L vec = new Vector2L(x, z);
                    long minDistance = Long.MAX_VALUE;
                    for(Long _hash : position.chunks) {
                        int _x = ChunkHash.getHashX(_hash);
                        int _z = ChunkHash.getHashZ(_hash);
                        long distance = (long) vec.distance(new Vector2L(_x, _z));
                        if(distance < minDistance) {
                            minDistance = distance;
                        }
                    }
                    chunks.put((long) hash, minDistance);
                }
            }
        } else if(packet instanceof ChunkThrowaway throwaway) {
            server.getWorlds().get(throwaway.levelName).getChunkQueue().remove(throwaway.chunkHash);
        }
    }
}
