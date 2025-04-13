package org.powernukkitx.vanillagen.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkUnloadListener implements Listener {
    
    @EventHandler
    public void on(ChunkUnloadEvent event) {
        event.setSaveChunk(false);
    }
    
}
