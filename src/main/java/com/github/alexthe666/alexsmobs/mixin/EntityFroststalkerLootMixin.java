package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityFroststalker;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Conditional loot tables. Mob.getLootTable() is final in 1.21.1 so getDefaultLootTable() overrides are ignored.
 */
@Mixin(Mob.class)
public abstract class EntityFroststalkerLootMixin {

    @Inject(method = "getLootTable", at = @At("HEAD"), cancellable = true)
    private void alexsmobs$conditionalLootTables(CallbackInfoReturnable<ResourceKey<LootTable>> cir) {
        Mob self = (Mob) (Object) this;
        if (self instanceof EntityFroststalker froststalker && froststalker.hasSpikes()) {
            cir.setReturnValue(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("alexsmobs", "entities/froststalker_spikes")));
            cir.cancel();
            return;
        }
        if (self instanceof EntityCrimsonMosquito mosquito && mosquito.getBloodLevel() > 0) {
            cir.setReturnValue(mosquito.isFromFly() ? EntityCrimsonMosquito.FROM_FLY_FULL_LOOT : EntityCrimsonMosquito.FULL_LOOT);
            cir.cancel();
        }
    }
}
