package com.github.alexthe666.citadel.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor for Minecraft client's timer. Used by ClientTickRateTracker to set
 * msPerTick for tick rate modification. Mixin rewrites at runtime (intermediary);
 * no reflection in production.
 */
@Mixin(Minecraft.class)
public interface MinecraftAccessor {

    @Accessor("timer")
    DeltaTracker.Timer citadel$getTimer();
}
