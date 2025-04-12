package org.powernukkitx.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.ChunkLoadEvent;
import cn.nukkit.level.Level;
import lombok.SneakyThrows;
import org.powernukkitx.generator.GenerationQueue;
import org.powernukkitx.generator.VanillaGenerator;
import org.powernukkitx.VanillaPNX;

import java.util.concurrent.TimeUnit;

public class ChunkLoadListener implements Listener {


    //Freezing the server until the generator is ready!

    @SneakyThrows
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Level level = event.getLevel();
        if(VanillaGenerator.class.isAssignableFrom(level.getGenerator().getClass())) {
            while (!GenerationQueue.isAcknowledged(level.getName())) {
                if(VanillaPNX.get().getWrapper().getSocket() != null) LevelLoadListener.sendLevelInfo(level);
                TimeUnit.MILLISECONDS.sleep(10);
            }
        }
    }

}
