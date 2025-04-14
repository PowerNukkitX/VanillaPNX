package org.powernukkitx.vanillagen.listener;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.powernukkitx.vanillagen.PaperBridge;
import org.powernukkitx.vanillagen.packet.ChunkTerrainDataPacket;
import org.powernukkitx.vanillagen.packet.PopulationPacket;
import org.powernukkitx.vanillagen.packet.objects.ChunkData;
import org.powernukkitx.vanillagen.packet.objects.EntityData;
import org.powernukkitx.vanillagen.packet.objects.entity.EntityExtra;
import org.powernukkitx.vanillagen.packet.objects.entity.ItemFrameData;
import org.powernukkitx.vanillagen.utils.ChunkHash;
import org.powernukkitx.vanillagen.utils.WorldInfo;


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
                entityData.entity = entity.getType().getKey().getKey();
                writeExtra(entity, entityData);
                entities.add(entityData);
            }


            PopulationPacket packet = new PopulationPacket();
            packet.levelName = world.getName();
            packet.entityData = entities.toArray(EntityData[]::new);
            PaperBridge.get().getSocket().send(packet);
        }

    }

    private void writeExtra(Entity entity, EntityData data) {
        if(entity instanceof ItemFrame itemFrame) {
            ItemFrameData itemFrameData = new ItemFrameData();
            itemFrameData.face = itemFrame.getFacing().name();
            itemFrameData.item = itemFrame.getItem().getType().name();
            data.extras = new EntityExtra[] {itemFrameData};
        }
    }
    
}
