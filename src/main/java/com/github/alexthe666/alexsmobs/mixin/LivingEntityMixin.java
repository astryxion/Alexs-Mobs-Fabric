package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.event.ServerEvents;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric: Invoke Alex's Mobs living-entity tick logic per entity when it ticks (server-only).
 * Replaces the previous full-world getEntitiesOfClass(LivingEntity.class, WORLD_BORDER_AABB) scan
 * every tick, which was O(world) and destroyed TPS.
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void alexsmobs$onLivingTick(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.level().isClientSide) {
            ServerEvents.onLivingEntityTick(self);
        }
    }
}
