package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Custom brewing recipe wrapper; input matching is delegated to {@link Ingredient#test(ItemStack)} (26.1+),
 * which replaces the legacy {@code Ingredient#getItems()} iteration used on older versions.
 */
public class ProperBrewingRecipe {

    private final Ingredient input;
    private final Ingredient ingredient;
    private final ItemStack output;

    public ProperBrewingRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
        this.input = input;
        this.ingredient = ingredient;
        this.output = output.copy();
    }

    public boolean isInput(ItemStack stack) {
        return input.test(stack);
    }

    public boolean isIngredient(ItemStack stack) {
        return ingredient.test(stack);
    }

    public ItemStack getOutput(ItemStack inputStack, ItemStack ingredientStack) {
        if (isInput(inputStack) && isIngredient(ingredientStack)) {
            return output.copy();
        }
        return ItemStack.EMPTY;
    }
}
