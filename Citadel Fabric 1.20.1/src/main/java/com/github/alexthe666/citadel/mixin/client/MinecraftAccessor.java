package com.github.alexthe666.citadel.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor for Minecraft client's timer. Used by ClientTickRateTracker to set
 * msPerTick for tick rate modification.
 */
@Mixin(Minecraft.class)
public interface MinecraftAccessor {

    @Accessor("timer")
    Timer citadel$getTimer();
}
