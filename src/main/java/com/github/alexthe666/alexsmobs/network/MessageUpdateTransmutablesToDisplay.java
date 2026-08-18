package com.github.alexthe666.alexsmobs.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Server -> Client packet to update transmutation display options.
 */
public record MessageUpdateTransmutablesToDisplay(int playerId, ItemStack stack1, ItemStack stack2, ItemStack stack3) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageUpdateTransmutablesToDisplay> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("alexsmobs", "update_transmutables"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageUpdateTransmutablesToDisplay> CODEC = new StreamCodec<>() {
        @Override
        public MessageUpdateTransmutablesToDisplay decode(RegistryFriendlyByteBuf buf) {
            int playerId = buf.readInt();
            ItemStack stack1 = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
            ItemStack stack2 = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
            ItemStack stack3 = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
            return new MessageUpdateTransmutablesToDisplay(playerId, stack1, stack2, stack3);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageUpdateTransmutablesToDisplay packet) {
            buf.writeInt(packet.playerId);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.stack1);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.stack2);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.stack3);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}