package org.powernukkitx.vanillagen.generator;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.ChunkVector2;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import lombok.Getter;
import org.powernukkitx.vanillagen.VanillaPNX;
import org.powernukkitx.vanillagen.packet.ChunkRequestPacket;
import org.powernukkitx.vanillagen.packet.objects.ChunkInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GenerationQueue {

    private static final ObjectArraySet<String> acknowledgedLevels = new ObjectArraySet<>();

    @Getter
    private static final HashMap<String, Long2LongOpenHashMap> requestedLevelChunks = new HashMap<>();


    private void tick(Level level) {
        List<ChunkInfo> chunks = new ArrayList<>();
        for(IChunk chunk : level.getChunks().values()) {
            if (chunk.getChunkState() != ChunkState.FINISHED) {
                if (!requestedLevelChunks.containsKey(level.getName())) {
                    requestedLevelChunks.put(level.getName(), new Long2LongOpenHashMap());
                }
                Long2LongOpenHashMap requestedChunks = requestedLevelChunks.get(level.getName());

                long hash = Level.chunkHash(chunk.getX(), chunk.getZ());
                    if (!requestedChunks.containsKey(hash)) {
                        ChunkInfo chunkInfo = new ChunkInfo();
                        chunkInfo.chunkHash = hash;
                        long minDistance = Long.MAX_VALUE;
                        ChunkVector2 looking = new ChunkVector2(chunk.getX(), chunk.getZ());
                        for(Player player : level.getPlayers().values()) {
                            ChunkVector2 playerChunk = new ChunkVector2(player.getChunkX(), player.getChunkZ());
                            long distance = (long) looking.distance(playerChunk);
                            if(distance < minDistance) minDistance = distance;
                        }
                        chunkInfo.priority = minDistance;
                        chunks.add(chunkInfo);
                        requestedChunks.put(hash, System.currentTimeMillis());
                    } else if(System.currentTimeMillis() - requestedChunks.get(hash) > 5000) {
                        requestedChunks.remove(hash);
                    }

            }
        }
        if(!chunks.isEmpty()) {
            ChunkRequestPacket request = new ChunkRequestPacket();
            request.levelName = level.getName();
            request.chunks = chunks.toArray(ChunkInfo[]::new);
            VanillaPNX.get().getWrapper().getSocket().send(request);
        }
    }

    public static void acknowledged(String levelName) {
        if(isAcknowledged(levelName)) return;
        acknowledgedLevels.add(levelName);
        Level level = Server.getInstance().getLevelByName(levelName);
        level.getScheduler().scheduleRepeatingTask(VanillaPNX.get(), () -> {
            if(isAcknowledged(level.getName())) {
                VanillaGenerator.getQueue().tick(level);
            }
        },1);
    }

    public static void addToReceived(String level, Long chunkHash) {

    }

    public static boolean isAcknowledged(String level) {
        return acknowledgedLevels.contains(level);
    }

}
