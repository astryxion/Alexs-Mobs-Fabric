package com.github.alexthe666.alexsmobs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;


public class BlockSandCircle extends FallingBlock {

    public BlockSandCircle(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return ((ColoredFallingBlock) Blocks.SAND).getDustColor(Blocks.SAND.defaultBlockState(), level, pos);
    }
}
