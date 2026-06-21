package com.github.alexthe666.alexsmobs.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Server -> Client packet to update capsid inventory.
 */
public record MessageUpdateCapsid(long blockPos, ItemStack heldStack) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageUpdateCapsid> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("alexsmobs", "update_capsid"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageUpdateCapsid> CODEC = new StreamCodec<>() {
        @Override
        public MessageUpdateCapsid decode(RegistryFriendlyByteBuf buf) {
            long blockPos = buf.readLong();
            ItemStack heldStack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
            return new MessageUpdateCapsid(blockPos, heldStack);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageUpdateCapsid packet) {
            buf.writeLong(packet.blockPos);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.heldStack);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}