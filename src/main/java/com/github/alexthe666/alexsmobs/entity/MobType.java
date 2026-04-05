package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;

/**
 * Replacement for removed vanilla MobType in 1.21.1.
 * Use {@link #getMobType(LivingEntity)} instead of entity.getMobType().
 */
public enum MobType {
    UNDEAD,
    ARTHROPOD,
    WATER,
    ILLAGER,
    UNDEFINED;

    public static MobType getMobType(LivingEntity entity) {
        // Alex's Mobs entities (1:1 behavior from former getMobType overrides)
        if (entity instanceof EntityBoneSerpent || entity instanceof EntityBoneSerpentPart
                || entity instanceof EntitySkelewag || entity instanceof EntitySoulVulture
                || entity instanceof EntityMurmur || entity instanceof EntityMurmurHead) {
            return UNDEAD;
        }
        if (entity instanceof EntityOrca || entity instanceof EntityLobster || entity instanceof EntityTerrapin
                || entity instanceof EntityGiantSquid || entity instanceof EntityLaviathan
                || entity instanceof EntityCachalotWhale || entity instanceof EntityCombJelly) {
            return WATER;
        }
        if (entity instanceof EntityLeafcutterAnt || entity instanceof EntityMantisShrimp || entity instanceof EntityFly
                || entity instanceof EntityCrimsonMosquito || entity instanceof EntityWarpedMosco
                || entity instanceof EntityCockroach || entity instanceof EntityTarantulaHawk || entity instanceof EntityTriops
                || entity instanceof EntityEndergrade || entity instanceof EntityCentipedeHead
                || entity instanceof EntityCentipedeBody || entity instanceof EntityCentipedeTail) {
            return ARTHROPOD;
        }
        // Vanilla and other mods: use entity type tags
        if (entity.getType().is(EntityTypeTags.UNDEAD)) {
            return UNDEAD;
        }
        if (entity.getType().is(EntityTypeTags.ARTHROPOD)) {
            return ARTHROPOD;
        }
        if (entity.getType().is(EntityTypeTags.AQUATIC)) {
            return WATER;
        }
        if (entity.getType().is(EntityTypeTags.ILLAGER)) {
            return ILLAGER;
        }
        return UNDEFINED;
    }
}
