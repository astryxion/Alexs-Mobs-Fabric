package com.github.alexthe666.alexsmobs.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Server -> Client packet to sync mungus biome transformation.
 */
public record MessageMungusBiomeChange(int mungusID, int posX, int posZ, String biomeOption) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageMungusBiomeChange> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("alexsmobs", "mungus_biome_change"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageMungusBiomeChange> CODEC = new StreamCodec<>() {
        @Override
        public MessageMungusBiomeChange decode(RegistryFriendlyByteBuf buf) {
            int mungusID = buf.readInt();
            int posX = buf.readInt();
            int posZ = buf.readInt();
            String biomeOption = buf.readUtf();
            return new MessageMungusBiomeChange(mungusID, posX, posZ, biomeOption);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageMungusBiomeChange packet) {
            buf.writeInt(packet.mungusID);
            buf.writeInt(packet.posX);
            buf.writeInt(packet.posZ);
            buf.writeUtf(packet.biomeOption);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}