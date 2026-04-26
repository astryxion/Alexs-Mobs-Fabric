package com.github.alexthe666.alexsmobs.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;

public class BlockSkunkSpray extends MultifaceBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<BlockSkunkSpray> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(propertiesCodec()).apply(instance, BlockSkunkSpray::new));


    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

    public static BlockBehaviour.Properties defaultProperties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).noOcclusion().randomTicks().noCollision().instabreak().sound(SoundType.FROGSPAWN);
    }

    public BlockSkunkSpray(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false)).setValue(AGE, 0));
    }

    @Override
    public MapCodec<BlockSkunkSpray> codec() {
        return CODEC;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader reader, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(reader));
        }

        return super.updateShape(state, reader, tickAccess, pos, direction, neighborPos, neighborState, random);
    }

    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        this.tick(state, level, pos, randomSource);
    }

    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(8) == 0) {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            for (Direction direction : Direction.values()) {
                blockpos$mutableblockpos.setWithOffset(pos, direction);
                BlockState blockstate = level.getBlockState(blockpos$mutableblockpos);
                if (blockstate.is(this) && !this.incrementAge(blockstate, level, blockpos$mutableblockpos)) {
                    level.scheduleTick(blockpos$mutableblockpos, this, Mth.nextInt(random, 50, 100));
                }
            }
            this.incrementAge(state, level, pos);
        } else {
            level.scheduleTick(pos, this, Mth.nextInt(random, 50, 100));
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> definition) {
        super.createBlockStateDefinition(definition);
        definition.add(AGE);
    }

    public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player, BlockHitResult hit) {
        ItemStack itemStack = player.getMainHandItem();
        int setContent = -1;
        if(itemStack.is(Items.GLASS_BOTTLE)) {
           Direction dir = hit.getDirection().getOpposite();
           if(hasFace(state, dir)){
               worldIn.setBlockAndUpdate(pos, removeStinkFace(state, dir));
               ItemStack bottle = new ItemStack(AMItemRegistry.STINK_BOTTLE);
               if(!player.addItem(bottle)){
                   player.drop(bottle, false);
               }
               if(!player.isCreative()){
                   itemStack.shrink(1);
               }
               return InteractionResult.SUCCESS;
           }
        }
        return super.useWithoutItem(state, worldIn, pos, player, hit);
    }

    public static BlockState removeStinkFace(BlockState state, Direction faceProperty) {
        BlockState blockstate = state.setValue(getFaceProperty(faceProperty), Boolean.valueOf(false));
        return hasAnyFace(blockstate) ? blockstate : Blocks.AIR.defaultBlockState();
    }
    private boolean incrementAge(BlockState state, Level level, BlockPos pos) {
        int i = state.getValue(AGE);
        if (i < 3) {
            level.setBlock(pos, state.setValue(AGE, Integer.valueOf(i + 1)), 2);
            return false;
        } else {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            return true;
        }
    }


    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return !context.getItemInHand().is(AMBlockRegistry.SKUNK_SPRAY.asItem()) || super.canBeReplaced(state, context);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    public void animateTick(BlockState blockState, Level level, BlockPos pos, RandomSource randomSource) {
        if (randomSource.nextInt(2) == 0) {
            ArrayList<Direction> faces = new ArrayList<Direction>(availableFaces(blockState));
            Direction direction = null;
            if (faces.size() == 1) {
                direction = faces.get(0);
            } else if (faces.size() > 1) {
                direction = Util.getRandom(faces, randomSource);
            }
            if (direction != null) {
                double d0 = direction.getStepX() == 0 ? randomSource.nextDouble() : 0.5D + (double) direction.getStepX() * 0.8D;
                double d1 = direction.getStepY() == 0 ? randomSource.nextDouble() : 0.5D + (double) direction.getStepY() * 0.8D;
                double d2 = direction.getStepZ() == 0 ? randomSource.nextDouble() : 0.5D + (double) direction.getStepZ() * 0.8D;
                level.addParticle(AMParticleRegistry.SMELLY, (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
