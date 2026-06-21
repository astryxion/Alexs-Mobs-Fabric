package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.entity.EntityFroststalker;
import com.github.alexthe666.alexsmobs.event.ServerEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

/** Block invalid mob targets (kimono, bug pheromones, froststalker helmet). */
@Mixin(Mob.class)
public class MobTargetMixin {

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void alexsmobs$blockInvalidTarget(@Nullable LivingEntity target, CallbackInfo ci) {
        Mob self = (Mob) (Object) this;
        if (target != null && ServerEvents.shouldCancelTargeting(self, target)) {
            ci.cancel();
            return;
        }
        if (self instanceof EntityFroststalker froststalker && target != null && froststalker.isValidLeader(target) && froststalker.getLastHurtByMob() != target) {
            ci.cancel();
        }
    }
}
