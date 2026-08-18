package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;

/**
 * OR-combination of ingredients (NeoForge {@code CompoundIngredient.of}).
 */
public final class IngredientOr {
    private IngredientOr() {
    }

    public static Ingredient of(Ingredient... parts) {
        return Ingredient.of(Arrays.stream(parts).flatMap(ing -> ing.items()).map(Holder::value));
    }
}
