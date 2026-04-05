package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class EffectEnderFlu extends MobEffect {

    private int lastDuration = -1;

    public EffectEnderFlu() {
        super(MobEffectCategory.HARMFUL, 0X6836AA);
    }

    public boolean tick(ServerLevel level, LivingEntity entity, int amplifier) {
        if (lastDuration == 1) {
            int phages = amplifier + 1;
            entity.hurt(entity.damageSources().magic(), phages * 10);
            for (int i = 0; i < phages; i++) {
                EntityEnderiophage phage = AMEntityRegistry.ENDERIOPHAGE.create(level);
                phage.copyPosition(entity);
                phage.onSpawnFromEffect();
                phage.setSkinForDimension();
                if (!level.isClientSide) {
                    phage.setStandardFleeTime();
                    level.addFreshEntity(phage);
                }
            }
        }
        return true;
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        lastDuration = duration;
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.ender_flu";
    }

}