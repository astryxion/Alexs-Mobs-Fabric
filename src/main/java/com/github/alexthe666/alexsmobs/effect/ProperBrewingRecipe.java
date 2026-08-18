package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Matches potion inputs by item and components, not only by item id.
 */
public class ProperBrewingRecipe {

    private final ItemStack inputStack;
    private final Ingredient input;
    private final Ingredient ingredient;
    private final ItemStack output;

    public ProperBrewingRecipe(ItemStack input, Ingredient ingredient, ItemStack output) {
        this.inputStack = input.copy();
        this.input = Ingredient.of(input.getItem());
        this.ingredient = ingredient;
        this.output = output.copy();
    }

    public ProperBrewingRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
        this.inputStack = ItemStack.EMPTY;
        this.input = input;
        this.ingredient = ingredient;
        this.output = output.copy();
    }

    public boolean isInput(ItemStack stack) {
        if (!this.inputStack.isEmpty()) {
            if (!ItemStack.isSameItem(stack, this.inputStack)) {
                return false;
            }
            PotionContents expected = this.inputStack.get(DataComponents.POTION_CONTENTS);
            PotionContents actual = stack.get(DataComponents.POTION_CONTENTS);
            if (expected != null && actual != null && expected.potion().isPresent() && actual.potion().isPresent()) {
                var expectedPotion = expected.potion().get();
                var actualPotion = actual.potion().get();
                if (expectedPotion == actualPotion || expectedPotion.value() == actualPotion.value()) {
                    return true;
                }
                return expectedPotion.unwrapKey().isPresent() && expectedPotion.unwrapKey().equals(actualPotion.unwrapKey());
            }
            return ItemStack.isSameItemSameComponents(this.inputStack, stack);
        }
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
