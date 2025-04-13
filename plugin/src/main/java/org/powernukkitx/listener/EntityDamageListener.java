package org.powernukkitx.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.ChunkState;

public class EntityDamageListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity().getChunk().getChunkState() != ChunkState.GENERATED) {
            event.setCancelled();
        }
    }

}
