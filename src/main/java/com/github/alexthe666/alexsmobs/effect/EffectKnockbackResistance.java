package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EffectKnockbackResistance extends MobEffect {

    public EffectKnockbackResistance() {
        super(MobEffectCategory.BENEFICIAL, 0X865337);
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "03C3C89D-7037-4B42-869F-B146BCB64D2F", 0.5D, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public void applyEffectTick(LivingEntity LivingEntityIn, int amplifier) {

    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.knockback_resistance";
    }

}
