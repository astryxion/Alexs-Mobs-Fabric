package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.misc.AMEntityHooks;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {

    /**
     * Vanilla refuses passengers whose type cannot serialize. Several mobs mount the player and must be allowed anyway.
     */
    @ModifyExpressionValue(
            method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;canSerialize()Z")
    )
    private boolean alexsmobs$allowRidingUnsaveableVehicle(boolean original) {
        return original || AMEntityHooks.ridesUnsaveableVehicles((Entity) (Object) this);
    }
}
