package org.powernukkitx.generator;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import com.google.gson.JsonArray;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.powernukkitx.VanillaPNX;

public class GenerationQueue {

    private static final ObjectArraySet<String> acknowledgedLevels = new ObjectArraySet<>();

    private static final Object2ObjectArrayMap<String, ObjectArraySet<Long>> receivedLevelChunks = new Object2ObjectArrayMap<>();
    private static final Object2ObjectArrayMap<String, ObjectArraySet<Long>> requestedLevelChunks = new Object2ObjectArrayMap<>();

    public void init() {
        for(Level level : Server.getInstance().getLevels().values()) {
            if(VanillaGenerator.class.isAssignableFrom(level.getGenerator().getClass())) {
                level.getScheduler().scheduleRepeatingTask(VanillaPNX.get(), () -> {
                    if(isAcknowledged(level.getName())) {
                        tick(level);
                    }
                },1);
            }
        }
    }

    private void tick(Level level) {
        JsonArray chunks = new JsonArray();
        for(IChunk chunk : level.getChunks().values()) {
            if (chunk.getChunkState() != ChunkState.FINISHED) {
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
                        chunks.add(hash);
                        requestedChunks.add(hash);
                    }
                } else {
                    receivedChunks.remove(hash);
                    requestedChunks.remove(hash);
                }
            }
        }
        if(!chunks.isEmpty()) {
            JsonArray array = new JsonArray();
            array.add("RequestChunks");
            array.add(level.getName());
            array.add(chunks);
            VanillaPNX.get().getWrapper().getSocket().send(array);
        }
    }

    public static void acknowledged(String level) {
        acknowledgedLevels.add(level);
    }

    public static void addToReceived(String level, Long chunkHash) {
        if (!receivedLevelChunks.containsKey(level)) {
            receivedLevelChunks.put(level, new ObjectArraySet<>());
        }
        receivedLevelChunks.get(level).add(chunkHash);
    }

    public static boolean isAcknowledged(String level) {
        return acknowledgedLevels.contains(level);
    }

}
