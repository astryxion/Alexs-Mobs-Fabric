package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.List;

public class RecipeBisonUpgrade extends CustomRecipe {

    public RecipeBisonUpgrade(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    private static List<ItemStack> items(CraftingContainer inv) {
        List<ItemStack> list = new java.util.ArrayList<>();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            list.add(inv.getItem(i));
        }
        return list;
    }

    private ItemStack createBoots(CraftingContainer craftInput) {
        ItemStack boots = ItemStack.EMPTY;
        int fur = 0;
        List<ItemStack> items = items(craftInput);
        for (int j = 0; j < items.size(); ++j) {
            ItemStack itemstack1 = items.get(j);
            if (itemstack1.is(AMBlockRegistry.BISON_FUR_BLOCK.asItem())) {
                fur++;
            }
        }
        if (fur == 1) {
            for (int j = 0; j < items.size(); ++j) {
                ItemStack itemstack1 = items.get(j);
                CompoundTag custom = itemstack1.hasTag() ? itemstack1.getTag() : null;
                boolean notFurred = custom == null || !custom.getBoolean("BisonFur");
                boolean isFeet = itemstack1.getItem() instanceof ArmorItem armorItem && armorItem.getEquipmentSlot() == EquipmentSlot.FEET;
                if (!itemstack1.isEmpty() && notFurred && isFeet) {
                    boots = itemstack1;
                }
            }
            if (!boots.isEmpty()) {
                ItemStack stack = boots.copy();
                CompoundTag tag = stack.hasTag() ? stack.getTag().copy() : new CompoundTag();
                tag.putBoolean("BisonFur", true);
                stack.setTag(tag);
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(CraftingContainer craftInput, Level worldIn) {
        return !createBoots(craftInput).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer craftInput, RegistryAccess provider) {
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
