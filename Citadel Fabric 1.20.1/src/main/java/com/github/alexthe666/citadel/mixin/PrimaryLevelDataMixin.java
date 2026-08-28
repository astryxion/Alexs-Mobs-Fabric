package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.generation.NoiseGeneratorSettingsAccessor;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(PrimaryLevelData.class)
public class PrimaryLevelDataMixin {

    @Inject(at = @At("HEAD"), remap = CitadelConstants.REMAPREFS,
            method = "setTagData")
    private void citadel_preSetTagData(RegistryAccess registryAccess, CompoundTag compoundTag, CompoundTag compoundTag1, CallbackInfo ci) {
        citadelUpdateSurfaceRules(registryAccess, true);
    }

    @Inject(at = @At("TAIL"), remap = CitadelConstants.REMAPREFS,
            method = "setTagData")
    private void citadel_postSetTagData(RegistryAccess registryAccess, CompoundTag compoundTag, CompoundTag compoundTag1, CallbackInfo ci) {
        citadelUpdateSurfaceRules(registryAccess, false);
    }

    @Unique
    private void citadelUpdateSurfaceRules(RegistryAccess registryAccess, boolean saving) {
        Registry<LevelStem> registry = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
        if (registry.containsKey(LevelStem.OVERWORLD)) {
            LevelStem levelstem = registry.get(LevelStem.OVERWORLD);
            if (levelstem.generator() instanceof NoiseBasedChunkGenerator noiseBasedChunkGenerator) {
                try {
                    java.lang.reflect.Field settingsField = NoiseBasedChunkGenerator.class.getDeclaredField("settings");
                    settingsField.setAccessible(true);
                    Object settingsHolder = settingsField.get(noiseBasedChunkGenerator);
                    if (settingsHolder instanceof net.minecraft.core.Holder<?> holder && holder.isBound() && holder.value() instanceof NoiseGeneratorSettingsAccessor accessor) {
                        accessor.onSaveData(saving);
                    }
                } catch (Exception e) {
                    Citadel.LOGGER.debug("Could not access NoiseBasedChunkGenerator.settings for surface rule save", e);
                }
            }
        }
    }

}
