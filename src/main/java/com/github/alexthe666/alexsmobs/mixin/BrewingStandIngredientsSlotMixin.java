package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.effect.ProperBrewingRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$IngredientsSlot")
public class BrewingStandIngredientsSlotMixin {

    @Inject(method = "mayPlace", at = @At("RETURN"), cancellable = true)
    private void alexsmobs$mayPlaceCustomIngredient(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            return;
        }
        if (stack.is(Items.GLOWSTONE_DUST) || stack.is(Items.REDSTONE) || stack.is(Items.GUNPOWDER)
                || stack.is(Items.DRAGON_BREATH) || ProperBrewingRecipe.isCustomIngredient(stack)) {
            cir.setReturnValue(true);
        }
    }
}
