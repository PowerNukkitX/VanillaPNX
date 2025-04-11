package org.powernukkitx.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.ChunkLoadEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import lombok.SneakyThrows;
import org.powernukkitx.generator.GenerationQueue;
import org.powernukkitx.generator.VanillaGenerator;
import org.powernukkitx.VanillaPNX;

import java.util.concurrent.TimeUnit;

public class ChunkLoadListener implements Listener {


    @SneakyThrows
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        IChunk chunk = event.getChunk();
        Level level = event.getLevel();
        if(VanillaGenerator.class.isAssignableFrom(level.getGenerator().getClass())) {
            while (!GenerationQueue.isAcknowledged(level.getName())) {
                if(VanillaPNX.get().getWrapper().getSocket() != null && VanillaPNX.get().getWrapper().getSocket().isServerHello()) LevelLoadListener.sendLevelInfo(level);
                TimeUnit.MILLISECONDS.sleep(10);
            }
        }
    }

}
