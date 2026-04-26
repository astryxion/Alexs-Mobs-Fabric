package com.github.alexthe666.alexsmobs.entity.util;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.network.MessageSyncEntityData;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public class VineLassoUtil {

    private static final String LASSO_PACKET = "LassoSentPacketAlexsMobs";
    private static final String LASSO_REMOVED = "LassoRemovedAlexsMobs";
    private static final String LASSOED_TO_TAG = "LassoOwnerAlexsMobs";
    private static final String LASSOED_TO_ENTITY_ID_TAG = "LassoOwnerIDAlexsMobs";

    public static void lassoTo(@Nullable LivingEntity lassoer, LivingEntity lassoed) {
        CompoundTag lassoedTag = CitadelEntityData.getOrCreateCitadelTag(lassoed);
        if (lassoer == null) {
            SquidGrappleUtil.putUuid(lassoedTag, LASSOED_TO_TAG, UUID.randomUUID());
            lassoedTag.putInt(LASSOED_TO_ENTITY_ID_TAG, -1);
            lassoedTag.putBoolean(LASSO_REMOVED, true);
        } else {
            SquidGrappleUtil.putUuid(lassoedTag, LASSOED_TO_TAG, lassoer.getUUID());
            lassoedTag.putInt(LASSOED_TO_ENTITY_ID_TAG, lassoer.getId());
            lassoedTag.putBoolean(LASSO_REMOVED, false);
        }
        lassoedTag.putBoolean(LASSO_PACKET, true);
        CitadelEntityData.setCitadelTag(lassoed, lassoedTag);
        if(!lassoed.level().isClientSide()){
            AlexsMobs.sendMSGToAll(new MessageSyncEntityData(lassoed.getId(), lassoedTag));
        }
    }

    public static boolean hasLassoData(LivingEntity lasso) {
        CompoundTag lassoedTag = CitadelEntityData.getOrCreateCitadelTag(lasso);
        if (lassoedTag.getBooleanOr(LASSO_REMOVED, false)) {
            return false;
        }
        boolean hasEntityId = lassoedTag.contains(LASSOED_TO_ENTITY_ID_TAG) && lassoedTag.getIntOr(LASSOED_TO_ENTITY_ID_TAG, -1) != -1;
        boolean hasUuid = SquidGrappleUtil.readUuid(lassoedTag, LASSOED_TO_TAG).isPresent();
        return hasEntityId || hasUuid;
    }

    public static Entity getLassoedTo(LivingEntity lassoed) {
        CompoundTag lassoedTag = CitadelEntityData.getOrCreateCitadelTag(lassoed);
        if(lassoedTag.getBooleanOr(LASSO_REMOVED, false)){
            return null;
        }
        if (hasLassoData(lassoed)) {
            int entityId = lassoedTag.contains(LASSOED_TO_ENTITY_ID_TAG) ? lassoedTag.getIntOr(LASSOED_TO_ENTITY_ID_TAG, -1) : -1;
            if (entityId != -1) {
                Entity found = lassoed.level().getEntity(entityId);
                if (found != null) {
                    return found;
                }
            }
            UUID uuid = SquidGrappleUtil.readUuid(lassoedTag, LASSOED_TO_TAG).orElse(null);
            if (uuid != null) {
                if (lassoed.level() instanceof ServerLevel serverLevel) {
                    Entity found = serverLevel.getEntity(uuid);
                    if (found != null) {
                        lassoedTag.putInt(LASSOED_TO_ENTITY_ID_TAG, found.getId());
                        return found;
                    }
                }
                if (lassoed.level().isClientSide()) {
                    return lassoed.level().getPlayerByUUID(uuid);
                }
            }
        }
        return null;
    }

    public static void tickLasso(LivingEntity lassoed) {
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(lassoed);
        if (!lassoed.level().isClientSide()) {
            if (tag.contains(LASSO_PACKET) || tag.getBooleanOr(LASSO_REMOVED, false)) {
                tag.putBoolean(LASSO_PACKET, false);
                CitadelEntityData.setCitadelTag(lassoed, tag);
                AlexsMobs.sendMSGToAll(new MessageSyncEntityData(lassoed.getId(), tag));
            }
        }
        Entity lassoedOwner = VineLassoUtil.getLassoedTo(lassoed);
        if (lassoedOwner != null) {
            double distance = lassoed.distanceTo(lassoedOwner);

            if (lassoed instanceof Mob) {
                Mob mob = (Mob) lassoed;
                if (distance > 3.0F) {
                    mob.getNavigation().moveTo(lassoedOwner, 1.0F);
                } else {
                    mob.getNavigation().stop();
                }
            }
            if (distance > 10) {
                double d0 = (lassoedOwner.getX() - lassoed.getX()) / (double)distance;
                double d1 = (lassoedOwner.getY() - lassoed.getY()) / (double)distance;
                double d2 = (lassoedOwner.getZ() - lassoed.getZ()) / (double)distance;
                double yd = Math.copySign(d1 * d1 * 0.4D, d1);
                if(lassoed instanceof Player){
                    yd = 0;
                }
                lassoed.setDeltaMovement(lassoed.getDeltaMovement().add(Math.copySign(d0 * d0 * 0.4D, d0), yd, Math.copySign(d2 * d2 * 0.4D, d2)));
            }
        }
    }
}
