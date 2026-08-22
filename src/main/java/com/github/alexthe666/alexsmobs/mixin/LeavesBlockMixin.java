package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {

    @Inject(method = "randomTick", at = @At("TAIL"))
    private void alexsmobs$dropBananaFromLeaves(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!AMConfig.bananasDropFromLeaves || AMConfig.bananaChance <= 0) {
            return;
        }
        if (!state.is(AMTagRegistry.DROPS_BANANAS)) {
            return;
        }
        if (random.nextInt(Math.max(1, AMConfig.bananaChance)) != 0) {
            return;
        }
        ItemEntity banana = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() - 0.2D, pos.getZ() + 0.5D, new ItemStack(AMItemRegistry.BANANA));
        banana.setDefaultPickUpDelay();
        level.addFreshEntity(banana);
    }
}
