package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.event.ServerEvents;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric: per-entity armor tick logic (both sides; server-only work is guarded inside ServerEvents).
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void alexsmobs$onLivingTick(CallbackInfo ci) {
        ServerEvents.onLivingEntityTick((LivingEntity) (Object) this);
    }
}
