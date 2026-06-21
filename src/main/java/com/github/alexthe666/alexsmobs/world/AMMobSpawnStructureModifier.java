package com.github.alexthe666.alexsmobs.world;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;

public class AMMobSpawnStructureModifier {
    public static final MapCodec<AMMobSpawnStructureModifier> CODEC = MapCodec.unit(AMMobSpawnStructureModifier::new);

    public AMMobSpawnStructureModifier() {
    }

    public void apply(BiomeSelectionContext selectionContext, BiomeModificationContext.MobSpawnSettingsContext builder) {
        // Structure-related spawn additions are applied in AMWorldRegistry.init() via BiomeSelectionContext#validForStructure.
    }

    public MapCodec<AMMobSpawnStructureModifier> codec() {
        return CODEC;
    }

    public static MapCodec<AMMobSpawnStructureModifier> makeCodec() {
        return CODEC;
    }
}
