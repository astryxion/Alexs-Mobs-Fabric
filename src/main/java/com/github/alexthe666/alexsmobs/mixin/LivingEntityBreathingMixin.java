package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.entity.IAMUnderwaterBreather;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityBreathingMixin {

    @Inject(method = "canBreatheUnderwater", at = @At("HEAD"), cancellable = true)
    private void alexsmobs$canBreatheUnderwaterAM(CallbackInfoReturnable<Boolean> cir) {
        Object self = this;
        if (self instanceof IAMUnderwaterBreather breather) {
            cir.setReturnValue(breather.canBreatheUnderwaterAM());
        }
    }
}
