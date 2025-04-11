package org.powernukkitx.socket;

import com.google.gson.JsonArray;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.powernukkitx.listener.ServerLoadListener;
import org.powernukkitx.utils.WorldInfo;

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
        JsonArray array = new JsonArray();
        array.add("LevelAcknowledged");
        array.add(info.getName());
        PNXSocket.send(info.getServer(), array);
    }

}
