package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.effect.ProperBrewingRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionBrewing.class)
public class PotionBrewingMixin {

    @Inject(method = "isIngredient", at = @At("RETURN"), cancellable = true)
    private void alexsmobs$isIngredient(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            return;
        }
        if (stack.is(Items.GLOWSTONE_DUST) || stack.is(Items.REDSTONE) || stack.is(Items.GUNPOWDER)
                || stack.is(Items.DRAGON_BREATH) || ProperBrewingRecipe.isCustomIngredient(stack)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isContainer", at = @At("RETURN"), cancellable = true)
    private void alexsmobs$isContainer(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && ProperBrewingRecipe.isCustomInput(stack)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "hasMix", at = @At("RETURN"), cancellable = true)
    private void alexsmobs$hasMix(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && !ProperBrewingRecipe.mixCustom(input, ingredient).isEmpty()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mix", at = @At("RETURN"), cancellable = true)
    private void alexsmobs$mix(ItemStack ingredient, ItemStack input, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack output = ProperBrewingRecipe.mixCustom(input, ingredient);
        if (!output.isEmpty()) {
            cir.setReturnValue(output);
        }
    }
}
