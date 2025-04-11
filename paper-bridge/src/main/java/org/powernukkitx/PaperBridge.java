package org.powernukkitx;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.powernukkitx.listener.ServerLoadListener;
import org.powernukkitx.socket.PNXServer;
import org.powernukkitx.socket.PNXSocket;
import org.powernukkitx.utils.WorldInfo;

public final class PaperBridge extends JavaPlugin {

    private static PaperBridge instance;

    @Getter
    private PNXSocket socket;

    @Override
    public void onLoad() {
        instance = this;
        socket = new PNXSocket();
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ServerLoadListener(), this);
        new BukkitRunnable() {
            @Override
            public void run() {
                if(System.currentTimeMillis() - PNXSocket.getHeartbeatTime() > 60000) {
                    Bukkit.getLogger().warning("Did not receive an heartbeat from PowerNukkitX in the last minute... Shutting down!");
                    Bukkit.getServer().shutdown();
                }
                for(PNXServer server : socket.getServers().values()) {
                    for(WorldInfo info : server.getWorlds().values()) {
                        info.tick();
                    }
                }
            }
        }.runTaskTimer(this, 20, 1);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static PaperBridge get() {
        return instance;
    }
}
