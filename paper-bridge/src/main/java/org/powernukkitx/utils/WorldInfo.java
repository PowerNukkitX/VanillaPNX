package org.powernukkitx.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
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

    public void queueChunk(long chunkHash) {
        chunkQueue.add(chunkHash);
    }

    public void tick() {
        for(Long chunkHash : chunkQueue.clone()) {
            int x = ChunkHash.getHashX(chunkHash);
            int z = ChunkHash.getHashZ(chunkHash);
            if(!chunkLoadingQueue.contains(chunkHash)) {
                getWorld().loadChunk(x, z, true);
            }
            if(getWorld().isChunkGenerated(x, z)) {
                sentChunkFinished(chunkHash);
            }

        }
    }

    private void sentChunkFinished(Long hash) {
        int x = ChunkHash.getHashX(hash);
        int z = ChunkHash.getHashZ(hash);

        Chunk chunk = getWorld().getChunkAt(x, z);

        JsonArray data = new JsonArray();
        for(int _x = 0; _x < 16; _x++) {
            for(int _z = 0; _z < 16; _z++) {
                for(int _y = getWorld().getMinHeight(); _y < getWorld().getMaxHeight(); _y++) {
                    Block block = chunk.getBlock(_x, _y, _z);
                    String state = block.getState().getBlockData().getAsString();
                    JsonArray blockData = new JsonArray();
                    blockData.add(_x);
                    blockData.add(_y);
                    blockData.add(_z);
                    blockData.add(state);
                    blockData.add(block.getBiome().getKey().getKey());
                    data.add(blockData);
                }
            }
        }

        JsonArray array = new JsonArray();
        array.add("ChunkCompletion");
        array.add(getName());
        array.add(hash);
        array.add(data);
        chunkQueue.remove(hash);
        PNXSocket.send(server, array);
    }

    public World getWorld() {
        if(world == null) {
            WorldCreator worldCreator = new WorldCreator(getName());
            worldCreator.environment(dimension(getDimension()));
            worldCreator.generateStructures(true);
            worldCreator.type(WorldType.NORMAL);
            worldCreator.seed(getSeed());
            world = worldCreator.createWorld();
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

}
