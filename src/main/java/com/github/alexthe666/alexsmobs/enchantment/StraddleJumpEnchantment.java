package com.github.alexthe666.alexsmobs.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class StraddleJumpEnchantment extends StraddleEnchantment {

    protected StraddleJumpEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot... slots) {
        super(rarity, category, slots);
    }

    @Override
    public int getMinCost(int level) {
        return 4 + (level - 1) * 5;
    }

    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 10;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
