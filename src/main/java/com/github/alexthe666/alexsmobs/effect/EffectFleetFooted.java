package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class EffectFleetFooted extends MobEffect {

    private static final UUID FLEET_FOOTED_SPEED_UUID = UUID.fromString("7E1C5A6A-6F4E-4B8A-9F2C-1D3E4F5A6B7C");
    private static final AttributeModifier SPRINT_JUMP_SPEED_BONUS = new AttributeModifier(FLEET_FOOTED_SPEED_UUID, "fleet_footed_speed", 0.2, AttributeModifier.Operation.ADDITION);
    private int lastDuration = -1;
    private int removeEffectAfter = 0;

    public EffectFleetFooted() {
        super(MobEffectCategory.BENEFICIAL, 0X685441);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        AttributeInstance modifiableattributeinstance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        boolean applyEffect = entity.isSprinting() && !entity.onGround() && lastDuration > 2;
        if (removeEffectAfter > 0) {
            removeEffectAfter--;
        }
        if (applyEffect) {
            if (modifiableattributeinstance != null && !modifiableattributeinstance.hasModifier(SPRINT_JUMP_SPEED_BONUS)) {
                modifiableattributeinstance.addPermanentModifier(SPRINT_JUMP_SPEED_BONUS);
            }
            removeEffectAfter = 5;
        }
        if (removeEffectAfter <= 0 || lastDuration < 2) {
            if (modifiableattributeinstance != null) {
                modifiableattributeinstance.removeModifier(SPRINT_JUMP_SPEED_BONUS);
            }
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        AttributeInstance modifiableattributeinstance = attributeMap.getInstance(Attributes.MOVEMENT_SPEED);
        if (modifiableattributeinstance != null && modifiableattributeinstance.hasModifier(SPRINT_JUMP_SPEED_BONUS)) {
            modifiableattributeinstance.removeModifier(SPRINT_JUMP_SPEED_BONUS);
        }
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        lastDuration = duration;
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.fleet_footed";
    }
}
