package org.powernukkitx.listener;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.powernukkitx.PaperBridge;
import org.powernukkitx.packet.ChunkCompletion;
import org.powernukkitx.packet.objects.ChunkData;
import org.powernukkitx.utils.ChunkHash;
import org.powernukkitx.utils.WorldInfo;

public class ChunkPopulateListener implements Listener {
    
    @EventHandler
    public void on(ChunkPopulateEvent event) {
        World world = event.getWorld();
        Chunk chunk = event.getChunk();
        if(!PaperBridge.get().getSocket().getServer().getWorlds().get(world.getName()).getChunkQueue().containsKey(ChunkHash.chunkHash(chunk.getX(), chunk.getZ()))) {
            ChunkCompletion data = new ChunkCompletion();
            data.levelName = world.getName();
            WorldInfo info = PaperBridge.get().getSocket().getServer().getWorlds().get(world.getName());
            data.chunks = new ChunkData[]{info.getChunkData(ChunkHash.chunkHash(chunk.getX(), chunk.getZ()))};
            PaperBridge.get().getSocket().send(data);
        }
    }
    
}
