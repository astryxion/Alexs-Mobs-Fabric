package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.effect.ProperBrewingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin {

    @Inject(method = "isBrewable", at = @At("RETURN"), cancellable = true)
    private static void alexsmobs$isBrewable(NonNullList<ItemStack> items, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && ProperBrewingRecipe.canBrew(items)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canPlaceItem", at = @At("HEAD"), cancellable = true)
    private void alexsmobs$canPlaceItem(int index, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (index >= 0 && index <= 2 && ProperBrewingRecipe.isValidInput(stack)) {
            cir.setReturnValue(true);
        } else if (index == 3 && ProperBrewingRecipe.isValidIngredient(stack)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "doBrew", at = @At("HEAD"), cancellable = true)
    private static void alexsmobs$customBrew(Level level, BlockPos pos, NonNullList<ItemStack> items, CallbackInfo ci) {
        ItemStack ingredient = items.get(3);
        if (ingredient.isEmpty()) {
            return;
        }
        boolean brewed = false;
        for (int i = 0; i < 3; i++) {
            ItemStack input = items.get(i);
            if (input.isEmpty()) {
                continue;
            }
            for (ProperBrewingRecipe recipe : ProperBrewingRecipe.CUSTOM_RECIPES) {
                if (recipe.isInput(input) && recipe.isIngredient(ingredient)) {
                    items.set(i, recipe.getOutput(input, ingredient));
                    brewed = true;
                }
            }
        }
        if (brewed) {
            ingredient.shrink(1);
            ci.cancel();
        }
    }
}
