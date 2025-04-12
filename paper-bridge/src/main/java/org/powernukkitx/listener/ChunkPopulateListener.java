package org.powernukkitx.listener;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.powernukkitx.PaperBridge;
import org.powernukkitx.packet.ChunkTerrainDataPacket;
import org.powernukkitx.packet.PopulationPacket;
import org.powernukkitx.packet.objects.ChunkData;
import org.powernukkitx.packet.objects.EntityData;
import org.powernukkitx.utils.ChunkHash;
import org.powernukkitx.utils.WorldInfo;

public class ChunkPopulateListener implements Listener {
    
    @EventHandler
    public void on(ChunkPopulateEvent event) {
        World world = event.getWorld();
        Chunk chunk = event.getChunk();
        if(PaperBridge.get().getSocket().getServer().getWorlds().containsKey(world.getName())) {
            new Thread(() -> {
                if (!PaperBridge.get().getSocket().getServer().getWorlds().get(world.getName()).getChunkQueue().containsKey(ChunkHash.chunkHash(chunk.getX(), chunk.getZ()))) {
                    ChunkTerrainDataPacket data = new ChunkTerrainDataPacket();
                    data.levelName = world.getName();
                    WorldInfo info = PaperBridge.get().getSocket().getServer().getWorlds().get(world.getName());
                    data.chunks = new ChunkData[]{info.getChunkData(ChunkHash.chunkHash(chunk.getX(), chunk.getZ()))};
                    PaperBridge.get().getSocket().send(data);
                }
            }).start();
            ObjectOpenHashSet<EntityData> entities = new ObjectOpenHashSet<>();
            for (Entity entity : chunk.getEntities()) {
                EntityData entityData = new EntityData();
                entityData.x = entity.getX();
                entityData.y = entity.getY();
                entityData.z = entity.getZ();
                entityData.entity = entity.getName();
                entities.add(entityData);
            }
            if (!entities.isEmpty()) {
                PopulationPacket packet = new PopulationPacket();
                packet.levelName = world.getName();
                packet.entityData = entities.toArray(EntityData[]::new);
                PaperBridge.get().getSocket().send(packet);
            }
        }

    }
    
}
