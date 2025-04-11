package org.powernukkitx.utils;

import com.google.gson.JsonArray;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.powernukkitx.PaperBridge;
import org.powernukkitx.socket.PNXServer;
import org.powernukkitx.socket.PNXSocket;

@Getter
@ToString
@RequiredArgsConstructor
public class WorldInfo {

    private final String name;
    private final long seed;
    private final int dimension;

    private final PNXServer server;
    private World world;

    private final ObjectArraySet<Long> chunkLoadingQueue = new ObjectArraySet<>();
    private final ObjectArraySet<Long> chunkQueue = new ObjectArraySet<>();

    private static final ObjectArraySet<Thread> chunkPacketThreads = new ObjectArraySet<>();

    public void queueChunk(long chunkHash) {
        chunkQueue.add(chunkHash);
    }

    public void tick() {
        chunkPacketThreads.removeIf(thread -> !thread.isAlive());
        if (chunkPacketThreads.size() < 32) {
            Thread thread = new Thread(() -> {
                long startTime = System.currentTimeMillis();
                ObjectArraySet<Long> chunkQueueClone = chunkQueue.clone();
                chunkQueue.clear();
                for(Long chunkHash : chunkQueueClone) {
                    sentChunkFinished(chunkHash);
                }
                Bukkit.getLogger().info("Sending " + chunkQueueClone.size() + " Chunks took " + (System.currentTimeMillis() - startTime) + " ms");
            });
            thread.start();
            chunkPacketThreads.add(thread);
        }
    }

    private void sentChunkFinished(Long hash) {
        int x = ChunkHash.getHashX(hash);
        int z = ChunkHash.getHashZ(hash);

        Chunk rChunk = getWorld().getChunkAt(x, z);
        ChunkSnapshot chunk = rChunk.getWorld().getChunkAt(x, z).getChunkSnapshot(false, true, false);
        JsonArray data = new JsonArray();
        int minHeight = getMinHeight(dimension);
        int maxHeight = getMaxHeight(dimension);
        for(int _x = 0; _x < 16; _x++) {
            for(int _z = 0; _z < 16; _z++) {
                for(int _y = minHeight; _y <= maxHeight; _y++) {
                    String state = chunk.getBlockData(_x, _y, _z).getAsString();
                    JsonArray blockData = new JsonArray();
                    blockData.add(_x);
                    blockData.add(_y);
                    blockData.add(_z);
                    blockData.add(state);
                    blockData.add(chunk.getBiome(_x, _y, _z).getKey().getKey());
                    data.add(blockData);
                }
            }
        }

        JsonArray array = new JsonArray();
        array.add("ChunkCompletion");
        array.add(getName());
        array.add(hash);
        array.add(data);
        PNXSocket.send(server, array);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PaperBridge.get(), () -> rChunk.unload(false));
    }

    public World getWorld() {
        if(world == null) {
            WorldCreator worldCreator = new WorldCreator(getName());
            worldCreator.environment(dimension(getDimension()));
            worldCreator.generateStructures(true);
            worldCreator.type(WorldType.NORMAL);
            worldCreator.seed(getSeed());
            world = worldCreator.createWorld();
            world.setAutoSave(false);
        }
        return world;
    }

    private World.Environment dimension(int id) {
        return switch (id) {
            case 1 -> World.Environment.NETHER;
            case 2 -> World.Environment.THE_END;
            default -> World.Environment.NORMAL;
        };
    }

    public int getMinHeight(int dimension) {
        return switch (dimension) {
            case 0 -> -64;
            default -> 0;
        };
    }

    public int getMaxHeight(int dimension) {
        return switch (dimension) {
            case 0 -> 319;
            case 1 -> 127;
            default -> 255;
        };
    }

}
