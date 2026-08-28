package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.effect.ProperBrewingRecipe;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$PotionSlot")
public class BrewingStandMenuMixin {

    @Inject(method = "mayPlace", at = @At("RETURN"), cancellable = true)
    private void alexsmobs$mayPlaceCustomInput(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && ProperBrewingRecipe.isCustomInput(stack)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mayPlaceItem(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"), cancellable = true)
    private static void alexsmobs$mayPlaceItemCustomInput(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && ProperBrewingRecipe.isCustomInput(stack)) {
            cir.setReturnValue(true);
        }
    }
}
