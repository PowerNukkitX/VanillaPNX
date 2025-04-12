package org.powernukkitx.server.socket;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.powernukkitx.NettySocketServer;
import org.powernukkitx.VanillaPNX;
import org.powernukkitx.generator.GenerationQueue;
import org.powernukkitx.generator.VanillaGenerator;
import org.powernukkitx.listener.LevelLoadListener;
import org.powernukkitx.packet.*;
import org.powernukkitx.packet.objects.ChunkData;
import org.powernukkitx.packet.objects.LevelPlayerPosition;

public class PNXNettyImpl extends NettySocketServer {

    public PNXNettyImpl(int port, int destinationPort) {
        super(port, destinationPort);
    }

    @Override
    protected void onPacket(Packet packet) {
        if(packet instanceof ServerHello) {
            VanillaPNX.get().getServer().getLevels().values().forEach(LevelLoadListener::sendLevelInfo);
            Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(() -> {
                send(new ClientHeartbeat());
                ObjectOpenHashSet<LevelPlayerPosition> positions = new ObjectOpenHashSet<>();
                for(Level level : Server.getInstance().getLevels().values()) {
                    if(GenerationQueue.isAcknowledged(level.getName())) {
                        LongOpenHashSet chunks = new LongOpenHashSet();
                        for(Player player : level.getPlayers().values()) {
                            chunks.add(Level.chunkHash(player.getChunkX(), player.getChunkZ()));
                        }
                        if(!chunks.isEmpty()) {
                            LevelPlayerPosition position = new LevelPlayerPosition();
                            position.levelName = level.getName();
                            position.chunks = chunks.toArray(Long[]::new);
                            positions.add(position);
                        }
                    }
                }
                if(!positions.isEmpty()) {
                    PlayerPositionUpdate update = new PlayerPositionUpdate();
                    update.positions = positions.toArray(LevelPlayerPosition[]::new);
                    VanillaPNX.get().getWrapper().getSocket().send(update);
                }
            }, 10, 1);
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
