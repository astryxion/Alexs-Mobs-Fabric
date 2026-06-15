package com.github.alexthe666.alexsmobs.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Server -> Client packet to sync dancing state.
 */
public record MessageStartDancing(int entityID, boolean dance, BlockPos jukeBox) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageStartDancing> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("alexsmobs", "start_dancing"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageStartDancing> CODEC = new StreamCodec<>() {
        @Override
        public MessageStartDancing decode(RegistryFriendlyByteBuf buf) {
            int entityID = buf.readInt();
            boolean dance = buf.readBoolean();
            BlockPos jukeBox = buf.readBlockPos();
            return new MessageStartDancing(entityID, dance, jukeBox);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageStartDancing packet) {
            buf.writeInt(packet.entityID);
            buf.writeBoolean(packet.dance);
            buf.writeBlockPos(packet.jukeBox);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}