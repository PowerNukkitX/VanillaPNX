package org.powernukkitx.vanillagen.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.event.level.LevelUnloadEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.network.protocol.LevelChunkPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.powernukkitx.vanillagen.generator.VanillaGenerator;

import java.util.Objects;

public class ChunkSendManager implements Listener {

    private static final Int2ObjectArrayMap<Long2ObjectArrayMap<ObjectArraySet<Player>>> chunkQueue = new Int2ObjectArrayMap<>();

    public static void sendChunk(IChunk chunk) {
        Level level = chunk.getLevel();
        if(chunkQueue.containsKey(level.getId())) {
            Long2ObjectArrayMap<ObjectArraySet<Player>> chunks = chunkQueue.get(level.getId());
            long chunkHash = Level.chunkHash(chunk.getX(), chunk.getZ());
            if(chunks.containsKey(chunkHash)) {
                ObjectArraySet<Player> players = chunks.get(chunkHash);
                final var pair = chunk.getLevel().requireProvider().requestChunkData(chunk.getX(), chunk.getZ());
                for (Player player : Objects.requireNonNull(players)) {
                    if (player.isConnected()) {
                        LevelChunkPacket pk = new LevelChunkPacket();
                        pk.chunkX = chunk.getX();
                        pk.chunkZ = chunk.getZ();
                        pk.dimension = chunk.getLevel().getDimension();
                        pk.subChunkCount = pair.right();
                        pk.data = pair.left();
                        player.sendChunk(pk.chunkX, pk.chunkZ, pk);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChunkSend(DataPacketSendEvent event) {
        if(event.getPacket() instanceof LevelChunkPacket pk) {
            Player player = event.getPlayer();
            Level level = player.getLevel();
            if(level.getChunk(pk.chunkX, pk.chunkZ).getChunkState() != ChunkState.FINISHED) event.setCancelled();
            if(!chunkQueue.containsKey(level.getId())) chunkQueue.put(level.getId(), new Long2ObjectArrayMap<>());
            if(level.getGenerator() instanceof VanillaGenerator) {
                Long2ObjectArrayMap<ObjectArraySet<Player>> requests = chunkQueue.get(level.getId());
                long chunkHash = Level.chunkHash(pk.chunkX, pk.chunkZ);
                if(!requests.containsKey(chunkHash)) requests.put(chunkHash, new ObjectArraySet<>());
                ObjectArraySet<Player> players = requests.get(chunkHash);
                if(players.contains(player)) {
                    players.remove(player);
                    if(players.isEmpty()) requests.remove(chunkHash);
                } else players.add(player);
            }
        }
    }

    @EventHandler
    public void onLevelLoad(LevelLoadEvent event) {
        Level level = event.getLevel();
        if(level.getGenerator() instanceof VanillaGenerator) {
            chunkQueue.put(level.getId(), new Long2ObjectArrayMap<>());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(LevelUnloadEvent event) {
        if(!event.isCancelled()) {
            Level level = event.getLevel();
            if(level.getGenerator() instanceof VanillaGenerator) {
                chunkQueue.remove(level.getId());
            }
        }
    }

}
