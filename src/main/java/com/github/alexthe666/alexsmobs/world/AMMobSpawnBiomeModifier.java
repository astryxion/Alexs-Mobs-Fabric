package com.github.alexthe666.alexsmobs.world;

import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public class AMMobSpawnBiomeModifier {
    public static final MapCodec<AMMobSpawnBiomeModifier> CODEC = MapCodec.unit(AMMobSpawnBiomeModifier::new);

    public AMMobSpawnBiomeModifier() {
    }

    public void apply(Holder<Biome> biome, BiomeModificationContext.MobSpawnSettingsContext builder) {
        AMWorldRegistry.addBiomeSpawns(biome, builder);
    }

    public MapCodec<AMMobSpawnBiomeModifier> codec() {
        return CODEC;
    }

    public static MapCodec<AMMobSpawnBiomeModifier> makeCodec() {
        return CODEC;
    }
}
