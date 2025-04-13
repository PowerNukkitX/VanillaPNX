package org.powernukkitx.socket;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.joml.Vector2L;
import org.powernukkitx.NettySocketServer;
import org.powernukkitx.PaperBridge;
import org.powernukkitx.packet.*;
import org.powernukkitx.packet.objects.*;
import org.powernukkitx.utils.ChunkHash;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


public class PaperNettyImpl extends NettySocketServer {

    @Getter
    private static Long heartbeatTime = System.currentTimeMillis();
    
    @Getter
    protected PNXServer server;

    public PaperNettyImpl(int port) {
        super(port, -1);
    }

    @Override
    protected void onPacket(Packet packet) {
        if(packet instanceof ClientHelloPacket hello) {
            destinationPort = hello.port;
            server = new PNXServer(hello.port);
        } else if(packet instanceof ClientHeartbeatPacket) {
            heartbeatTime = System.currentTimeMillis();
        } else if(packet instanceof WorldInfoPacket info) {
            server.addWorldInfo(new org.powernukkitx.utils.WorldInfo(info.name, info.seed, info.dimension, server));
        } else if(packet instanceof ChunkRequestPacket request) {
            for(ChunkInfo element : request.chunks) {
                server.getWorlds().get(request.levelName).queueChunk(element.chunkHash, element.priority);
            }
        } else if(packet instanceof PlayerPositionUpdatePacket update) {
            for(LevelPlayerPosition position : update.positions) {
                ConcurrentHashMap<Long, Long> chunks = server.getWorlds().get(position.levelName).getChunkQueue();
                if(!chunks.isEmpty()) {
                    for (Long hash : chunks.keySet()) {
                        int x = ChunkHash.getHashX(hash);
                        int z = ChunkHash.getHashZ(hash);
                        Vector2L vec = new Vector2L(x, z);
                        long minDistance = Long.MAX_VALUE;
                        for (Long _hash : position.chunks) {
                            int _x = ChunkHash.getHashX(_hash);
                            int _z = ChunkHash.getHashZ(_hash);
                            long distance = (long) vec.distance(new Vector2L(_x, _z));
                            if (distance < minDistance) {
                                minDistance = distance;
                            }
                        }
                        chunks.put(hash, minDistance);
                    }
                }
            }
        } else if(packet instanceof ChunkThrowawayPacket throwaway) {
            for(LevelPlayerPosition position : throwaway.positions) {
                for(Long chunkHash : position.chunks) {
                    server.getWorlds().get(position.levelName).getChunkQueue().remove(chunkHash);
                }
            }
        } else if(packet instanceof BlockEntityDataPacket blockEntityData) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PaperBridge.get(), () -> {
                Chunk chunk = getServer().getWorlds().get(blockEntityData.levelName).getWorld().getChunkAt(ChunkHash.getHashX(blockEntityData.chunkHash), ChunkHash.getHashZ(blockEntityData.chunkHash));
                ObjectOpenHashSet<BlockEntityData> blockEntities = new ObjectOpenHashSet<>();
                for(BlockState blockState : chunk.getTileEntities()) {
                    BlockEntityData data = new BlockEntityData();
                    BlockVector vector = new BlockVector();
                    vector.x = blockState.getX();
                    vector.y = blockState.getY();
                    vector.z = blockState.getZ();
                    data.vector = vector;
                    data.state = blockState.getBlockData().getAsString();
                    ObjectOpenHashSet<ItemData> items = new ObjectOpenHashSet<>();
                    if(blockState instanceof Chest lootable) {
                        LootTable lootTable = lootable.getLootTable();
                        if(lootTable == null) continue;
                        LootContext context = new LootContext.Builder(blockState.getLocation()).build();
                        for (ItemStack item : lootTable.populateLoot(new Random(blockState.hashCode()), context)) {
                            ItemData itemData = new ItemData();
                            itemData.id = item.getType().getKey().getKey();
                            itemData.count = item.getAmount();
                            if(item.getItemMeta() instanceof Damageable damageable) {
                                itemData.damage = damageable.getDamage();
                            } else itemData.damage = 0;
                            ObjectOpenHashSet<EnchantmentData> enchantments = new ObjectOpenHashSet<>();
                            for(var enchantment : item.getEnchantments().entrySet()) {
                                EnchantmentData enchantmentData = new EnchantmentData();
                                enchantmentData.id = enchantment.getKey().getKey().getKey();
                                enchantmentData.level = enchantment.getValue();
                                enchantments.add(enchantmentData);
                            }
                            itemData.enchantments = enchantments.toArray(EnchantmentData[]::new);
                            items.add(itemData);
                        }
                    }
                    data.items = items.toArray(ItemData[]::new);
                    blockEntities.add(data);
                }
                BlockEntityDataPacket dataPacket = new BlockEntityDataPacket();
                dataPacket.levelName = blockEntityData.levelName;
                dataPacket.chunkHash = blockEntityData.chunkHash;
                dataPacket.blockEntities = blockEntities.toArray(BlockEntityData[]::new);
                send(dataPacket);
            });
        }
    }
}
