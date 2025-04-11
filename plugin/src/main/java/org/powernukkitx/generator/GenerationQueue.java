package org.powernukkitx.generator;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import com.google.gson.JsonArray;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.powernukkitx.VanillaPNX;

public class GenerationQueue {

    private static final ObjectArraySet<String> acknowledgedLevels = new ObjectArraySet<>();

    private static final Object2ObjectArrayMap<String, LongOpenHashSet> receivedLevelChunks = new Object2ObjectArrayMap<>();
    private static final Object2ObjectArrayMap<String, Long2LongOpenHashMap> requestedLevelChunks = new Object2ObjectArrayMap<>();

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
                    requestedLevelChunks.put(level.getName(), new Long2LongOpenHashMap());
                }
                if (!receivedLevelChunks.containsKey(level.getName())) {
                    receivedLevelChunks.put(level.getName(), new LongOpenHashSet());
                }
                LongOpenHashSet receivedChunks = receivedLevelChunks.get(level.getName());
                Long2LongOpenHashMap requestedChunks = requestedLevelChunks.get(level.getName());

                long hash = Level.chunkHash(chunk.getX(), chunk.getZ());
                if (!receivedChunks.contains(hash)) {
                    if (!requestedChunks.containsKey(hash)) {
                        chunks.add(hash);
                        requestedChunks.put(hash, System.currentTimeMillis());
                    } else if(System.currentTimeMillis() - requestedChunks.get(hash) > 10000) {
                        requestedChunks.remove(hash);
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
            receivedLevelChunks.put(level, new LongOpenHashSet());
        }
        receivedLevelChunks.get(level).add(chunkHash);
    }

    public static boolean isAcknowledged(String level) {
        return acknowledgedLevels.contains(level);
    }

}
