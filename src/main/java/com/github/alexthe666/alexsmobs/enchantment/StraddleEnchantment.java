package com.github.alexthe666.alexsmobs.enchantment;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.ItemStraddleboard;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class StraddleEnchantment extends Enchantment {

    protected StraddleEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot... slots) {
        super(rarity, category, slots);
    }

    @Override
    public int getMinCost(int level) {
        return 6 + (level + 1) * 6;
    }

    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 10;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isDiscoverable() {
        return AMConfig.straddleboardEnchants;
    }

    @Override
    public boolean isTreasureOnly() {
        return false;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof ItemStraddleboard;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }
}
