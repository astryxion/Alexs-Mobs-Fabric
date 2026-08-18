package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class AMRecipeRegistry {
    public static final RecipeSerializer<RecipeMimicreamRepair> MIMICREAM_RECIPE = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "mimicream_repair"), new RecipeSerializer<>(RecipeMimicreamRepair.MAP_CODEC, RecipeMimicreamRepair.STREAM_CODEC));
    public static final RecipeSerializer<RecipeBisonUpgrade> BISON_UPGRADE = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "bison_upgrade"), new RecipeSerializer<>(RecipeBisonUpgrade.MAP_CODEC, RecipeBisonUpgrade.STREAM_CODEC));

    public static void init(){
    }
}
