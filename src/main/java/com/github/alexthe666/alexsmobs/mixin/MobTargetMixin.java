package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.entity.EntityFroststalker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

/** Block froststalkers from acquiring helmeted players as targets (matches NeoForge goal predicate). */
@Mixin(Mob.class)
public class MobTargetMixin {

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void alexsmobs$blockFroststalkerHelmetTarget(@Nullable LivingEntity target, CallbackInfo ci) {
        Mob self = (Mob) (Object) this;
        if (self instanceof EntityFroststalker froststalker && target != null && froststalker.isValidLeader(target) && froststalker.getLastHurtByMob() != target) {
            ci.cancel();
        }
    }
}
