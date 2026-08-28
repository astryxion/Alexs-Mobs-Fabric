package com.github.alexthe666.citadel.mixin.client;

import net.minecraft.client.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor for Timer.msPerTick. Used by ClientTickRateTracker to set client tick rate.
 */
@Mixin(Timer.class)
public interface TimerAccessor {

    @Accessor("msPerTick")
    void citadel$setMsPerTick(float msPerTick);
}
