package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.event.EventMergeStructureSpawns;
import net.minecraft.core.BlockPos;
import net.minecraft.util.TriState;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {
    @Inject(
            at = @At("RETURN"),
            remap = CitadelConstants.REMAPREFS,
            cancellable = true,
            method = "getMobsAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/util/random/WeightedList;")
    private void citadel_getMobsAt(Level level, StructureManager structureManager, MobCategory mobCategory, BlockPos pos, CallbackInfoReturnable<WeightedList<MobSpawnSettings.SpawnerData>> cir) {
        WeightedList<MobSpawnSettings.SpawnerData> biomeSpawns = level.environmentAttributes().getValue(EnvironmentAttributes.NATURAL_MOB_SPAWNS, pos).getMobsToSpawn(mobCategory);
        if (biomeSpawns != cir.getReturnValue()) {
            EventMergeStructureSpawns event = new EventMergeStructureSpawns(structureManager, pos, mobCategory, cir.getReturnValue(), biomeSpawns);
            event.post();
            if (event.getResult() == TriState.TRUE) {
                cir.setReturnValue(event.getStructureSpawns());
            }
        }
    }
}
