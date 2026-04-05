package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.misc.AMDamageTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric 1.20.1: Apply Farseer random death message (1:1 with former DamageSourceRandomMessages).
 */
@Mixin(DamageSource.class)
public class DamageSourceMixin {

    @Inject(method = "getLocalizedDeathMessage", at = @At("HEAD"), cancellable = true)
    private void alexsmobs$farseerDeathMessage(LivingEntity attacked, CallbackInfoReturnable<Component> cir) {
        DamageSource self = (DamageSource) (Object) this;
        if ("farseer".equals(self.getMsgId())) {
            cir.setReturnValue(AMDamageTypes.getFarseerDeathMessage(self, attacked));
        }
    }
}
