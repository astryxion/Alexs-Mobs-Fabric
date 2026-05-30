package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityFroststalker;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

/** Fabric: replaces NeoForge LivingChangeTargetEvent for armor-based target blocking. */
@Mixin(Mob.class)
public class MobTargetMixin {

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void alexsmobs$onSetTarget(@Nullable LivingEntity target, CallbackInfo ci) {
        if (target == null) {
            return;
        }
        Mob self = (Mob) (Object) this;
        if (self instanceof EntityFroststalker froststalker && froststalker.isValidLeader(target) && froststalker.getLastHurtByMob() != target) {
            ci.cancel();
            return;
        }
        if (self.getMobType() == MobType.ARTHROPOD && target.hasEffect(AMEffectRegistry.BUG_PHEROMONES) && self.getLastHurtByMob() != target) {
            ci.cancel();
            return;
        }
        if (self.getMobType() == MobType.UNDEAD && !self.getType().is(AMTagRegistry.IGNORES_KIMONO)
                && target.getItemBySlot(EquipmentSlot.CHEST).is(AMItemRegistry.UNSETTLING_KIMONO) && self.getLastHurtByMob() != target) {
            ci.cancel();
        }
    }
}
