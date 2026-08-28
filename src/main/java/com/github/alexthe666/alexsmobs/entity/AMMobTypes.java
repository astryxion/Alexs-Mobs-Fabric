package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.world.entity.LivingEntity;

/** Prefers Alex's Mobs type overrides, then vanilla LivingEntity#getMobType(). */
public final class AMMobTypes {
    private AMMobTypes() {}

    public static net.minecraft.world.entity.MobType getMobType(LivingEntity entity) {
        if (entity instanceof EntityBoneSerpent || entity instanceof EntityBoneSerpentPart
                || entity instanceof EntitySkelewag || entity instanceof EntitySoulVulture
                || entity instanceof EntityMurmur || entity instanceof EntityMurmurHead) {
            return net.minecraft.world.entity.MobType.UNDEAD;
        }
        if (entity instanceof EntityOrca || entity instanceof EntityLobster || entity instanceof EntityTerrapin
                || entity instanceof EntityGiantSquid || entity instanceof EntityLaviathan
                || entity instanceof EntityCachalotWhale || entity instanceof EntityCombJelly) {
            return net.minecraft.world.entity.MobType.WATER;
        }
        if (entity instanceof EntityLeafcutterAnt || entity instanceof EntityMantisShrimp || entity instanceof EntityFly
                || entity instanceof EntityCrimsonMosquito || entity instanceof EntityWarpedMosco
                || entity instanceof EntityCockroach || entity instanceof EntityTarantulaHawk || entity instanceof EntityTriops
                || entity instanceof EntityEndergrade || entity instanceof EntityCentipedeHead
                || entity instanceof EntityCentipedeBody || entity instanceof EntityCentipedeTail) {
            return net.minecraft.world.entity.MobType.ARTHROPOD;
        }
        return entity.getMobType();
    }
}
