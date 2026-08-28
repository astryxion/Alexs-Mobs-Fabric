package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.effect.ProperBrewingRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin {

    @Inject(method = "canPlaceItem", at = @At("RETURN"), cancellable = true)
    private void alexsmobs$canPlaceCustomInput(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            return;
        }
        if (slot < 3 && ProperBrewingRecipe.isCustomInput(stack)
                && ((BrewingStandBlockEntity) (Object) this).getItem(slot).isEmpty()) {
            cir.setReturnValue(true);
        } else if (slot == 3 && (stack.is(net.minecraft.world.item.Items.GLOWSTONE_DUST)
                || stack.is(net.minecraft.world.item.Items.REDSTONE)
                || ProperBrewingRecipe.isCustomIngredient(stack))) {
            cir.setReturnValue(true);
        }
    }
}
