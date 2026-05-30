package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.world.StructureSettingsExtension;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(Structure.StructureSettings.class)
public class StructureSettingsMixin implements StructureSettingsExtension {

    @Mutable
    @Shadow @Final private Map<MobCategory, StructureSpawnOverride> spawnOverrides;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void alexsmobs$makeSpawnOverridesMutable(CallbackInfo ci) {
        this.spawnOverrides = new HashMap<>(this.spawnOverrides);
    }

    @Override
    public void alexsmobs$addSpawn(MobCategory category, MobSpawnSettings.SpawnerData data) {
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
