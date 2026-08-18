package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntitySharkToothArrow;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemModArrow extends ArrowItem {
    public ItemModArrow(Item.Properties group) {
        super(group);
    }

    @Override
    public AbstractArrow createArrow(Level worldIn, ItemStack stack, LivingEntity shooter, ItemStack weapon) {
        if (this == AMItemRegistry.SHARK_TOOTH_ARROW) {
            return new EntitySharkToothArrow(worldIn, shooter, stack, weapon);
        }
        return super.createArrow(worldIn, stack, shooter, weapon);
    }

}
