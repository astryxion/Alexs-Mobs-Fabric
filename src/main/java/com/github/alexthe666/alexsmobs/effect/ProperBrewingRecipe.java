package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom brewing recipes that cannot be expressed as potion+ingredient->potion
 * (e.g. item input or item output). Registered via FabricBrewingRecipeRegistry where possible;
 * the rest are stored here and applied by mixin for 1:1 behavior.
 */
public class ProperBrewingRecipe {

    private final Ingredient input;
    private final Ingredient ingredient;
    private final ItemStack output;

    public ProperBrewingRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
        this.input = input;
        this.ingredient = ingredient;
        this.output = output;
    }

    public boolean isInput(@Nonnull ItemStack stack) {
        if (stack == null) {
            return false;
        }
        ItemStack[] matchingStacks = input.getItems();
        if (matchingStacks.length == 0) {
            return stack.isEmpty();
        }
        for (ItemStack itemstack : matchingStacks) {
            if (ItemStack.isSameItem(stack, itemstack) && ItemStack.isSameItemSameTags(itemstack, stack)) {
                return true;
            }
        }
        return false;
    }

    public boolean isIngredient(ItemStack stack) {
        return ingredient.test(stack);
    }

    public ItemStack getOutput(ItemStack inputStack, ItemStack ingredientStack) {
        return output.copy();
    }

    /** Recipes that need custom handling (item input or item output). Applied by mixin. */
    public static final List<ProperBrewingRecipe> CUSTOM_RECIPES = new ArrayList<>();

    public static void registerCustomRecipes() {
        // LAVA_BOTTLE (item) + BONE_SERPENT_TOOTH -> LAVA_VISION_POTION
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(
                Ingredient.of(AMItemRegistry.LAVA_BOTTLE),
                Ingredient.of(AMItemRegistry.BONE_SERPENT_TOOTH),
                AMEffectRegistry.createPotion(AMEffectRegistry.LAVA_VISION_POTION)));
        // POISON (potion in bottle) + RATTLESNAKE_RATTLE -> POISON_BOTTLE (item)
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)),
                Ingredient.of(AMItemRegistry.RATTLESNAKE_RATTLE),
                new ItemStack(AMItemRegistry.POISON_BOTTLE)));
        // POISON_BOTTLE (item) + CENTIPEDE_LEG -> POISON_RESISTANCE_POTION
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(
                Ingredient.of(AMItemRegistry.POISON_BOTTLE),
                Ingredient.of(AMItemRegistry.CENTIPEDE_LEG),
                AMEffectRegistry.createPotion(AMEffectRegistry.POISON_RESISTANCE_POTION)));
        // KOMODO_SPIT_BOTTLE (item) + CENTIPEDE_LEG -> POISON_RESISTANCE_POTION
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(
                Ingredient.of(AMItemRegistry.KOMODO_SPIT_BOTTLE),
                Ingredient.of(AMItemRegistry.CENTIPEDE_LEG),
                AMEffectRegistry.createPotion(AMEffectRegistry.POISON_RESISTANCE_POTION)));
    }
}


