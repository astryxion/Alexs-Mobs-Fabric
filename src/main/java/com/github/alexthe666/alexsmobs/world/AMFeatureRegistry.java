package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;

public class AMFeatureRegistry {
    public static final MapCodec<? extends Feature> LEAFCUTTER_ANTHILL = Registry.register(BuiltInRegistries.FEATURE_TYPE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "leafcutter_anthill"), FeatureLeafcutterAnthill.CODEC);

    public static void init() {
    }

}