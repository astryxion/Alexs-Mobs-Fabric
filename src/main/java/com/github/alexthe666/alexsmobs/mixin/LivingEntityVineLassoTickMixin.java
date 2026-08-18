package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.entity.util.VineLassoUtil;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityVineLassoTickMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void alexsmobs$tickVineLasso(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.level().isClientSide() && VineLassoUtil.hasLassoData(self)) {
            VineLassoUtil.tickLasso(self);
        }
    }
}
