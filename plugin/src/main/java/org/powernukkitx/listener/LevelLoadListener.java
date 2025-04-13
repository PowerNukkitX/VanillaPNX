package org.powernukkitx.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.level.Level;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import lombok.Getter;
import org.powernukkitx.VanillaPNX;
import org.powernukkitx.generator.VanillaGenerator;
import org.powernukkitx.packet.WorldInfoPacket;
import org.powernukkitx.server.socket.PNXNettyImpl;;

public class LevelLoadListener implements Listener {

    @Getter
    private static final ObjectArraySet<String> sentLevels = new ObjectArraySet<>();

    @EventHandler
    public void onLevelLoad(LevelLoadEvent event) {
        PNXNettyImpl socket = VanillaPNX.get().getWrapper().getSocket();
        if(socket != null && socket.isAlive()) {
            Level level = event.getLevel();
            if(!sentLevels.contains(level.getName())) {
                sendLevelInfo(level);
            }
        }
    }

    public static void sendLevelInfo(Level level) {
        if(VanillaGenerator.class.isAssignableFrom(level.getGenerator().getClass()) && VanillaPNX.get().getWrapper().getSocket().isServerHello()) {
            if (!sentLevels.contains(level.getName())) {
                PNXNettyImpl socket = VanillaPNX.get().getWrapper().getSocket();
                WorldInfoPacket info = new WorldInfoPacket();
                info.name = level.getName();
                info.seed = level.getSeed();
                info.dimension = level.getDimension();
                socket.send(info);
                sentLevels.add(level.getName());
            }
        }
    }

}
