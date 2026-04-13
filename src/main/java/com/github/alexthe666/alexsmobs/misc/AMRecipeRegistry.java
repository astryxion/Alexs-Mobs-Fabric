package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class AMRecipeRegistry {

    public static RecipeSerializer<RecipeMimicreamRepair> MIMICREAM_RECIPE;
    public static RecipeSerializer<RecipeBisonUpgrade> BISON_UPGRADE;

    public static void init() {
        MIMICREAM_RECIPE = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(AlexsMobs.MODID, "mimicream_repair"),
                new SimpleCraftingRecipeSerializer<>(RecipeMimicreamRepair::new));
        BISON_UPGRADE = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(AlexsMobs.MODID, "bison_upgrade"),
                new SimpleCraftingRecipeSerializer<>(RecipeBisonUpgrade::new));
    }
}
