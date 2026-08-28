package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentCanEnchantMixin {

    @Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true)
    private void alexsmobs$canEnchant(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Enchantment self = (Enchantment) (Object) this;
        if (stack.is(Items.ENCHANTED_BOOK)) {
            cir.setReturnValue(true);
            return;
        }
        if (isStraddleEnchant(self)) {
            cir.setReturnValue(stack.is(AMItemRegistry.STRADDLEBOARD));
        }
    }

    private static boolean isStraddleEnchant(Enchantment enchantment) {
        String key = enchantment.getDescriptionId();
        return key != null && (key.contains("straddle_jump") || key.contains("lavawax")
                || key.contains("serpentfriend") || key.contains("board_return"));
    }
}
