package org.powernukkitx.vanillagen.socket;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.powernukkitx.vanillagen.PaperBridge;
import org.powernukkitx.vanillagen.listener.ServerLoadListener;
import org.powernukkitx.vanillagen.packet.LevelAcknowledgedPacket;
import org.powernukkitx.vanillagen.utils.WorldInfo;

@Getter
public class PNXServer {

    protected final int port;
    protected final Object2ObjectArrayMap<String, WorldInfo> worlds = new Object2ObjectArrayMap<>();

    public PNXServer(int port) {
        this.port = port;
    }

    public void addWorldInfo(WorldInfo info) {
        Bukkit.getLogger().info("Registered new World: " + info.getName());
        worlds.put(info.getName(), info);
        if(ServerLoadListener.isLoaded()) info.getWorld();
        LevelAcknowledgedPacket levelAcknowledged = new LevelAcknowledgedPacket();
        levelAcknowledged.levelName = info.getName();
        PaperBridge.get().getSocket().send(levelAcknowledged);
    }

}
