package com.github.alexthe666.alexsmobs.mixin;

import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Map;

/**
 * Vanilla anvil logic can skip {@link Enchantment#canEnchant(ItemStack)} when
 * the target is an
 * enchanted book or the player is in creative. Strip invalid enchantments
 * before applying.
 */
@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

    @ModifyArgs(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;setEnchantments(Ljava/util/Map;Lnet/minecraft/world/item/ItemStack;)V"))
    private static void alexsmobs$filterAnvilEnchantments(Args args) {
        @SuppressWarnings("unchecked")
        Map<Enchantment, Integer> enchantments = (Map<Enchantment, Integer>) args.get(0);
        ItemStack stack = (ItemStack) args.get(1);
        if (!stack.is(net.minecraft.world.item.Items.ENCHANTED_BOOK))
            enchantments.entrySet().removeIf(entry -> !entry.getKey().canEnchant(stack));
    }
}
