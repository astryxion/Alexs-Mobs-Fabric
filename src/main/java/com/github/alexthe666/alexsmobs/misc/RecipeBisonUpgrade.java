package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.List;

public class RecipeBisonUpgrade extends CustomRecipe {

    public RecipeBisonUpgrade(CraftingBookCategory category) {
        super(category);
    }

    private ItemStack createBoots(CraftingInput craftInput) {
        ItemStack boots = ItemStack.EMPTY;
        int fur = 0;
        List<ItemStack> items = craftInput.items();
        for (int j = 0; j < items.size(); ++j) {
            ItemStack itemstack1 = items.get(j);
            if (itemstack1.is(AMBlockRegistry.BISON_FUR_BLOCK.asItem())) {
                fur++;
            }
        }
        if (fur == 1) {
            for (int j = 0; j < items.size(); ++j) {
                ItemStack itemstack1 = items.get(j);
                CompoundTag custom = itemstack1.get(DataComponents.CUSTOM_DATA) != null ? itemstack1.get(DataComponents.CUSTOM_DATA).copyTag() : null;
                boolean notFurred = custom == null || !custom.getBoolean("BisonFur");
                boolean isFeet = itemstack1.getItem() instanceof ArmorItem armorItem && armorItem.getEquipmentSlot() == EquipmentSlot.FEET;
                if (!itemstack1.isEmpty() && notFurred && isFeet) {
                    boots = itemstack1;
                }
            }
            if (!boots.isEmpty()) {
                ItemStack stack = boots.copy();
                CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA) != null ? stack.get(DataComponents.CUSTOM_DATA).copyTag() : new CompoundTag();
                tag.putBoolean("BisonFur", true);
                stack.set(DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(CraftingInput craftInput, Level worldIn) {
        return !createBoots(craftInput).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput craftInput, HolderLookup.Provider provider) {
        return createBoots(craftInput);
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AMRecipeRegistry.BISON_UPGRADE;
    }
}
