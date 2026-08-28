package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.entity.EntityMimicube;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** EntityMimicube custom jump; LivingEntity.jumpFromGround() is final in 1.21.1. */
@Mixin(LivingEntity.class)
public class LivingEntityJumpMixin {

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void alexsmobs$mimicubeJump(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof EntityMimicube mimicube) {
            mimicube.applyMimicubeJump();
            ci.cancel();
        }
    }
}
