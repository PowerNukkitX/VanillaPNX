package org.powernukkitx.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.level.Level;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import lombok.Getter;
import org.powernukkitx.VanillaPNX;
import org.powernukkitx.generator.VanillaGenerator;
import org.powernukkitx.packet.WorldInfo;
import org.powernukkitx.server.socket.PNXNettyImpl;;

public class LevelLoadListener implements Listener {

    @Getter
    private static final ObjectArraySet<String> sentLevels = new ObjectArraySet<>();

    @EventHandler
    public void onLevelLoad(LevelLoadEvent event) {
        PNXNettyImpl socket = VanillaPNX.get().getWrapper().getSocket();
        if(socket != null) {
            Level level = event.getLevel();
            if(!sentLevels.contains(level.getName())) {
                sendLevelInfo(level);
            }
        }
    }

    public static void sendLevelInfo(Level level) {
        if(VanillaGenerator.class.isAssignableFrom(level.getGenerator().getClass())) {
            if (!sentLevels.contains(level.getName())) {
                PNXNettyImpl socket = VanillaPNX.get().getWrapper().getSocket();
                WorldInfo info = new WorldInfo();
                info.name = level.getName();
                info.seed = level.getSeed();
                info.dimension = level.getDimension();
                socket.send(info);
                sentLevels.add(level.getName());
            }
        }
    }

}
