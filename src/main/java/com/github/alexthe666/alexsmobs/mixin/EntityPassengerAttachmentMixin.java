package com.github.alexthe666.alexsmobs.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Crow shoulder seating is handled in EntityCrow.rideTick via setPos.
 * Do not also override passenger attachment here or the crow floats / jitters.
 */
@Mixin(Entity.class)
public class EntityPassengerAttachmentMixin {

    @Inject(method = "getPassengerAttachmentPoint", at = @At("HEAD"), cancellable = true)
    private void alexsmobs$crowShoulderOffset(Entity passenger, EntityDimensions dimensions, float scale, CallbackInfoReturnable<Vec3> cir) {
        // Intentionally no-op: dual positioning caused shoulder float/flapping bugs.
    }
}
