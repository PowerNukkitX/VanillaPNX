package org.powernukkitx.utils;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.*;
import org.powernukkitx.PaperBridge;
import org.powernukkitx.packet.ChunkCompletion;
import org.powernukkitx.packet.objects.BlockData;
import org.powernukkitx.packet.objects.BlockVector;
import org.powernukkitx.packet.objects.ChunkData;
import org.powernukkitx.socket.PNXServer;

import java.util.Comparator;

@Getter
@ToString
@RequiredArgsConstructor
public class WorldInfo {

    private final String name;
    private final long seed;
    private final int dimension;

    private final PNXServer server;
    private World world;

    @Getter
    private final Long2LongOpenHashMap chunkQueue = new Long2LongOpenHashMap();

    private static final ObjectArraySet<Thread> chunkPacketThreads = new ObjectArraySet<>();

    public void queueChunk(long chunkHash, long priority) {
        chunkQueue.put(chunkHash, priority);
    }

    public void tick() {
        chunkPacketThreads.removeIf(thread -> !thread.isAlive());
        if(!chunkQueue.isEmpty()) {
            if (chunkPacketThreads.size() < 32) {
                Thread thread = new Thread(() -> {
                    long startTime = System.currentTimeMillis();
                    LongOpenHashSet reserve = new LongOpenHashSet();
                    long minPriority = -1;
                    for(var chunkHash : chunkQueue.long2LongEntrySet().stream().sorted(Comparator.comparingLong(Long2LongMap.Entry::getLongValue)).toArray()) {
                        var entry = ((Long2LongMap.Entry) chunkHash);
                        if(minPriority == -1) minPriority = entry.getLongValue();
                        if(reserve.size() < (minPriority < 2 ? 4 : 2)) {
                            long hash = entry.getLongKey();
                            chunkQueue.remove(hash);
                            reserve.add(hash);
                        }
                    }

                    ObjectOpenHashSet<ChunkData> chunkData = new ObjectOpenHashSet<>();
                    for(long hash : reserve) {
                        chunkData.add(getChunkData(hash));
                    }
                    ChunkCompletion data = new ChunkCompletion();
                    data.levelName = getName();
                    data.chunks = chunkData.toArray(ChunkData[]::new);
                    PaperBridge.get().getSocket().send(data);
                    Bukkit.getLogger().info("Sending " + reserve.size() + " Chunks took " + (System.currentTimeMillis() - startTime) + " ms");
                });
                thread.start();
                chunkPacketThreads.add(thread);
            }
        }
    }

    public ChunkData getChunkData(Long hash) {
        int x = ChunkHash.getHashX(hash);
        int z = ChunkHash.getHashZ(hash);

        Chunk rChunk = getWorld().getChunkAt(x, z);
        ChunkSnapshot chunk = rChunk.getWorld().getChunkAt(x, z).getChunkSnapshot(false, true, false);
        ObjectOpenHashSet<BlockData> blocks = new ObjectOpenHashSet<>();
        int minHeight = getMinHeight(dimension);
        int maxHeight = getMaxHeight(dimension);
        for(byte _x = 0; _x < 16; _x++) {
            for(byte _z = 0; _z < 16; _z++) {
                for(int _y = minHeight; _y <= maxHeight; _y++) {
                    String state = chunk.getBlockData(_x, _y, _z).getAsString();
                    BlockData blockData = new BlockData();
                    BlockVector blockVector = new BlockVector();
                    blockVector.x = _x;
                    blockVector.y = _y;
                    blockVector.z = _z;
                    blockData.vector = blockVector;
                    blockData.blockState = state;
                    blockData.biome = chunk.getBiome(_x, _y, _z).getKey().getKey();
                    blocks.add(blockData);
                }
            }
        }

        ChunkData data = new ChunkData();
        data.chunkHash = hash;
        data.blockData = blocks.toArray(BlockData[]::new);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PaperBridge.get(), () -> rChunk.unload(false));
        return data;
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
