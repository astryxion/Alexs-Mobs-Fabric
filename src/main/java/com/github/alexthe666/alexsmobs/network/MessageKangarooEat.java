package com.github.alexthe666.alexsmobs.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Server -> Client packet to display kangaroo eating particles.
 */
public record MessageKangarooEat(int kangaroo, ItemStack stack) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageKangarooEat> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("alexsmobs", "kangaroo_eat"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageKangarooEat> CODEC = new StreamCodec<>() {
        @Override
        public MessageKangarooEat decode(RegistryFriendlyByteBuf buf) {
            int kangaroo = buf.readInt();
            ItemStack stack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
            return new MessageKangarooEat(kangaroo, stack);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageKangarooEat packet) {
            buf.writeInt(packet.kangaroo);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.stack);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}