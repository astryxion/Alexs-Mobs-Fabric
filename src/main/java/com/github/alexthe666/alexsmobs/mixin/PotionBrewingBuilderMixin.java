package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {"net.minecraft.world.item.alchemy.PotionBrewing$Builder"})
public class PotionBrewingBuilderMixin {

    @Inject(method = "build()Lnet/minecraft/world/item/alchemy/PotionBrewing;", at = @At("HEAD"))
    private void alexsmobs$collectRecipes(CallbackInfoReturnable<PotionBrewing> cir) {
        AMEffectRegistry.registerBrewingRecipes();
    }
}
