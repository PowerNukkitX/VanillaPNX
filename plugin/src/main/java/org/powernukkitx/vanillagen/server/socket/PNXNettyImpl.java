package org.powernukkitx.vanillagen.server.socket;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import org.powernukkitx.vanillagen.NettySocketServer;
import org.powernukkitx.vanillagen.VanillaPNX;
import org.powernukkitx.vanillagen.generator.GenerationQueue;
import org.powernukkitx.vanillagen.generator.VanillaGenerator;
import org.powernukkitx.vanillagen.listener.LevelLoadListener;
import org.powernukkitx.vanillagen.packet.*;
import org.powernukkitx.vanillagen.packet.objects.ChunkData;
import org.powernukkitx.vanillagen.packet.objects.LevelPlayerPosition;

public class PNXNettyImpl extends NettySocketServer {

    public PNXNettyImpl(int port, int destinationPort) {
        super(port, destinationPort);
    }

    @Getter
    private boolean serverHello = false;

    @Override
    protected void onPacket(Packet packet) {
        if(packet instanceof ServerHelloPacket) {
            serverHello = true;
            VanillaPNX.get().getServer().getLevels().values().forEach(LevelLoadListener::sendLevelInfo);
            Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(() -> {
                send(new ClientHeartbeatPacket());
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
                    PlayerPositionUpdatePacket update = new PlayerPositionUpdatePacket();
                    update.positions = positions.toArray(LevelPlayerPosition[]::new);
                    VanillaPNX.get().getWrapper().getSocket().send(update);
                }
            }, 10, 5);
        } else if(packet instanceof LevelAcknowledgedPacket levelAcknowledged) {
            GenerationQueue.acknowledged(levelAcknowledged.levelName);
        } else if(packet instanceof ChunkTerrainDataPacket chunkTerrainDataPacket) {
            Level level = VanillaPNX.get().getServer().getLevelByName(chunkTerrainDataPacket.levelName);
            for(ChunkData data : chunkTerrainDataPacket.chunks) {
                long chunkHash = data.chunkHash;
                VanillaGenerator.applyTerrain(level.getChunk(Level.getHashX(chunkHash), Level.getHashZ(chunkHash)), data.blockData);
            }
        } else if(packet instanceof PopulationPacket population) {
            String levelName = population.levelName;
            VanillaGenerator.applyEntity(levelName, population.entityData);
        } else if(packet instanceof BlockEntityDataPacket blockEntityData) {
            VanillaGenerator.applyBlockEntity(blockEntityData.levelName, blockEntityData.blockEntities);
        }
    }
}
