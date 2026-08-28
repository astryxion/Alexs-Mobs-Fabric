package com.github.alexthe666.alexsmobs.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class ItemPigshoes extends Item {

    public ItemPigshoes(Item.Properties props) {
        super(props);
    }

    public int getEnchantmentValue() {
        return 1;
    }

    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.category.canEnchant(stack.getItem())
                && enchantment != Enchantments.VANISHING_CURSE
                && enchantment != Enchantments.BINDING_CURSE
                && enchantment != Enchantments.UNBREAKING
                && enchantment != Enchantments.MENDING;
    }
}
