package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.Registries;

import java.util.List;

public class RecipeMimicreamRepair extends CustomRecipe {
    public RecipeMimicreamRepair(CraftingBookCategory category) {
        super(category);
    }

    private static List<ItemStack> items(CraftingInput inv) {
        return inv.items();
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingInput craftInput, Level worldIn) {
        if (!AMConfig.mimicreamRepair) {
            return false;
        }
        ItemStack damageableStack = ItemStack.EMPTY;
        int mimicreamCount = 0;
        List<ItemStack> items = items(craftInput);
        for (int j = 0; j < items.size(); ++j) {
            ItemStack itemstack1 = items.get(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.isDamageableItem() && !isBlacklisted(itemstack1, worldIn.registryAccess())) {
                    damageableStack = itemstack1;
                } else {
                    if (itemstack1.is(AMItemRegistry.MIMICREAM)) {
                        mimicreamCount++;
                    }
                }
            }
        }
        return !damageableStack.isEmpty() && mimicreamCount >= 8;
    }

    public boolean isBlacklisted(ItemStack stack, HolderLookup.Provider provider) {
        ResourceLocation name = BuiltInRegistries.ITEM.getResourceKey(stack.getItem()).map(ResourceKey::location).orElse(null);
        return name != null && AMConfig.mimicreamBlacklist.contains(name.toString());
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack assemble(CraftingInput craftInput, HolderLookup.Provider provider) {
        ItemStack damageableStack = ItemStack.EMPTY;
        int mimicreamCount = 0;
        List<ItemStack> items = items(craftInput);
        for (int j = 0; j < items.size(); ++j) {
            ItemStack itemstack1 = items.get(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.isDamageableItem() && !isBlacklisted(itemstack1, provider)) {
                    damageableStack = itemstack1;
                } else {
                    if (itemstack1.is(AMItemRegistry.MIMICREAM)) {
                        mimicreamCount++;
                    }
                }
            }
        }

        if (!damageableStack.isEmpty() && mimicreamCount >= 8) {
            ItemStack itemstack2 = damageableStack.copy();
            CompoundTag compoundnbt = damageableStack.get(DataComponents.CUSTOM_DATA) != null
                    ? damageableStack.get(DataComponents.CUSTOM_DATA).copyTag()
                    : new CompoundTag();

            if (damageableStack.is(AMItemRegistry.GHOSTLY_PICKAXE) && compoundnbt.contains("Items")) {
                compoundnbt.remove("Items");
            }
            ResourceLocation mendingName = Enchantments.MENDING.location();
            if (mendingName != null && compoundnbt.contains("Enchantments", 9)) {
                net.minecraft.nbt.ListTag oldNBTList = compoundnbt.getList("Enchantments", 10);
                net.minecraft.nbt.ListTag newNBTList = new net.minecraft.nbt.ListTag();
                for (int i = 0; i < oldNBTList.size(); ++i) {
                    CompoundTag compoundnbt2 = oldNBTList.getCompound(i);
                    ResourceLocation resourcelocation1 = ResourceLocation.tryParse(compoundnbt2.getString("id"));
                    if (resourcelocation1 == null || !resourcelocation1.equals(mendingName)) {
                        newNBTList.add(compoundnbt2);
                    }
                }
                compoundnbt.put("Enchantments", newNBTList);
            }
            itemstack2.set(DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(compoundnbt));
            itemstack2.setDamageValue(0);
            return itemstack2;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput craftInput) {
        List<ItemStack> items = items(craftInput);
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(items.size(), ItemStack.EMPTY);
        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = items.get(i);
            net.minecraft.world.item.Item remItem = itemstack.getItem().getCraftingRemainingItem();
            if (remItem != null && remItem != net.minecraft.world.item.Items.AIR) {
                nonnulllist.set(i, new ItemStack(remItem, 1));
            } else if (itemstack.isDamageableItem()) {
                ItemStack itemstack1 = itemstack.copy();
                itemstack1.setCount(1);
                nonnulllist.set(i, itemstack1);
                break;
            }
        }
        return nonnulllist;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AMRecipeRegistry.MIMICREAM_RECIPE;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }
}
