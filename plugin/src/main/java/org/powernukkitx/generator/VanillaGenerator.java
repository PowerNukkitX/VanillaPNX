package org.powernukkitx.generator;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.terra.mappings.JeBlockState;
import cn.nukkit.level.generator.terra.mappings.MappingRegistries;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.Utils;
import lombok.Getter;
import org.powernukkitx.VanillaPNX;
import org.powernukkitx.packet.BlockEntityDataPacket;
import org.powernukkitx.packet.objects.*;

import java.util.Map;
import java.util.stream.Collectors;

public class VanillaGenerator extends Generator {

    @Getter
    private final static GenerationQueue queue = new GenerationQueue();

    private final static Map<String, Integer> BIOMES = MappingRegistries.BIOME.get().entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    public static final String NAME = "vanilla";


    public VanillaGenerator(DimensionData dimensionData, Map<String, Object> options) {
        super(dimensionData, options);
    }


    @Override
    public void stages(GenerateStage.Builder builder) {
        builder.start(Registries.GENERATE_STAGE.get(VanillaGenerateStage.NAME));
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static void applyTerrain(IChunk chunk, BlockData[] terrain) {
        new Thread(() -> {
            boolean blockEntity = false;
            for(BlockData element : terrain) {
                BlockVector vector = element.vector;
                int x = vector.x;
                int y = vector.y;
                int z = vector.z;

                String rawState = element.blockState;
                chunk.setBiomeId(x, y, z, BIOMES.getOrDefault("minecraft:" + element.biome, 0));
                if(shouldBeWaterlogged(rawState)) {
                    chunk.setBlockState(x, y, z, BlockWater.PROPERTIES.getDefaultState(), 1);
                }
                BlockState state = MappingRegistries.BLOCKS.getPNXBlock(new JeBlockState(rawState));
                if(state == null) state = BlockUnknown.PROPERTIES.getDefaultState();
                if(state.toBlock() instanceof BlockEntityHolder<?>) blockEntity = true;
                chunk.setBlockState(x, y, z, state);
            }
            if(blockEntity) {
                BlockEntityDataPacket blockEntityData = new BlockEntityDataPacket();
                blockEntityData.levelName = chunk.getLevel().getName();
                blockEntityData.chunkHash = Level.chunkHash(chunk.getX(), chunk.getZ());
                VanillaPNX.get().getWrapper().getSocket().send(blockEntityData);
            }
            if (Server.getInstance().getSettings().chunkSettings().lightUpdates()) {
                chunk.populateSkyLight();
                chunk.setLightPopulated();
            }
            for(Player player : chunk.getLevel().getPlayers().values()) {
                chunk.getLevel().requestChunk(chunk.getX(), chunk.getZ(), player);
            }
            chunk.setChunkState(ChunkState.FINISHED);
        }).start();
    }

    public static void applyEntity(String levelName, EntityData[] entityData) {
        for(EntityData data : entityData) {
            Position position = new Position(data.x, data.y, data.z, Server.getInstance().getLevelByName(levelName));
            Entity entity = Registries.ENTITY.provideEntity(getEntityName(data.entity.toLowerCase()), position.getChunk(), Entity.getDefaultNBT(position));
            if(entity != null) {
                entity.spawnToAll();
            } else VanillaPNX.get().getLogger().error("Entity " + data.entity + " was not spawnable!");
        }
    }

    public static void applyBlockEntity(String levelName, BlockEntityData[] blockEntityData) {
        Level level = Server.getInstance().getLevelByName(levelName);
        for(BlockEntityData data : blockEntityData) {
            BlockVector vector = data.vector;
            Location location = new Location(vector.x, vector.y, vector.z, level);
            if(level.getBlock(location) instanceof BlockEntityHolder<?> holder) {
                if(holder.getOrCreateBlockEntity() instanceof InventoryHolder inventoryHolder) {
                    for(ItemData itemData : data.items) {
                        Item item = Item.get("minecraft:" + itemData.id);
                        if(item.isNull()) continue;
                        item.setCount(itemData.count);
                        item.setDamage(itemData.damage);
                        for(EnchantmentData enchantmentData : itemData.enchantments) {
                            try {
                                Enchantment enchantment = Enchantment.getEnchantment(getEnchantmentId(enchantmentData.id));
                                if (enchantment != null) {
                                    enchantment.setLevel(enchantmentData.level);
                                    item.addEnchantment(enchantment);
                                } else VanillaPNX.get().getLogger().error("Enchantment " + enchantmentData.id + " does not exist.");
                            } catch (Exception e) {
                                VanillaPNX.get().getLogger().error("Enchantment " + enchantmentData.id + " does not exist.");
                            }
                        }
                        Inventory inventory = inventoryHolder.getInventory();
                        while(true) {
                            int randomSlot = Utils.rand(0, inventory.getSize());
                            if(inventory.getItem(randomSlot).isNull()) {
                                inventory.setItem(randomSlot, item);
                                break;
                            }
                        }
                        Block block = level.getBlock(location);
                        level.setBlock(block, block);
                    }
                }
            }
        }
    }

    private static boolean shouldBeWaterlogged(String rawState) {
        if(rawState.contains("waterlogged=true")) return true;
        if(rawState.contains("seagrass")) return true;
        if(rawState.startsWith("minecraft:kelp")) return true;
        if(rawState.contains("bubble_column")) return true;
        return false;
    }

    private static String getEntityName(String id) {
        return switch (id) {
            case "villager" -> EntityID.VILLAGER_V2;
            case "zombified_piglin" -> EntityID.ZOMBIE_PIGMAN;
            case "evoker" -> EntityID.EVOCATION_ILLAGER;
            case "end_crystal" -> EntityID.ENDER_CRYSTAL;
            default -> "minecraft:" + id.replace(" ", "_");
        };
    }

    private static String getEnchantmentId(String id) {
        return switch (id) {
            case "binding_curse" -> Enchantment.NAME_BINDING_CURSE;
            case "vanishing_curse" -> Enchantment.NAME_VANISHING_CURSE;
            default -> id;
        };
    }
}
