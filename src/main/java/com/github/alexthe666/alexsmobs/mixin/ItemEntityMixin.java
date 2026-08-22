package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.item.ItemMysteriousWorm;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void alexsmobs$mysteriousWormVoid(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        ItemStack stack = self.getItem();
        if (stack.getItem() instanceof ItemMysteriousWorm worm && worm.onEntityItemUpdate(stack, self)) {
            ci.cancel();
        }
    }
}
