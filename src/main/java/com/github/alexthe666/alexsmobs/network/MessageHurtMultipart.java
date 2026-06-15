package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.entity.IHurtableMultipart;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * Bidirectional packet to hurt multipart entities.
 */
public record MessageHurtMultipart(int part, int parent, float damage, String damageType) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageHurtMultipart> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("alexsmobs", "hurt_multipart"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageHurtMultipart> CODEC = new StreamCodec<>() {
        @Override
        public MessageHurtMultipart decode(RegistryFriendlyByteBuf buf) {
            int part = buf.readInt();
            int parent = buf.readInt();
            float damage = buf.readFloat();
            String damageType = buf.readUtf();
            return new MessageHurtMultipart(part, parent, damage, damageType);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageHurtMultipart packet) {
            buf.writeInt(packet.part);
            buf.writeInt(packet.parent);
            buf.writeFloat(packet.damage);
            buf.writeUtf(packet.damageType);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleServer(MessageHurtMultipart payload, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();
        if (player != null && player.level() != null) {
            Entity part = player.level().getEntity(payload.part);
            Entity parent = player.level().getEntity(payload.parent);

            if (part instanceof IHurtableMultipart && parent instanceof LivingEntity) {
                ((IHurtableMultipart) part).onAttackedFromServer((LivingEntity) parent, payload.damage, null);
            }

            if (payload.damageType != null && !payload.damageType.isEmpty()) {
                ResourceKey<DamageType> key = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.parse(payload.damageType));
                player.level().registryAccess().lookup(Registries.DAMAGE_TYPE).flatMap(reg -> reg.get(key)).ifPresent(holder -> {
                    DamageSource source = new DamageSource(holder);
                    if (part == null && parent instanceof LivingEntity) {
                        parent.hurt(source, payload.damage);
                    }
                });
            }
        }
    }
}