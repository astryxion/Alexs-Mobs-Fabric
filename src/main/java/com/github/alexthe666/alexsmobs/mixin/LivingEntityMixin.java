package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.event.ServerEvents;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric: Invoke Alex's Mobs living-entity tick logic per entity when it ticks.
 * Matches NeoForge {@code EntityTickEvent.Post} (both sides) so movement-based armor
 * (rocky chestplate, centipede leggings, flying fish boots) applies on the client player.
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void alexsmobs$onLivingTick(CallbackInfo ci) {
        ServerEvents.onLivingEntityTick((LivingEntity) (Object) this);
    }
}
