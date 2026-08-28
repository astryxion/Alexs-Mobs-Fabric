package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityFroststalker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class EntityFroststalkerLootMixin {

    @Inject(method = "getLootTable", at = @At("HEAD"), cancellable = true)
    private void alexsmobs$conditionalLootTables(CallbackInfoReturnable<ResourceLocation> cir) {
        Mob self = (Mob) (Object) this;
        if (self instanceof EntityFroststalker froststalker && froststalker.hasSpikes()) {
            cir.setReturnValue(new ResourceLocation("alexsmobs", "entities/froststalker_spikes"));
            cir.cancel();
            return;
        }
        if (self instanceof EntityCrimsonMosquito mosquito && mosquito.getBloodLevel() > 0) {
            cir.setReturnValue(mosquito.isFromFly() ? EntityCrimsonMosquito.FROM_FLY_FULL_LOOT : EntityCrimsonMosquito.FULL_LOOT);
            cir.cancel();
        }
    }
}
