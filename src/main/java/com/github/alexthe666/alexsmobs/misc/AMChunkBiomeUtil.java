package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.mixin.LevelChunkSectionBiomesAccessor;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;

/** Writes biome palettes into chunk sections (requires alexsmobs.accesswidener). */
public final class AMChunkBiomeUtil {

    private AMChunkBiomeUtil() {
    }

    public static void setSectionBiomes(LevelChunkSection section, PalettedContainer<Holder<Biome>> container) {
        ((LevelChunkSectionBiomesAccessor) section).alexsmobs$setBiomes(container);
    }
}
