package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

/**
 * Places a ring of frosted ice under an entity, matching vanilla frost-walker survival/obstruction checks.
 */
public final class AMFrostWalker {
    private AMFrostWalker() {
    }

    public static void freezeAround(LivingEntity entity, Level level, BlockPos pos, int radiusLevel) {
        if (!entity.onGround()) {
            return;
        }
        BlockState frostedIce = Blocks.FROSTED_ICE.defaultBlockState();
        float radius = (float) Math.min(16, 2 + radiusLevel);
        BlockPos.MutableBlockPos above = new BlockPos.MutableBlockPos();
        for (BlockPos candidate : BlockPos.betweenClosed(
                pos.offset((int) (-radius), -1, (int) (-radius)),
                pos.offset((int) radius, -1, (int) radius))) {
            if (!candidate.closerToCenterThan(entity.position(), radius)) {
                continue;
            }
            above.set(candidate.getX(), candidate.getY() + 1, candidate.getZ());
            if (!level.getBlockState(above).isAir()) {
                continue;
            }
            BlockState state = level.getBlockState(candidate);
            if (state.is(Blocks.WATER)
                    && state.getFluidState().isSource()
                    && frostedIce.canSurvive(level, candidate)
                    && level.isUnobstructed(frostedIce, candidate, CollisionContext.empty())) {
                level.setBlockAndUpdate(candidate, frostedIce);
                level.scheduleTick(candidate.immutable(), Blocks.FROSTED_ICE, Mth.nextInt(entity.getRandom(), 60, 120));
            }
        }
    }
}
