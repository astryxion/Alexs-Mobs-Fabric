package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.event.ServerEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {

    @Inject(method = "canEntityWalkOnPowderSnow", at = @At("HEAD"), cancellable = true)
    private static void alexsmobs$bisonFur(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof LivingEntity living && ServerEvents.hasBisonFurBoots(living)) {
            cir.setReturnValue(true);
        }
    }
}
