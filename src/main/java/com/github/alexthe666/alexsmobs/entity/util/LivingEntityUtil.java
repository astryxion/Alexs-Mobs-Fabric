package com.github.alexthe666.alexsmobs.entity.util;

import net.minecraft.world.entity.LivingEntity;

public class LivingEntityUtil {

    private LivingEntityUtil() {
    }

    /**
     * Clears the last hurt mob without calling {@link LivingEntity#setLastHurtMob(net.minecraft.world.entity.Entity)} with null.
     * Some mods mixin that method and NPE when the target is null (e.g. Forcemaster RPG).
     */
    public static void clearLastHurtMob(LivingEntity entity) {
        try {
            java.lang.reflect.Field field = LivingEntity.class.getDeclaredField("lastHurtMob");
            field.setAccessible(true);
            field.set(entity, null);
        } catch (Exception ignored) {
        }
    }
}
