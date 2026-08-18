package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

/**
 * Bidirectional packet to dismount entities from players.
 */
public record MessageMosquitoDismount(int rider, int mount) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageMosquitoDismount> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("alexsmobs", "mosquito_dismount"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageMosquitoDismount> CODEC = new StreamCodec<>() {
        @Override
        public MessageMosquitoDismount decode(RegistryFriendlyByteBuf buf) {
            int rider = buf.readInt();
            int mount = buf.readInt();
            return new MessageMosquitoDismount(rider, mount);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageMosquitoDismount packet) {
            buf.writeInt(packet.rider);
            buf.writeInt(packet.mount);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleServer(MessageMosquitoDismount payload, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();
        if (player != null && player.level() != null) {
            Entity entity = player.level().getEntity(payload.rider);
            Entity mountEntity = player.level().getEntity(payload.mount);
            if ((entity instanceof EntityCrimsonMosquito || entity instanceof EntityBaldEagle
                    || entity instanceof EntityEnderiophage) && mountEntity != null) {
                entity.stopRiding();
            }
        }
    }
}