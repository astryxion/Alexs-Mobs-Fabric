package com.github.alexthe666.alexsmobs.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Server -> Client packet to mount entities on players.
 */
public record MessageMosquitoMountPlayer(int rider, int mount) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageMosquitoMountPlayer> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("alexsmobs", "mosquito_mount_player"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageMosquitoMountPlayer> CODEC = new StreamCodec<>() {
        @Override
        public MessageMosquitoMountPlayer decode(RegistryFriendlyByteBuf buf) {
            int rider = buf.readInt();
            int mount = buf.readInt();
            return new MessageMosquitoMountPlayer(rider, mount);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageMosquitoMountPlayer packet) {
            buf.writeInt(packet.rider);
            buf.writeInt(packet.mount);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}