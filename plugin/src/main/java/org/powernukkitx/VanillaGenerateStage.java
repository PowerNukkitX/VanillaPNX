package org.powernukkitx;

import cn.nukkit.block.BlockBarrier;
import cn.nukkit.block.BlockInvisibleBedrock;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;

public class VanillaGenerateStage extends GenerateStage {
    public static final String NAME = "vanilla_generatestage";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void apply(ChunkGenerateContext chunkGenerateContext) {
        IChunk chunk = chunkGenerateContext.getChunk();
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                for(int y = chunkGenerateContext.getLevel().getMinHeight(); y <= chunkGenerateContext.getLevel().getMaxHeight(); y++) {
                    chunk.setBlockState(x, y, z, BlockInvisibleBedrock.PROPERTIES.getDefaultState());
                }
            }
        }
        chunk.setChunkState(ChunkState.POPULATED);
    }
};
