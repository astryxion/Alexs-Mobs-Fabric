package com.github.alexthe666.alexsmobs.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Server -> Client packet to sync entity position.
 */
public record MessageSyncEntityPos(int entityId, double x, double y, double z) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageSyncEntityPos> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("alexsmobs", "sync_entity_pos"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSyncEntityPos> CODEC = new StreamCodec<>() {
        @Override
        public MessageSyncEntityPos decode(RegistryFriendlyByteBuf buf) {
            int entityId = buf.readInt();
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            return new MessageSyncEntityPos(entityId, x, y, z);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageSyncEntityPos packet) {
            buf.writeInt(packet.entityId);
            buf.writeDouble(packet.x);
            buf.writeDouble(packet.y);
            buf.writeDouble(packet.z);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}