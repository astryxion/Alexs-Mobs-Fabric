package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.effect.AMBrewing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin {

    @Inject(method = "canPlaceItem(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"), cancellable = true)
    private void alexsmobs$canPlaceInput(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && slot < 3 && AMBrewing.isInput(stack) && ((BrewingStandBlockEntity) (Object) this).getItem(slot).isEmpty()) {
            cir.setReturnValue(true);
        }
    }
}
