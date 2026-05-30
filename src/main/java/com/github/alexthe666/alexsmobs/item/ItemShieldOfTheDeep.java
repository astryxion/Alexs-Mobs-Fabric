package com.github.alexthe666.alexsmobs.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

public class ItemShieldOfTheDeep extends ShieldItem {

    public ItemShieldOfTheDeep(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repairCandidate.is(AMItemRegistry.SERRATED_SHARK_TOOTH) || super.isValidRepairItem(stack, repairCandidate);
    }
}
