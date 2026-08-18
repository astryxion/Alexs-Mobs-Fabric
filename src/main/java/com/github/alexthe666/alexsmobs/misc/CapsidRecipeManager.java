package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.Level;

import java.util.List;
import java.util.Map;

public class CapsidRecipeManager extends SimpleJsonResourceReloadListener<JsonElement> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(CapsidRecipe.class, new CapsidRecipe.Deserializer()).create();

    private final List<CapsidRecipe> capsidRecipes = Lists.newArrayList();

    public CapsidRecipeManager() {
        super(ExtraCodecs.JSON, FileToIdConverter.json("capsid_recipes"));
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profile) {
        this.capsidRecipes.clear();
        ImmutableMap.Builder<Identifier, CapsidRecipe> builder = ImmutableMap.builder();
        AlexsMobs.LOGGER.log(Level.ALL, "Loading in capsid_recipes jsons...");
        jsonMap.forEach((id, jsonElement) -> {
            try {
                CapsidRecipe capsidRecipe = GSON.fromJson(jsonElement, CapsidRecipe.class);
                builder.put(id, capsidRecipe);
            } catch (Exception exception) {
                AlexsMobs.LOGGER.error("Couldn't parse capsid recipe {}", id, exception);
            }
        });
        ImmutableMap<Identifier, CapsidRecipe> immutablemap = builder.build();
        immutablemap.forEach((id, capsidRecipe) -> {
            capsidRecipes.add(capsidRecipe);
        });
    }

    public CapsidRecipe getRecipeFor(ItemStack stack) {
        for (CapsidRecipe recipe : capsidRecipes) {
            if (recipe.matches(stack)) {
                return recipe;
            }
        }

        return null;
    }

    public List<CapsidRecipe> getCapsidRecipes() {
        return capsidRecipes;
    }

    @Override
    public String getName() {
        return "CapsidRecipeManager";
    }
}
