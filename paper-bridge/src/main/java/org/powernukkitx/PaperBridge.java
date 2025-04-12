package org.powernukkitx;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.powernukkitx.listener.ChunkPopulateListener;
import org.powernukkitx.listener.ChunkUnloadListener;
import org.powernukkitx.listener.ServerLoadListener;
import org.powernukkitx.packet.ServerHelloPacket;
import org.powernukkitx.socket.PaperNettyImpl;
import org.powernukkitx.utils.WorldInfo;

public final class PaperBridge extends JavaPlugin {

    private static PaperBridge instance;

    @Getter
    private PaperNettyImpl socket;

    @Override
    public void onLoad() {
        instance = this;
        for(int port = Bukkit.getPort()+1; port < 0xFFFF; port++) {
            try {
                socket = new PaperNettyImpl(port);
                PaperBridge.get().getLogger().info("PNXSocketPortInfo=" + port + ";");
                break;
            } catch (Exception e){}
        }
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ServerLoadListener(), this);
        getServer().getPluginManager().registerEvents(new ChunkUnloadListener(), this);
        getServer().getPluginManager().registerEvents(new ChunkPopulateListener(), this);
        new BukkitRunnable() {
            @Override
            public void run() {
                if(System.currentTimeMillis() - PaperNettyImpl.getHeartbeatTime() > 5000) {
                    Bukkit.getLogger().warning("Did not receive an heartbeat from PowerNukkitX in the last 5 seconds... Shutting down!");
                    Bukkit.getServer().shutdown();
                }
                for(WorldInfo info : socket.getServer().getWorlds().values()) {
                    info.tick();
                }
            }
        }.runTaskTimer(this, 0, 5);
        socket.send(new ServerHelloPacket());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static PaperBridge get() {
        return instance;
    }
}
