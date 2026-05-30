package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.enchantment.StraddleEnchantment;
import com.github.alexthe666.alexsmobs.item.ItemStraddleboard;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Enchanting tables use {@link net.minecraft.world.item.enchantment.EnchantmentCategory#canEnchant(Item)},
 * which ignores {@link net.minecraft.world.item.enchantment.Enchantment#canEnchant(ItemStack)} overrides.
 * Straddleboard enchantments use BREAKABLE on Fabric; filter them off non-board items after vanilla rolls options.
 *
 * <p>1.20.1 method is {@code getAvailableEnchantmentResults} — do not use {@code getPossibleEntries} (1.21+).</p>
 */
@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getAvailableEnchantmentResults", at = @At("RETURN"))
    private static void alexsmobs$stripStraddleFromNonBoardTable(
            int level,
            ItemStack stack,
            boolean allowTreasure,
            CallbackInfoReturnable<List<EnchantmentInstance>> cir
    ) {
        if (stack.getItem() instanceof ItemStraddleboard) {
            return;
        }
        cir.getReturnValue().removeIf(instance -> instance.enchantment instanceof StraddleEnchantment);
    }
}
