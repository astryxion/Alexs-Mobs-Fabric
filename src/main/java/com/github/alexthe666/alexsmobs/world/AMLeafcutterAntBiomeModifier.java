package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.resources.ResourceLocation;

/** Fabric: 1:1 replacement for Forge BiomeModifier; adds leafcutter anthill feature via BiomeModifications. */
public class AMLeafcutterAntBiomeModifier {

    public static void register() {
        BiomeModifications.create(new ResourceLocation(AlexsMobs.MODID, "am_leafcutter_ant_spawns"))
                .add(ModificationPhase.ADDITIONS, ctx -> true, (selectionContext, modificationContext) -> {
                    AMWorldRegistry.addLeafcutterAntSpawns(selectionContext.getBiomeRegistryEntry(), modificationContext.getGenerationSettings());
                });
    }
}
