package org.powernukkitx.vanillagen.listener;

import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.powernukkitx.vanillagen.PaperBridge;
import org.powernukkitx.vanillagen.utils.WorldInfo;

public class ServerLoadListener implements Listener {

    @Getter
    private static boolean loaded = false;

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        for(WorldInfo info : PaperBridge.get().getSocket().getServer().getWorlds().values()) {
            info.getWorld();
        }
        loaded = true;
    }

}
