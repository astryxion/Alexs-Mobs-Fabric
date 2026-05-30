package com.github.alexthe666.alexsmobs.world;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

import net.minecraft.world.entity.EntityType;

/** Implemented by {@link com.github.alexthe666.alexsmobs.mixin.StructureSettingsMixin}. */
public interface StructureSettingsExtension {
    void alexsmobs$addSpawn(MobCategory category, MobSpawnSettings.SpawnerData data);

    boolean alexsmobs$hasSpawn(MobCategory category, EntityType<?> type);
}
