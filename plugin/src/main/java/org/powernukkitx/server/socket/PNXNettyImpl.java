package org.powernukkitx.server.socket;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import org.powernukkitx.NettySocketServer;
import org.powernukkitx.VanillaPNX;
import org.powernukkitx.generator.GenerationQueue;
import org.powernukkitx.generator.VanillaGenerator;
import org.powernukkitx.listener.LevelLoadListener;
import org.powernukkitx.packet.*;
import org.powernukkitx.packet.objects.ChunkData;

public class PNXNettyImpl extends NettySocketServer {

    public PNXNettyImpl(int port, int destinationPort) {
        super(port, destinationPort);
    }

    @Override
    protected void onPacket(Packet packet) {
        if(packet instanceof ServerHello) {
            VanillaPNX.get().getServer().getLevels().values().forEach(LevelLoadListener::sendLevelInfo);
            Server.getInstance().getScheduler().scheduleRepeatingTask(() -> {
                send(new ClientHeartbeat());
            }, 20);
            VanillaGenerator.getQueue().init();
        } else if(packet instanceof LevelAcknowledged levelAcknowledged) {
            GenerationQueue.acknowledged(levelAcknowledged.levelName);
        } else if(packet instanceof ChunkCompletion chunkCompletion) {
            Level level = VanillaPNX.get().getServer().getLevelByName(chunkCompletion.levelName);
            for(ChunkData data : chunkCompletion.chunks) {
                long chunkHash = data.chunkHash;
                GenerationQueue.addToReceived(chunkCompletion.levelName, chunkHash);
                VanillaGenerator.applyData(level.getChunk(Level.getHashX(chunkHash), Level.getHashZ(chunkHash)), data.blockData);
            }
        }
    }
}
