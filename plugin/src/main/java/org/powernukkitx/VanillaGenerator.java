package org.powernukkitx;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.terra.mappings.JeBlockState;
import cn.nukkit.level.generator.terra.mappings.MappingRegistries;
import cn.nukkit.registry.BiomeRegistry;
import cn.nukkit.registry.Registries;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.Map;
import java.util.stream.Collectors;

public class VanillaGenerator extends Generator {

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

    public static void applyData(IChunk chunk, JsonArray data) {
        new Thread(() -> {
            for(JsonElement element : data) {
                JsonArray blockData = element.getAsJsonArray();
                int x = blockData.get(0).getAsInt();
                int y = blockData.get(1).getAsInt();
                int z = blockData.get(2).getAsInt();

                String rawState = blockData.get(3).getAsString();
                chunk.setBiomeId(x, y, z, BIOMES.getOrDefault("minecraft:" + blockData.get(4).getAsString(), 0));
                if(shouldBeWaterlogged(rawState)) {
                    chunk.setBlockState(x, y, z, BlockWater.PROPERTIES.getDefaultState(), 1);
                }
                BlockState state = MappingRegistries.BLOCKS.getPNXBlock(new JeBlockState(rawState));
                if(state == null) state = BlockUnknown.PROPERTIES.getDefaultState();
                chunk.setBlockState(x, y, z, state);
            }
            chunk.populateSkyLight();
            chunk.setChunkState(ChunkState.FINISHED);
            for(Player player : chunk.getLevel().getPlayers().values()) {
                chunk.getLevel().requestChunk(chunk.getX(), chunk.getZ(), player);
            }
        }).start();
    }

    private static boolean shouldBeWaterlogged(String rawState) {
        if(rawState.contains("waterlogged=true")) return true;
        if(rawState.contains("seagrass")) return true;
        if(rawState.startsWith("minecraft:kelp")) return true;
        if(rawState.contains("bubble_column")) return true;
        return false;
    }
}
