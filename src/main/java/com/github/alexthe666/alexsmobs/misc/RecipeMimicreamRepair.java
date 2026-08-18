package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public class RecipeMimicreamRepair extends CustomRecipe {
    public static final MapCodec<RecipeMimicreamRepair> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(RecipeMimicreamRepair::category)
    ).apply(inst, RecipeMimicreamRepair::new));
    public static final net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, RecipeMimicreamRepair> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(MAP_CODEC.codec());

    private final CraftingBookCategory category;

    public RecipeMimicreamRepair(CraftingBookCategory category) {
        super();
        this.category = category;
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingInput inv, Level worldIn) {
        if (!AMConfig.mimicreamRepair) {
            return false;
        }
        ItemStack damageableStack = ItemStack.EMPTY;
        int mimicreamCount = 0;

        for (int j = 0; j < inv.size(); ++j) {
            ItemStack itemstack1 = inv.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.isDamageableItem() && !isBlacklisted(itemstack1)) {
                    damageableStack = itemstack1;
                } else {
                    if (itemstack1.getItem() == AMItemRegistry.MIMICREAM) {
                        mimicreamCount++;
                    }
                }
            }
        }

        return !damageableStack.isEmpty() && mimicreamCount >= 8;
    }

    public boolean isBlacklisted(ItemStack stack) {
        Identifier name = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return name != null && AMConfig.mimicreamBlacklist.contains(name.toString());
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack assemble(CraftingInput inv) {
        ItemStack damageableStack = ItemStack.EMPTY;
        int mimicreamCount = 0;

        for (int j = 0; j < inv.size(); ++j) {
            ItemStack itemstack1 = inv.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.isDamageableItem() && !isBlacklisted(itemstack1)) {
                    damageableStack = itemstack1;
                } else {
                    if (itemstack1.getItem() == AMItemRegistry.MIMICREAM) {
                        mimicreamCount++;
                    }
                }
            }
        }

        if (!damageableStack.isEmpty() && mimicreamCount >= 8) {
            ItemStack itemstack2 = damageableStack.copy();

            if (damageableStack.is(AMItemRegistry.GHOSTLY_PICKAXE)) {
                itemstack2.remove(DataComponents.CONTAINER);
            }

            ItemEnchantments existing = itemstack2.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
            ItemEnchantments.Mutable withoutMending = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            for (var e : existing.entrySet()) {
                boolean isMending = e.getKey().unwrapKey().map(k -> k.equals(Enchantments.MENDING)).orElse(false);
                if (!isMending) {
                    withoutMending.set(e.getKey(), e.getIntValue());
                }
            }
            itemstack2.set(DataComponents.ENCHANTMENTS, withoutMending.toImmutable());

            itemstack2.setDamageValue(itemstack2.getMaxDamage());
            return itemstack2;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public RecipeSerializer<RecipeMimicreamRepair> getSerializer() {
        return AMRecipeRegistry.MIMICREAM_RECIPE;
    }

    /**
     * Used to determine if a recipe can fit in a grid of the given width/height
     */
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }
}
