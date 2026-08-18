package com.github.alexthe666.alexsmobs.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Server -> Client packet to mount crow on player.
 */
public record MessageCrowMountPlayer(int rider, int mount) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageCrowMountPlayer> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("alexsmobs", "crow_mount_player"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageCrowMountPlayer> CODEC = new StreamCodec<>() {
        @Override
        public MessageCrowMountPlayer decode(RegistryFriendlyByteBuf buf) {
            int rider = buf.readInt();
            int mount = buf.readInt();
            return new MessageCrowMountPlayer(rider, mount);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageCrowMountPlayer packet) {
            buf.writeInt(packet.rider);
            buf.writeInt(packet.mount);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}