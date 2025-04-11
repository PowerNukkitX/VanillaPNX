package org.powernukkitx.listener;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.powernukkitx.PaperBridge;
import org.powernukkitx.socket.PNXServer;
import org.powernukkitx.utils.ChunkHash;
import org.powernukkitx.utils.WorldInfo;

public class ChunkUnloadListener implements Listener {
    
    @EventHandler
    public void on(ChunkUnloadEvent event) {
        for(PNXServer server : PaperBridge.get().getSocket().getServers().values()) {
            for(WorldInfo info : server.getWorlds().values()) {
                if(event.getWorld().getName().equals(info.getName())) {
                    Chunk chunk = event.getChunk();
                    if(info.getChunkQueue().contains(ChunkHash.chunkHash(chunk.getX(), chunk.getZ()))) {
                        PaperBridge.get().getLogger().warning("Chunk " + chunk.getX() + " " + chunk.getZ() + " in " + info.getName() + " unloaded while being queued!");
                        info.getWorld().loadChunk(chunk.getX(), chunk.getZ(), true);
                    }
                } else event.setSaveChunk(false);
            }
        }
    }
    
}
