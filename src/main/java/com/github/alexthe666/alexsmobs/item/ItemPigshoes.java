package com.github.alexthe666.alexsmobs.item;

import net.minecraft.tags.EnchantmentTags;
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
        var reg = net.minecraft.core.RegistryAccess.EMPTY.registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);
        boolean isCurse = reg.getResourceKey(enchantment).flatMap(key -> reg.getTag(EnchantmentTags.CURSE).map(holders -> holders.contains(reg.getHolderOrThrow(key)))).orElse(false);
        return enchantment.isSupportedItem(stack)
                && !isCurse
                && !enchantment.equals(reg.getHolderOrThrow(Enchantments.UNBREAKING).value())
                && !enchantment.equals(reg.getHolderOrThrow(Enchantments.MENDING).value());
    }
}
