package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.*;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import java.util.function.Supplier;

public class MessageHurtMultipart {

    public int part;
    public int parent;
    public float damage;
    public String damageType;

    public MessageHurtMultipart(int part, int parent, float damage) {
        this.part = part;
        this.parent = parent;
        this.damage = damage;
        this.damageType = "";
    }

    public MessageHurtMultipart(int part, int parent, float damage, String damageType) {
        this.part = part;
        this.parent = parent;
        this.damage = damage;
        this.damageType = damageType;
    }

    public MessageHurtMultipart() {
    }

    public static MessageHurtMultipart read(FriendlyByteBuf buf) {
        return new MessageHurtMultipart(buf.readInt(), buf.readInt(), buf.readFloat(), buf.readUtf());
    }

    public static void write(MessageHurtMultipart message, FriendlyByteBuf buf) {
        buf.writeInt(message.part);
        buf.writeInt(message.parent);
        buf.writeFloat(message.damage);
        buf.writeUtf(message.damageType);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageHurtMultipart message, Supplier<AlexsMobs.PacketContext> context) {
            context.get().setPacketHandled(true);
            context.get().enqueueWork(() -> {
                Player player = context.get().getSender();
                if (context.get().isClient()) {
                    player = AlexsMobs.PROXY.getClientSidePlayer();
                }

                if (player != null && player.level() != null) {
                    Entity part = player.level().getEntity(message.part);
                    Entity parent = player.level().getEntity(message.parent);
                    if (message.damageType.isEmpty() && message.damage == 0) {
                        if (part instanceof IHurtableMultipart && parent instanceof LivingEntity livingParent) {
                            ((IHurtableMultipart) part).onAttackedFromServer(livingParent, 0, livingParent.damageSources().generic());
                        }
                        return;
                    }
                    Registry<DamageType> registry = player.level().registryAccess().registry(Registries.DAMAGE_TYPE).get();
                    ResourceLocation damageId = ResourceLocation.tryParse(message.damageType);
                    if (damageId == null) {
                        return;
                    }
                    DamageType dmg = registry.get(damageId);
                    if (dmg != null) {
                        Holder<DamageType> holder = registry.getHolder(registry.getId(dmg)).orElse(null);
                        if (holder != null) {
                            DamageSource source = new DamageSource(holder);
                            if (part instanceof IHurtableMultipart && parent instanceof LivingEntity) {
                                ((IHurtableMultipart) part).onAttackedFromServer((LivingEntity) parent, message.damage, source);
                            }
                            if (part == null && parent != null && isMultipartEntity(parent)) {
                                parent.hurt(source, message.damage);
                            }
                        }
                    }
                }
            });
        }
    }

    /** Fabric 1.20.1: Entity.isMultipartEntity() not in API; check known multipart root types (1:1). */
    private static boolean isMultipartEntity(Entity e) {
        return e instanceof EntityVoidWorm || e instanceof EntityBoneSerpent || e instanceof EntityGiantSquid
            || e instanceof EntityLaviathan || e instanceof EntityCachalotWhale || e instanceof EntityAnaconda
            || e instanceof EntityCentipedeHead || e instanceof EntityMurmur;
    }
}