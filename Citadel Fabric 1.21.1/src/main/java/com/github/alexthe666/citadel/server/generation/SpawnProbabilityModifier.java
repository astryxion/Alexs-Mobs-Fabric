package com.github.alexthe666.citadel.server.generation;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.config.ServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;

public class SpawnProbabilityModifier {

    public static void register() {
        BiomeModifications.create(ResourceLocation.fromNamespaceAndPath(Citadel.MOD_ID, "mob_spawn_probability"))
                .add(ModificationPhase.POST_PROCESSING, ctx -> true, SpawnProbabilityModifier::modify);
    }

    private static void modify(BiomeSelectionContext selectionContext, BiomeModificationContext modificationContext) {
        Biome biome = selectionContext.getBiome();
        float currentProbability = biome.getMobSettings().getCreatureProbability();
        float probability = (float) ServerConfig.chunkGenSpawnModifierVal * currentProbability;
        modificationContext.getSpawnSettings().setCreatureSpawnProbability(Mth.clamp(probability, 0F, 1F));
    }
}
