package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.effect.ProperBrewingRecipe;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$PotionSlot")
public class BrewingStandMenuPotionSlotMixin {

    @Inject(method = "mayPlaceItem", at = @At("RETURN"), cancellable = true)
    private static void alexsmobs$mayPlaceItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && ProperBrewingRecipe.isValidInput(stack)) {
            cir.setReturnValue(true);
        }
    }
}
