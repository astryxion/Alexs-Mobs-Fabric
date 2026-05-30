package com.github.alexthe666.alexsmobs.mixin;

import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Structure.class)
public interface StructureAccessor {
    @Accessor("settings")
    Structure.StructureSettings alexsmobs$getSettings();
}
