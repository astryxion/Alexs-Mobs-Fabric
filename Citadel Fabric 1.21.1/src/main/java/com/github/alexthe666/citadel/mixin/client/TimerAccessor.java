package com.github.alexthe666.citadel.mixin.client;

import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor for DeltaTracker.Timer.msPerTick. Used by ClientTickRateTracker to set client
 * tick rate. Mixin rewrites at runtime (intermediary); no reflection in production.
 */
@Mixin(DeltaTracker.Timer.class)
public interface TimerAccessor {

    @Accessor("msPerTick")
    void citadel$setMsPerTick(float msPerTick);
}
