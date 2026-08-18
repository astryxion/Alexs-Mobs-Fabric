package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import com.github.alexthe666.alexsmobs.entity.EntityPotoo;
import com.github.alexthe666.alexsmobs.entity.EntitySugarGlider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Construction-time collision guards, client multipart IDs, and riding vehicles that cannot serialize.
 */
public final class AMEntityHooks {
    private static final AtomicInteger CLIENT_PART_ID = new AtomicInteger();

    private AMEntityHooks() {
    }

    public static boolean isFullyConstructed(Entity entity) {
        return entity.getEntityData() != null;
    }

    /**
     * Client world ID allocation can leave parts unassigned. Never call {@link Entity#getId()} here — it throws
     * while the id is still 0, which is exactly when multipart parents construct their parts from a spawn packet.
     */
    public static void assignClientPartId(Entity part) {
        if (!part.level().isClientSide()) {
            return;
        }
        int id = nextFallbackPartId();
        try {
            part.setId(id);
        } catch (IllegalStateException ignored) {
            setIdUnchecked(part, id);
        }
    }

    private static void setIdUnchecked(Entity part, int id) {
        try {
            java.lang.reflect.Field field = Entity.class.getDeclaredField("id");
            field.setAccessible(true);
            field.setInt(part, id);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    private static int nextFallbackPartId() {
        int id = CLIENT_PART_ID.decrementAndGet();
        return id == 0 ? CLIENT_PART_ID.decrementAndGet() : id;
    }

    public static boolean ridesUnsaveableVehicles(Entity rider) {
        return rider instanceof EntityCrimsonMosquito
                || rider instanceof EntityEnderiophage
                || rider instanceof EntityBaldEagle
                || rider instanceof EntityCrow
                || rider instanceof EntityCapuchinMonkey
                || rider instanceof EntityPotoo
                || rider instanceof EntitySugarGlider;
    }

    /**
     * Drops an item only on the logical server. Client interaction also runs {@code mobInteract},
     * so casting {@code level()} to {@link ServerLevel} crashes there.
     */
    @Nullable
    public static ItemEntity drop(Entity entity, ItemStack stack) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            return entity.spawnAtLocation(serverLevel, stack);
        }
        return null;
    }

    @Nullable
    public static ItemEntity drop(Entity entity, ItemStack stack, float yOffset) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            return entity.spawnAtLocation(serverLevel, stack, yOffset);
        }
        return null;
    }

    @Nullable
    public static ItemEntity drop(Entity entity, ItemLike item) {
        return drop(entity, new ItemStack(item));
    }

    @Nullable
    public static Entity findEntity(Level level, @Nullable UUID uuid, @Nullable Entity near) {
        if (uuid == null || level == null) {
            return null;
        }
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getEntity(uuid);
        }
        AABB search = near != null ? near.getBoundingBox().inflate(64.0D) : new AABB(-64, -64, -64, 64, 64, 64);
        for (Entity entity : level.getEntities(near, search)) {
            if (uuid.equals(entity.getUUID())) {
                return entity;
            }
        }
        return null;
    }

    public static boolean isShears(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(Items.SHEARS) || stack.getItem() instanceof ShearsItem);
    }

    public static boolean shearWithShears(Mob mob, Player player, InteractionHand hand, ItemStack stack) {
        if (!(mob instanceof Shearable shearable) || !isShears(stack) || !shearable.readyForShearing()) {
            return false;
        }
        if (mob.level().isClientSide()) {
            return true;
        }
        if (mob.level() instanceof ServerLevel serverLevel) {
            shearable.shear(serverLevel, SoundSource.PLAYERS, stack);
            stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }
        return true;
    }
}
