package org.powernukkitx.vanillagen.listener;

import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.ChunkUnloadEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import org.powernukkitx.vanillagen.VanillaPNX;
import org.powernukkitx.vanillagen.generator.GenerationQueue;
import org.powernukkitx.vanillagen.generator.VanillaGenerator;
import org.powernukkitx.vanillagen.packet.ChunkThrowawayPacket;
import org.powernukkitx.vanillagen.packet.objects.LevelPlayerPosition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ChunkUnloadListener implements Listener {

    private static final HashMap<String, Set<Long>> unloadQueue = new HashMap<>();

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Level level = event.getLevel();
        if(VanillaGenerator.class.isAssignableFrom(level.getGenerator().getClass())) {
            if(GenerationQueue.getRequestedLevelChunks().containsKey(level.getName())) {
                if(GenerationQueue.isAcknowledged(level.getName())) {
                    if (!unloadQueue.containsKey(level.getName())) {
                        unloadQueue.put(level.getName(), new HashSet<>());
                    }
                    Set<Long> queuedChunks = unloadQueue.get(level.getName());
                    IChunk chunk = event.getChunk();
                    long hash = Level.chunkHash(chunk.getX(), chunk.getZ());
                    Long2LongOpenHashMap chunks = GenerationQueue.getRequestedLevelChunks().get(level.getName());
                    if(chunks.containsKey(hash)) {
                        chunks.remove(hash);
                        queuedChunks.add(hash);
                    }
                }
            }
        }
    }

    public ChunkUnloadListener() {
        Server.getInstance().getScheduler().scheduleRepeatingTask(VanillaPNX.get(), () -> {
            if(VanillaPNX.get().getWrapper().getSocket().isServerHello()) {
                Set<LevelPlayerPosition> positions = new HashSet<>();
                for(String levelname : unloadQueue.keySet()) {
                    LevelPlayerPosition position = new LevelPlayerPosition();
                    position.levelName = levelname;
                    position.chunks = unloadQueue.get(levelname).toArray(Long[]::new);
                }
                ChunkThrowawayPacket throwaway = new ChunkThrowawayPacket();
                throwaway.positions = positions.toArray(LevelPlayerPosition[]::new);
                VanillaPNX.get().getWrapper().getSocket().send(throwaway);
                unloadQueue.clear();
            }
        }, 5);
    }

}
