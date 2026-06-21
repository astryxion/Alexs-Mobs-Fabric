package com.github.alexthe666.alexsmobs.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.state.BlockBehaviour.simpleCodec;

public class BlockRedSandCircle extends FallingBlock {

    public static final MapCodec<BlockRedSandCircle> CODEC = simpleCodec(BlockRedSandCircle::new);

    public BlockRedSandCircle(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return ((ColoredFallingBlock) Blocks.RED_SAND).getDustColor(Blocks.RED_SAND.defaultBlockState(), level, pos);
    }

    @Override
    public MapCodec<? extends FallingBlock> codec() {
        return CODEC;
    }
}
