package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom brewing mixes that match potion inputs by item and components.
 */
public final class AMBrewing {
    private static final List<ProperBrewingRecipe> RECIPES = new ArrayList<>();

    private AMBrewing() {
    }

    public static void reset() {
        RECIPES.clear();
    }

    public static void register(ProperBrewingRecipe recipe) {
        RECIPES.add(recipe);
    }

    public static boolean isIngredient(ItemStack stack) {
        for (ProperBrewingRecipe recipe : RECIPES) {
            if (recipe.isIngredient(stack)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInput(ItemStack stack) {
        for (ProperBrewingRecipe recipe : RECIPES) {
            if (recipe.isInput(stack)) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack mix(ItemStack input, ItemStack ingredient) {
        for (ProperBrewingRecipe recipe : RECIPES) {
            ItemStack output = recipe.getOutput(input, ingredient);
            if (!output.isEmpty()) {
                return output;
            }
        }
        return ItemStack.EMPTY;
    }
}
