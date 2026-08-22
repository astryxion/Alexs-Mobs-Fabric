package com.github.alexthe666.citadel.server.entity.collision;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.PathType;
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

    /** Public for use by pathfinding; uses reflection for 1.20.1 BlockState path type API. */
    public static PathType getBlockPathTypeFromState(BlockState state, BlockGetter level, BlockPos pos) {
        if (BLOCK_STATE_GET_PATH_TYPE == null) return null;
        try {
            return (PathType) BLOCK_STATE_GET_PATH_TYPE.invoke(state, level, pos);
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

    /** Public for use by pathfinding; uses reflection for 1.20.1 BlockState isLadder API. */
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

    protected PathType getBlockPathTypeStatic(PathfindingContext context, BlockGetter level, BlockPos.MutableBlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        PathType pathnodetype = getNodes(level, pos);
        if (pathnodetype == PathType.OPEN && j >= 1) {
            PathType pathnodetype1 = getNodes(level, pos.set(i, j - 1, k));
            pathnodetype = pathnodetype1 != PathType.WALKABLE && pathnodetype1 != PathType.OPEN && pathnodetype1 != PathType.WATER && pathnodetype1 != PathType.LAVA ? PathType.WALKABLE : PathType.OPEN;
            if (pathnodetype1 == PathType.DAMAGE_FIRE) {
                pathnodetype = PathType.DAMAGE_FIRE;
            }

            if (pathnodetype1 == PathType.DAMAGE_OTHER) {
                pathnodetype = PathType.DAMAGE_OTHER;
            }

            if (pathnodetype1 == PathType.STICKY_HONEY) {
                pathnodetype = PathType.STICKY_HONEY;
            }
        }

        if (pathnodetype == PathType.WALKABLE) {
            pathnodetype = checkNeighbourBlocks(context, i, j, k, pathnodetype);
        }

        if (pathnodetype != null && this.mob instanceof ICustomCollisions) {
            BlockState state = level.getBlockState(pos);
            if (((ICustomCollisions) this.mob).canPassThrough(pos, state, state.getBlockSupportShape(level, pos))) {
                return PathType.OPEN;
            }
        }
        return pathnodetype;
    }


    protected static PathType getNodes(BlockGetter p_237238_0_, BlockPos p_237238_1_) {
        BlockState blockstate = p_237238_0_.getBlockState(p_237238_1_);
        PathType type = getBlockPathTypeFromState(blockstate, p_237238_0_, p_237238_1_);
        if (type != null) return type;
        if (blockstate.isAir()) {
            return PathType.OPEN;
        } else if (blockstate.getBlock() == Blocks.BAMBOO) {
            return PathType.OPEN;
        } else {
            return getPathTypeFromState(p_237238_0_, p_237238_1_);
        }
    }

    @Override
    public PathType getPathType(PathfindingContext context, int x, int y, int z) {
        return getBlockPathTypeStatic(context, context.level(), new BlockPos.MutableBlockPos(x, y, z));
    }

    @Override
    public PathType getPathType(net.minecraft.world.entity.Mob mob, BlockPos pos) {
        PathfindingContext ctx = this.currentContext;
        return ctx != null ? getPathType(ctx, pos.getX(), pos.getY(), pos.getZ()) : super.getPathType(mob, pos);
    }
}