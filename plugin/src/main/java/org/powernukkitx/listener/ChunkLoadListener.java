package org.powernukkitx.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.ChunkLoadEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import lombok.SneakyThrows;
import org.powernukkitx.VanillaGenerator;
import org.powernukkitx.VanillaPNX;

import java.util.concurrent.TimeUnit;

public class ChunkLoadListener implements Listener {

    public static final Object2ObjectArrayMap<String, ObjectArraySet<Long>> receivedLevelChunks = new Object2ObjectArrayMap<>();
    public static final Object2ObjectArrayMap<String, ObjectArraySet<Long>> requestedLevelChunks = new Object2ObjectArrayMap<>();

    @SneakyThrows
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        IChunk chunk = event.getChunk();
        Level level = event.getLevel();
        if(VanillaGenerator.class.isAssignableFrom(level.getGenerator().getClass())) {
            while (VanillaPNX.get().getWrapper().getSocket() == null || !VanillaPNX.get().getWrapper().getSocket().isServerHello()) {
                TimeUnit.MILLISECONDS.sleep(10);
            }
            if (event.isNewChunk()) {

                if (!requestedLevelChunks.containsKey(level.getName())) {
                    requestedLevelChunks.put(level.getName(), new ObjectArraySet<>());
                }
                if (!receivedLevelChunks.containsKey(level.getName())) {
                    receivedLevelChunks.put(level.getName(), new ObjectArraySet<>());
                }
                ObjectArraySet<Long> receivedChunks = receivedLevelChunks.get(level.getName());
                ObjectArraySet<Long> requestedChunks = requestedLevelChunks.get(level.getName());

                Long hash = Level.chunkHash(chunk.getX(), chunk.getZ());
                if (!receivedChunks.contains(hash)) {
                    if (!requestedChunks.contains(hash)) {
                        VanillaPNX.get().getWrapper().getSocket().send("RequestChunk", level.getName(), String.valueOf(hash));
                        requestedChunks.add(hash);
                    }
                } else receivedChunks.remove(hash);
            }
        }
    }

    public static void addToReceived(String level, Long chunkHash) {
        if (!receivedLevelChunks.containsKey(level)) {
            receivedLevelChunks.put(level, new ObjectArraySet<>());
        }
        receivedLevelChunks.get(level).add(chunkHash);
    }

}
