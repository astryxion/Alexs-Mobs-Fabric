package com.github.alexthe666.citadel.server.generation;

import com.github.alexthe666.citadel.config.ServerConfig;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;

public class SpawnProbabilityModifier {

    public static float modifyProbability(Holder<Biome> biome, float currentProbability) {
        float probability = (float) (ServerConfig.chunkGenSpawnModifierVal) * currentProbability;
        return Mth.clamp(probability, 0F, 1F);
    }

    public static MapCodec<SpawnProbabilityModifier> makeCodec() {
        return MapCodec.unit(SpawnProbabilityModifier::new);
    }

    public float modify(Holder<Biome> biome, float currentProbability) {
        float probability = (float) (ServerConfig.chunkGenSpawnModifierVal) * currentProbability;
        return Mth.clamp(probability, 0F, 1F);
    }
}
