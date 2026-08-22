package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class EffectExsanguination extends MobEffect {

    private int lastDuration = -1;

    protected EffectExsanguination() {
        super(MobEffectCategory.HARMFUL, 0XED5151);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        float damage = Math.max(1.0F, amplifier + 1);
        entity.hurt(entity.damageSources().magic(), damage);
        for(int i = 0; i < 3; i++){
            entity.level().addParticle(ParticleTypes.DAMAGE_INDICATOR, entity.getRandomX(1.0), entity.getRandomY(), entity.getRandomZ(1.0), 0, 0, 0);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        lastDuration = duration;
        return duration > 0 && duration % 20 == 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.exsanguination";
    }

}
