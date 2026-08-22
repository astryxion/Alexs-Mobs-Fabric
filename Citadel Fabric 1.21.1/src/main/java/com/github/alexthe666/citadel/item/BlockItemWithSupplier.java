package com.github.alexthe666.citadel.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;

import java.util.ArrayList;
import java.util.List;

public class BlockItemWithSupplier extends BlockItem {

    private final Block blockSupplier;

    public BlockItemWithSupplier(Block blockSupplier, Properties props) {
        super(blockSupplier, props);
        this.blockSupplier = blockSupplier;
    }

    @Override
    public Block getBlock() {
        return blockSupplier;
    }

    public boolean canFitInsideContainerItems() {
        return !(blockSupplier instanceof ShulkerBoxBlock);
    }

    public void onDestroyed(ItemEntity p_150700_) {
        if (this.blockSupplier instanceof ShulkerBoxBlock) {
            ItemStack itemstack = p_150700_.getItem();
            CompoundTag compoundtag = getBlockEntityData(itemstack);
            if (compoundtag != null && compoundtag.contains("Items", 9)) {
                ListTag listtag = compoundtag.getList("Items", 10);
                List<ItemStack> stacks = new ArrayList<>();
                for (int i = 0; i < listtag.size(); i++) {
                    ItemStack stack = ItemStack.parseOptional(p_150700_.level().registryAccess(), listtag.getCompound(i));
                    if (stack != null && !stack.isEmpty()) stacks.add(stack);
                }
                ItemUtils.onContainerDestroyed(p_150700_, stacks);
            }
        }
    }

    private static CompoundTag getBlockEntityData(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null ? data.copyTag() : null;
    }
}
