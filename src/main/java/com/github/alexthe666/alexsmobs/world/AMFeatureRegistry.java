package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class AMFeatureRegistry {
    public static final Feature<NoneFeatureConfiguration> LEAFCUTTER_ANTHILL = Registry.register(BuiltInRegistries.FEATURE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "leafcutter_anthill"), new FeatureLeafcutterAnthill(NoneFeatureConfiguration.CODEC));

    public static void init() {
    }

}
