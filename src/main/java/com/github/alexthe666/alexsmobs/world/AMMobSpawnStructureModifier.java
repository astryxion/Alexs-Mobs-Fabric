package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.resources.ResourceLocation;

/**
 * Fabric: 1:1 replacement for Forge StructureModifier.
 * Structure spawn overrides (mimicube in end city, soul vulture in fossil, skelewag in shipwreck, underminer)
 * are implemented in {@link AMWorldRegistry#modifyStructure}; on Fabric they are applied via
 * datapack (structure JSON spawn_overrides) or a mixin that implements {@link AMWorldRegistry.StructureSpawnTarget}.
 */
public class AMMobSpawnStructureModifier {

    public static void register() {
        // Structure spawn overrides applied via datapack or mixin; see AMWorldRegistry.modifyStructure.
    }
}
