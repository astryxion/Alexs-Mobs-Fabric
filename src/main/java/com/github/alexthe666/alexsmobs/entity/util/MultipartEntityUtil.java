package com.github.alexthe666.alexsmobs.entity.util;

import net.minecraft.world.entity.LivingEntity;

public class MultipartEntityUtil {

    private MultipartEntityUtil() {
    }

    /** Copies parent hurt/death timers to a body segment (vanilla tracking syncs visuals to clients). */
    public static void syncHurtTimesFromParent(LivingEntity part, LivingEntity parent) {
        if (parent.hurtTime > 0 || parent.deathTime > 0) {
            part.hurtTime = parent.hurtTime;
            part.deathTime = parent.deathTime;
        }
    }
}
