package org.powernukkitx.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.ChunkUnloadEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.powernukkitx.VanillaPNX;
import org.powernukkitx.generator.GenerationQueue;
import org.powernukkitx.generator.VanillaGenerator;
import org.powernukkitx.packet.ChunkThrowaway;

public class ChunkUnloadListener implements Listener {

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Level level = event.getLevel();
        if(VanillaGenerator.class.isAssignableFrom(level.getGenerator().getClass())) {
            if(GenerationQueue.getRequestedLevelChunks().containsKey(level.getName())) {
                IChunk chunk = event.getChunk();
                long hash = Level.chunkHash(chunk.getX(), chunk.getZ());
                Long2LongOpenHashMap chunks = GenerationQueue.getRequestedLevelChunks().get(level.getName());
                if(chunks.containsKey(hash)) {
                    chunks.remove(hash);
                    ChunkThrowaway throwaway = new ChunkThrowaway();
                    throwaway.levelName = level.getName();
                    throwaway.chunkHash = hash;
                    VanillaPNX.get().getWrapper().getSocket().send(throwaway);
                }
            }
        }
    }

}
