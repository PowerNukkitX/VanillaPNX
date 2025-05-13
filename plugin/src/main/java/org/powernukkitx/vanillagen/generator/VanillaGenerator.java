package org.powernukkitx.vanillagen.generator;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.event.player.PlayerChunkRequestEvent;
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
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelChunkPacket;
import cn.nukkit.network.protocol.NetworkChunkPublisherUpdatePacket;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.Utils;
import lombok.Getter;
import org.powernukkitx.vanillagen.VanillaPNX;
import org.powernukkitx.vanillagen.listener.ChunkSendManager;
import org.powernukkitx.vanillagen.packet.BlockEntityDataPacket;
import org.powernukkitx.vanillagen.packet.objects.*;
import org.powernukkitx.vanillagen.packet.objects.entity.EntityExtra;
import org.powernukkitx.vanillagen.packet.objects.entity.ItemFrameData;

import java.util.Map;
import java.util.Objects;
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
                chunk.setBiomeId(x, y, z, BIOMES.getOrDefault("minecraft:" + element.biome, chunk.getLevel().getDimension() == 2 ? BIOMES.get("minecraft:the_end") : 0));
                if(shouldBeWaterlogged(rawState)) {
                    chunk.setBlockState(x, y, z, BlockWater.PROPERTIES.getDefaultState(), 1);
                }
                BlockState state = MappingRegistries.BLOCKS.getPNXBlock(new JeBlockState(rawState));
                if(state == null) {
                    Server.getInstance().getLogger().info("Received block from Paper Subserver which cannot be parsed: " + rawState);
                    state = BlockUnknown.PROPERTIES.getDefaultState();
                }
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
            if(VanillaPNX.get().getConfig().getBoolean("save-chunks")) chunk.getProvider().saveChunk(chunk.getX(), chunk.getZ(), chunk);
            ChunkSendManager.sendChunk(chunk);

        }).start();
    }

    public static void applyEntity(String levelName, EntityData[] entityData) {
        for(EntityData data : entityData) {
            Position position = new Position(data.x, data.y, data.z, Server.getInstance().getLevelByName(levelName));
            String entityId = getEntityName(data);
            for (EntityExtra entityExtra : data.extras) {
                if (entityExtra instanceof ItemFrameData itemFrameData) {
                    position.getLevel().getScheduler().scheduleDelayedTask(() -> {
                        BlockState state = BlockFrame.PROPERTIES.getBlockState(CommonBlockProperties.FACING_DIRECTION, BlockFace.valueOf(itemFrameData.face).ordinal());
                        Level level = position.getLevel();
                        level.setBlockStateAt(position.getFloorX(), position.getFloorY(), position.getFloorZ(), state);
                        Block block = level.getBlock(position);
                        BlockEntityItemFrame entity = ((BlockEntityHolder<BlockEntityItemFrame>) block).getOrCreateBlockEntity();
                        entity.setItem(Item.get("minecraft:" + itemFrameData.item.toLowerCase()));
                        entity.spawnToAll();
                    }, 10);
                }
            }
            if(entityId == null) return;
            Entity entity = Registries.ENTITY.provideEntity(entityId, position.getChunk(), Entity.getDefaultNBT(position));
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
                                String enchantmentId = getEnchantmentId(enchantmentData.id);
                                if(enchantmentId == null) continue;
                                Enchantment enchantment = Enchantment.getEnchantment(enchantmentId);
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

    private static String getEntityName(EntityData data) {
        return switch (data.entity.toLowerCase()) {
            case "villager" -> EntityID.VILLAGER_V2;
            case "zombified_piglin" -> EntityID.ZOMBIE_PIGMAN;
            case "evoker" -> EntityID.EVOCATION_ILLAGER;
            case "end_crystal" -> EntityID.ENDER_CRYSTAL;
            case "item_frame" -> null;
            default -> "minecraft:" + data.entity.replace(" ", "_");
        };
    }

    private static String getEnchantmentId(String id) {
        return switch (id) {
            case "binding_curse" -> Enchantment.NAME_BINDING_CURSE;
            case "vanishing_curse" -> Enchantment.NAME_VANISHING_CURSE;
            case "sweeping_edge" -> null;
            default -> id;
        };
    }
}
