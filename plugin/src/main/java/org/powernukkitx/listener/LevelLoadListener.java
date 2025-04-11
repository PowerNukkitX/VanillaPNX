package org.powernukkitx.listener;

import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.level.Level;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import lombok.Getter;
import org.powernukkitx.VanillaGenerator;
import org.powernukkitx.VanillaPNX;
import org.powernukkitx.server.socket.PaperSocket;

public class LevelLoadListener implements Listener {

    @Getter
    private static final ObjectArraySet<String> sentLevels = new ObjectArraySet<>();

    @EventHandler
    public void onLevelLoad(LevelLoadEvent event) {
        PaperSocket socket = VanillaPNX.get().getWrapper().getSocket();
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
                PaperSocket socket = VanillaPNX.get().getWrapper().getSocket();
                socket.send("WorldInfo", level.getName(), String.valueOf(level.getSeed()), String.valueOf(level.getDimension()));
                sentLevels.add(level.getName());
            }
        }
    }

}
