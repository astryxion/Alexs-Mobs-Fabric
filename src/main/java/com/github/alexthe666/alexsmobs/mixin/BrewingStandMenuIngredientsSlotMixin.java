package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.effect.ProperBrewingRecipe;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$IngredientsSlot")
public class BrewingStandMenuIngredientsSlotMixin {

    @Inject(method = "mayPlace", at = @At("RETURN"), cancellable = true)
    private void alexsmobs$mayPlace(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && ProperBrewingRecipe.isValidIngredient(stack)) {
            cir.setReturnValue(true);
        }
    }
}
