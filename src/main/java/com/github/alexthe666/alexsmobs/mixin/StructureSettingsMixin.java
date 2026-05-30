package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.world.StructureSettingsExtension;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(Structure.StructureSettings.class)
public class StructureSettingsMixin implements StructureSettingsExtension {

    @Mutable
    @Shadow @Final private Map<MobCategory, StructureSpawnOverride> spawnOverrides;

    /** Registry-loaded settings keep an immutable map; copy before first mutation. */
    @Unique
    private void alexsmobs$ensureMutableSpawnOverrides() {
        if (!(this.spawnOverrides instanceof HashMap)) {
            this.spawnOverrides = new HashMap<>(this.spawnOverrides);
        }
    }

    @Override
    public boolean alexsmobs$hasSpawn(MobCategory category, EntityType<?> type) {
        StructureSpawnOverride current = spawnOverrides.get(category);
        if (current == null) {
            return false;
        }
        for (MobSpawnSettings.SpawnerData spawn : current.spawns().unwrap()) {
            if (spawn.type == type) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void alexsmobs$addSpawn(MobCategory category, MobSpawnSettings.SpawnerData data) {
        alexsmobs$ensureMutableSpawnOverrides();
        StructureSpawnOverride current = spawnOverrides.get(category);
        List<MobSpawnSettings.SpawnerData> spawns = new ArrayList<>();
        if (current != null) {
            spawns.addAll(current.spawns().unwrap());
        }
        spawns.add(data);
        StructureSpawnOverride.BoundingBoxType box = current != null
                ? current.boundingBox()
                : StructureSpawnOverride.BoundingBoxType.PIECE;
        spawnOverrides.put(category, new StructureSpawnOverride(box, WeightedRandomList.create(spawns)));
    }
}
