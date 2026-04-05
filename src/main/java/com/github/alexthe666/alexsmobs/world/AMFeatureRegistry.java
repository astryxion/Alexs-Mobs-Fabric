package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class AMFeatureRegistry {

    public static Feature<NoneFeatureConfiguration> LEAFCUTTER_ANTHILL;

    public static void init() {
        LEAFCUTTER_ANTHILL = Registry.register(BuiltInRegistries.FEATURE, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "leafcutter_anthill"),
                new FeatureLeafcutterAnthill(NoneFeatureConfiguration.CODEC));
    }
}
