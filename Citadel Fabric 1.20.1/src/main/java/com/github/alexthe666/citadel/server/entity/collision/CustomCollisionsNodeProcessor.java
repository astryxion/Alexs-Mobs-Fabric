package com.github.alexthe666.citadel.server.entity.collision;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class CustomCollisionsNodeProcessor extends WalkNodeEvaluator {

    private static final java.lang.reflect.Method BLOCK_STATE_GET_PATH_TYPE;

    static {
        java.lang.reflect.Method m = null;
        try {
            m = BlockState.class.getMethod("getBlockPathType", BlockGetter.class, BlockPos.class);
        } catch (NoSuchMethodException e) {
            try {
                m = BlockState.class.getMethod("getPathTypeTo", BlockGetter.class, BlockPos.class);
            } catch (NoSuchMethodException ignored) {
            }
        }
        BLOCK_STATE_GET_PATH_TYPE = m;
    }

    /** Public for use by pathfinding; uses reflection for BlockState path type API when present. */
    public static BlockPathTypes getBlockPathTypeFromState(BlockState state, BlockGetter level, BlockPos pos) {
        if (BLOCK_STATE_GET_PATH_TYPE == null) return null;
        try {
            return (BlockPathTypes) BLOCK_STATE_GET_PATH_TYPE.invoke(state, level, pos);
        } catch (Throwable e) {
            return null;
        }
    }

    private static final java.lang.reflect.Method BLOCK_STATE_IS_LADDER;

    static {
        java.lang.reflect.Method m = null;
        try {
            m = BlockState.class.getMethod("isLadder", BlockGetter.class, BlockPos.class);
        } catch (NoSuchMethodException e) {
            try {
                m = BlockState.class.getMethod("isLadder", net.minecraft.world.level.LevelReader.class, BlockPos.class);
            } catch (NoSuchMethodException ignored) {
            }
        }
        BLOCK_STATE_IS_LADDER = m;
    }

    /** Public for use by pathfinding; uses reflection for BlockState isLadder API when present. */
    public static boolean isLadder(BlockState state, BlockGetter level, BlockPos pos) {
        if (BLOCK_STATE_IS_LADDER == null) return false;
        try {
            return Boolean.TRUE.equals(BLOCK_STATE_IS_LADDER.invoke(state, level, pos));
        } catch (Throwable e) {
            return false;
        }
    }

    public CustomCollisionsNodeProcessor() {
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter level, int x, int y, int z) {
        return getCustomBlockPathType(level, new BlockPos.MutableBlockPos(x, y, z));
    }

    private BlockPathTypes getCustomBlockPathType(BlockGetter level, BlockPos.MutableBlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        BlockPathTypes pathnodetype = getNodes(level, pos);
        if (pathnodetype == BlockPathTypes.OPEN && j >= 1) {
            BlockPathTypes pathnodetype1 = getNodes(level, pos.set(i, j - 1, k));
            pathnodetype = pathnodetype1 != BlockPathTypes.WALKABLE && pathnodetype1 != BlockPathTypes.OPEN && pathnodetype1 != BlockPathTypes.WATER && pathnodetype1 != BlockPathTypes.LAVA ? BlockPathTypes.WALKABLE : BlockPathTypes.OPEN;
            if (pathnodetype1 == BlockPathTypes.DAMAGE_FIRE) {
                pathnodetype = BlockPathTypes.DAMAGE_FIRE;
            }

            if (pathnodetype1 == BlockPathTypes.DAMAGE_OTHER) {
                pathnodetype = BlockPathTypes.DAMAGE_OTHER;
            }

            if (pathnodetype1 == BlockPathTypes.STICKY_HONEY) {
                pathnodetype = BlockPathTypes.STICKY_HONEY;
            }
        }

        if (pathnodetype == BlockPathTypes.WALKABLE) {
            pathnodetype = checkNeighbourBlocks(level, pos.set(i, j, k), pathnodetype);
        }

        if (pathnodetype != null && this.mob instanceof ICustomCollisions) {
            BlockState state = level.getBlockState(pos);
            if (((ICustomCollisions) this.mob).canPassThrough(pos, state, state.getBlockSupportShape(level, pos))) {
                return BlockPathTypes.OPEN;
            }
        }
        return pathnodetype;
    }

    protected static BlockPathTypes getNodes(BlockGetter level, BlockPos pos) {
        BlockState blockstate = level.getBlockState(pos);
        BlockPathTypes type = getBlockPathTypeFromState(blockstate, level, pos);
        if (type != null) return type;
        if (blockstate.isAir()) {
            return BlockPathTypes.OPEN;
        } else if (blockstate.getBlock() == Blocks.BAMBOO) {
            return BlockPathTypes.OPEN;
        } else {
            return getBlockPathTypeRaw(level, pos);
        }
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter level, int x, int y, int z, Mob mob) {
        this.mob = mob;
        return this.getBlockPathType(level, x, y, z);
    }
}
