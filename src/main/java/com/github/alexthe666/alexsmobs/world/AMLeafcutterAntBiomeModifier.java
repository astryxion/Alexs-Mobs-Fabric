package com.github.alexthe666.alexsmobs.world;

import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class AMLeafcutterAntBiomeModifier {
    public static final MapCodec<AMLeafcutterAntBiomeModifier> CODEC = RecordCodecBuilder.mapCodec((config) -> {
        return config.group(PlacedFeature.LIST_CODEC.fieldOf("features").forGetter((otherConfig) -> {
            return otherConfig.features;
        })).apply(config, AMLeafcutterAntBiomeModifier::new);
    });
    private final HolderSet<PlacedFeature> features;

    public AMLeafcutterAntBiomeModifier(HolderSet<PlacedFeature> features) {
        this.features = features;
    }

    public void apply(Holder<Biome> biome, BiomeModificationContext.GenerationSettingsContext builder) {
        AMWorldRegistry.addLeafcutterAntSpawns(biome, builder);
    }

    public MapCodec<AMLeafcutterAntBiomeModifier> codec() {
        return CODEC;
    }

    public static MapCodec<AMLeafcutterAntBiomeModifier> makeCodec() {
        return CODEC;
    }
}
