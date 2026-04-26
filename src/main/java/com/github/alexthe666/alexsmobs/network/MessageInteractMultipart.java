package com.github.alexthe666.alexsmobs.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Client -> Server packet to interact with multipart entities.
 */
public record MessageInteractMultipart(int parent, boolean offhand) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageInteractMultipart> ID =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("alexsmobs", "interact_multipart"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageInteractMultipart> CODEC = new StreamCodec<>() {
        @Override
        public MessageInteractMultipart decode(RegistryFriendlyByteBuf buf) {
            int parent = buf.readInt();
            boolean offhand = buf.readBoolean();
            return new MessageInteractMultipart(parent, offhand);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageInteractMultipart packet) {
            buf.writeInt(packet.parent);
            buf.writeBoolean(packet.offhand);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    // Unified handler for bidirectional packet
    public static void handle(MessageInteractMultipart payload, ClientPlayNetworking.Context context) {
                    var player = context.player();
            if (player != null && player.level() != null) {
                Entity parent = player.level().getEntity(payload.parent);
                if (parent != null && player.distanceTo(parent) < 20 && parent instanceof Mob) {
                    player.interactOn(parent, payload.offhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, Vec3.ZERO);
                }
            }

    }

    public static void handleServer(MessageInteractMultipart payload, ServerPlayNetworking.Context context) {
                    ServerPlayer player = context.player();
            if (player != null && player.level() != null) {
                Entity parent = player.level().getEntity(payload.parent);
                if (player.distanceTo(parent) < 20 && parent instanceof Mob) {
                    player.interactOn(parent, payload.offhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, Vec3.ZERO);
                }
            }

    }
}
