package com.github.alexthe666.alexsmobs.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Server -> Client packet to sync kangaroo inventory.
 */
public record MessageKangarooInventorySync(int kangaroo, int slotId, ItemStack stack) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageKangarooInventorySync> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("alexsmobs", "kangaroo_inventory_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageKangarooInventorySync> CODEC = new StreamCodec<>() {
        @Override
        public MessageKangarooInventorySync decode(RegistryFriendlyByteBuf buf) {
            int kangaroo = buf.readInt();
            int slotId = buf.readInt();
            ItemStack stack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
            return new MessageKangarooInventorySync(kangaroo, slotId, stack);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageKangarooInventorySync packet) {
            buf.writeInt(packet.kangaroo);
            buf.writeInt(packet.slotId);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.stack);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}