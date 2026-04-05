package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class EffectEarthquake extends MobEffect {

    public EffectEarthquake() {
        super(MobEffectCategory.HARMFUL, 0XF0E9E1);
    }

    public boolean tick(ServerLevel level, LivingEntity entity, int amplifier) {
        return true;
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.earthquake";
    }
}
