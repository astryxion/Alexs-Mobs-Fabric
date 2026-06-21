package com.github.alexthe666.alexsmobs.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Server -> Client packet to apply tarantula hawk sting effect.
 */
public record MessageTarantulaHawkSting(int hawk, int spider) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageTarantulaHawkSting> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("alexsmobs", "tarantula_hawk_sting"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageTarantulaHawkSting> CODEC = new StreamCodec<>() {
        @Override
        public MessageTarantulaHawkSting decode(RegistryFriendlyByteBuf buf) {
            int hawk = buf.readInt();
            int spider = buf.readInt();
            return new MessageTarantulaHawkSting(hawk, spider);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageTarantulaHawkSting packet) {
            buf.writeInt(packet.hawk);
            buf.writeInt(packet.spider);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}