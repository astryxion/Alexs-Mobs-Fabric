package com.github.alexthe666.alexsmobs.entity;

/**
 * Alex's Mobs entities that override vanilla underwater breathing. {@code canBreatheUnderwater()} is final in
 * modern Minecraft, so {@link com.github.alexthe666.alexsmobs.mixin.LivingEntityBreathingMixin} delegates here.
 */
public interface IAMUnderwaterBreather {
    boolean canBreatheUnderwaterAM();
}
