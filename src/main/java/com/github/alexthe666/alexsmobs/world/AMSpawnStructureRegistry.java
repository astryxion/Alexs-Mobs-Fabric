package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.mixin.StructureAccessor;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;

/** Forge {@code AMMobSpawnStructureModifier} parity for Fabric via structure spawn override mixin. */
public final class AMSpawnStructureRegistry {
    private AMSpawnStructureRegistry() {}

    public static void register() {
        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
            if (client) {
                return;
            }
            registries.registryOrThrow(Registries.STRUCTURE).holders().forEach(AMSpawnStructureRegistry::modifyStructure);
        });
    }

    static void modifyStructure(Holder<Structure> structure) {
        Structure.StructureSettings settings = ((StructureAccessor) structure.value()).alexsmobs$getSettings();
        StructureSettingsExtension ext = (StructureSettingsExtension) (Object) settings;
        if (AMConfig.mimicubeSpawnInEndCity && structure.is(BuiltinStructures.END_CITY) && AMConfig.mimicubeSpawnWeight > 0) {
            ext.alexsmobs$addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.MIMICUBE, AMConfig.mimicubeSpawnWeight, 1, 3));
        }
        if (AMConfig.soulVultureSpawnOnFossil && structure.is(BuiltinStructures.NETHER_FOSSIL) && AMConfig.soulVultureSpawnWeight > 0) {
            ext.alexsmobs$addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.SOUL_VULTURE, AMConfig.soulVultureSpawnWeight, 1, 1));
        }
        if (AMConfig.restrictSkelewagSpawns && structure.is(BuiltinStructures.SHIPWRECK) && AMConfig.skelewagSpawnWeight > 0) {
            ext.alexsmobs$addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.SKELEWAG, AMConfig.skelewagSpawnWeight, 1, 2));
        }
        if (AMConfig.restrictUnderminerSpawns && structure.is(AMTagRegistry.SPAWNS_UNDERMINERS) && AMConfig.underminerSpawnWeight > 0) {
            ext.alexsmobs$addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(AMEntityRegistry.UNDERMINER, AMConfig.underminerSpawnWeight, 1, 1));
        }
    }
}
