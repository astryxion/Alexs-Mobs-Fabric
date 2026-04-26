package com.github.alexthe666.alexsmobs.entity.util;

import com.github.alexthe666.alexsmobs.entity.EntitySquidGrapple;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public class SquidGrappleUtil {

    private static final String HOOK_1 = "SquidGrappleHook1AlexsMobs";
    private static final String HOOK_2 = "SquidGrappleHook2AlexsMobs";
    private static final String HOOK_3 = "SquidGrappleHook3AlexsMobs";
    private static final String HOOK_4 = "SquidGrappleHook4AlexsMobs";
    private static final String LAST_REPLACED_HOOK = "LastSquidGrappleHookAlexsMobs";

    public static int onFireHook(LivingEntity entity, UUID newHookUUID) {
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(entity);
        int index = getFirstAvailableHookIndex(entity);
        String indexStr = getHookStrFromIndex(index);
        if(tag.contains(indexStr)){
            EntitySquidGrapple hook = getHookEntity(entity.level(), readUuid(tag, indexStr).orElse(null));
            if(hook != null && !hook.isRemoved()){
                hook.setWithdrawing(true);
            }
        }
        putUuid(tag, indexStr, newHookUUID);
        CitadelEntityData.setCitadelTag(entity, tag);
        return index;
    }

    public static int getFirstAvailableHookIndex(LivingEntity entity){
        int nulls = getAnyNullHooks(entity);
        if(nulls != -1){
            return nulls;
        }
        int i = getHookCount(entity);
        if(i < 4){
            return i;
        }else{
            CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(entity);
            int j = tag.getIntOr(LAST_REPLACED_HOOK, 0);
            tag.putInt(LAST_REPLACED_HOOK, (j + 1) % 4);
            CitadelEntityData.setCitadelTag(entity, tag);
            return j;
        }
    }

    public static String getHookStrFromIndex(int i){
        switch (i){
            case 0:
                return HOOK_1;
            case 1:
                return HOOK_2;
            case 2:
                return HOOK_3;
            case 3:
                return HOOK_4;
        }
        return HOOK_1;
    }

    public static int getAnyNullHooks(LivingEntity entity) {
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(entity);
        if (!tag.contains(HOOK_1) || getHookEntity(entity.level(), readUuid(tag, HOOK_1).orElse(null)) == null) {
            return 0;
        }
        if (!tag.contains(HOOK_2) || getHookEntity(entity.level(), readUuid(tag, HOOK_2).orElse(null)) == null) {
            return 1;
        }
        if (!tag.contains(HOOK_3) || getHookEntity(entity.level(), readUuid(tag, HOOK_3).orElse(null)) == null) {
            return 2;
        }
        if (!tag.contains(HOOK_4) || getHookEntity(entity.level(), readUuid(tag, HOOK_4).orElse(null)) == null) {
            return 3;
        }
        return -1;
    }


    public static int getHookCount(LivingEntity entity) {
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(entity);
        int count = 0;
        if (tag.contains(HOOK_1) && getHookEntity(entity.level(), readUuid(tag, HOOK_1).orElse(null)) != null) {
            count++;
        }
        if (tag.contains(HOOK_2) && getHookEntity(entity.level(), readUuid(tag, HOOK_2).orElse(null)) != null) {
            count++;
        }
        if (tag.contains(HOOK_3) && getHookEntity(entity.level(), readUuid(tag, HOOK_3).orElse(null)) != null) {
            count++;
        }
        if (tag.contains(HOOK_4) && getHookEntity(entity.level(), readUuid(tag, HOOK_4).orElse(null)) != null) {
            count++;
        }
        return count;
    }

    public static EntitySquidGrapple getHookEntity(Level level, UUID id) {
        if (id != null && !level.isClientSide()) {
            Entity e = ((ServerLevel) level).getEntity(id);
            return e instanceof EntitySquidGrapple ? (EntitySquidGrapple) e : null;
        }
        return null;
    }

    static void putUuid(CompoundTag tag, String key, UUID uuid) {
        tag.store(key, UUIDUtil.CODEC, uuid);
    }

    static Optional<UUID> readUuid(CompoundTag tag, String key) {
        if (!tag.contains(key)) {
            return Optional.empty();
        }
        Optional<UUID> fromCodec = tag.read(key, UUIDUtil.CODEC);
        if (fromCodec.isPresent()) {
            return fromCodec;
        }
        Tag raw = tag.get(key);
        return raw == null ? Optional.empty() : UUIDUtil.CODEC.parse(NbtOps.INSTANCE, raw).result();
    }
}
