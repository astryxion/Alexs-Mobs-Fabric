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
 * Custom brewing recipes. Applied by BrewingStand/PotionBrewing mixins.
 * Keeps a concrete input ItemStack (with PotionUtils NBT) so potion-type matching works on 1.20.1.
 */
public class ProperBrewingRecipe {

    private final ItemStack input;
    private final Ingredient ingredient;
    private final ItemStack output;

    public ProperBrewingRecipe(ItemStack input, Ingredient ingredient, ItemStack output) {
        this.input = input.copy();
        this.ingredient = ingredient;
        this.output = output;
    }

    public ProperBrewingRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
        ItemStack[] stacks = input.getItems();
        this.input = stacks.length > 0 ? stacks[0].copy() : ItemStack.EMPTY;
        this.ingredient = ingredient;
        this.output = output;
    }

    public boolean isInput(@Nonnull ItemStack stack) {
        if (stack == null || stack.isEmpty() || this.input.isEmpty()) {
            return false;
        }
        return isSameBrewingInput(stack, this.input);
    }

    private static boolean isSameBrewingInput(ItemStack stack, ItemStack recipeInput) {
        if (!ItemStack.isSameItem(stack, recipeInput)) {
            return false;
        }
        if (stack.is(Items.POTION) || stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION)) {
            return PotionUtils.getPotion(stack) == PotionUtils.getPotion(recipeInput);
        }
        return java.util.Objects.equals(stack.getTag(), recipeInput.getTag());
    }

    public boolean isIngredient(ItemStack stack) {
        return ingredient.test(stack);
    }

    public ItemStack getOutput(ItemStack inputStack, ItemStack ingredientStack) {
        return isInput(inputStack) && isIngredient(ingredientStack) ? output.copy() : ItemStack.EMPTY;
    }

    public static final List<ProperBrewingRecipe> CUSTOM_RECIPES = new ArrayList<>();

    public static boolean isCustomIngredient(ItemStack stack) {
        for (ProperBrewingRecipe recipe : CUSTOM_RECIPES) {
            if (recipe.isIngredient(stack)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCustomInput(ItemStack stack) {
        for (ProperBrewingRecipe recipe : CUSTOM_RECIPES) {
            if (recipe.isInput(stack)) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack mixCustom(ItemStack input, ItemStack ingredient) {
        for (ProperBrewingRecipe recipe : CUSTOM_RECIPES) {
            ItemStack out = recipe.getOutput(input, ingredient);
            if (!out.isEmpty()) {
                return out;
            }
        }
        return ItemStack.EMPTY;
    }

    public static void registerCustomRecipes() {
        CUSTOM_RECIPES.clear();
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(AMEffectRegistry.createPotion(Potions.STRENGTH), Ingredient.of(AMItemRegistry.BEAR_FUR), AMEffectRegistry.createPotion(AMEffectRegistry.KNOCKBACK_RESISTANCE_POTION)));
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(AMEffectRegistry.createPotion(AMEffectRegistry.KNOCKBACK_RESISTANCE_POTION), Ingredient.of(Items.REDSTONE), AMEffectRegistry.createPotion(AMEffectRegistry.LONG_KNOCKBACK_RESISTANCE_POTION)));
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(AMEffectRegistry.createPotion(AMEffectRegistry.KNOCKBACK_RESISTANCE_POTION), Ingredient.of(Items.GLOWSTONE_DUST), AMEffectRegistry.createPotion(AMEffectRegistry.STRONG_KNOCKBACK_RESISTANCE_POTION)));
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(new ItemStack(AMItemRegistry.LAVA_BOTTLE), Ingredient.of(AMItemRegistry.BONE_SERPENT_TOOTH), AMEffectRegistry.createPotion(AMEffectRegistry.LAVA_VISION_POTION)));
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(AMEffectRegistry.createPotion(AMEffectRegistry.LAVA_VISION_POTION), Ingredient.of(Items.REDSTONE), AMEffectRegistry.createPotion(AMEffectRegistry.LONG_LAVA_VISION_POTION)));
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(AMEffectRegistry.createPotion(Potions.AWKWARD), Ingredient.of(AMItemRegistry.RATTLESNAKE_RATTLE), new ItemStack(AMItemRegistry.POISON_BOTTLE)));
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(new ItemStack(AMItemRegistry.POISON_BOTTLE), Ingredient.of(AMItemRegistry.CENTIPEDE_LEG), AMEffectRegistry.createPotion(AMEffectRegistry.POISON_RESISTANCE_POTION)));
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(new ItemStack(AMItemRegistry.KOMODO_SPIT_BOTTLE), Ingredient.of(AMItemRegistry.CENTIPEDE_LEG), AMEffectRegistry.createPotion(AMEffectRegistry.POISON_RESISTANCE_POTION)));
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(AMEffectRegistry.createPotion(AMEffectRegistry.POISON_RESISTANCE_POTION), Ingredient.of(AMItemRegistry.KOMODO_SPIT), AMEffectRegistry.createPotion(AMEffectRegistry.LONG_POISON_RESISTANCE_POTION)));
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(AMEffectRegistry.createPotion(Potions.STRONG_SWIFTNESS), Ingredient.of(AMItemRegistry.GAZELLE_HORN), AMEffectRegistry.createPotion(AMEffectRegistry.SPEED_III_POTION)));
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(AMEffectRegistry.createPotion(Potions.AWKWARD), Ingredient.of(AMItemRegistry.COCKROACH_WING), AMEffectRegistry.createPotion(AMEffectRegistry.BUG_PHEROMONES_POTION)));
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(AMEffectRegistry.createPotion(AMEffectRegistry.BUG_PHEROMONES_POTION), Ingredient.of(Items.REDSTONE), AMEffectRegistry.createPotion(AMEffectRegistry.LONG_BUG_PHEROMONES_POTION)));
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(AMEffectRegistry.createPotion(Potions.AWKWARD), Ingredient.of(AMItemRegistry.SOUL_HEART), AMEffectRegistry.createPotion(AMEffectRegistry.SOULSTEAL_POTION)));
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(AMEffectRegistry.createPotion(AMEffectRegistry.SOULSTEAL_POTION), Ingredient.of(Items.REDSTONE), AMEffectRegistry.createPotion(AMEffectRegistry.LONG_SOULSTEAL_POTION)));
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(AMEffectRegistry.createPotion(AMEffectRegistry.SOULSTEAL_POTION), Ingredient.of(Items.GLOWSTONE_DUST), AMEffectRegistry.createPotion(AMEffectRegistry.STRONG_SOULSTEAL_POTION)));
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(AMEffectRegistry.createPotion(Potions.AWKWARD), Ingredient.of(AMItemRegistry.DROPBEAR_CLAW), AMEffectRegistry.createPotion(AMEffectRegistry.CLINGING_POTION)));
        CUSTOM_RECIPES.add(new ProperBrewingRecipe(AMEffectRegistry.createPotion(AMEffectRegistry.CLINGING_POTION), Ingredient.of(Items.REDSTONE), AMEffectRegistry.createPotion(AMEffectRegistry.LONG_CLINGING_POTION)));
    }
}
