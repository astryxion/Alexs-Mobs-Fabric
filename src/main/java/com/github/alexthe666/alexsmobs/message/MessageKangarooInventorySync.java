package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.util.function.Supplier;

public class MessageKangarooInventorySync {

    public int kangaroo;
    public int slotId;
    public ItemStack stack;

    public MessageKangarooInventorySync(int kangaroo, int slotId, ItemStack stack) {
        this.kangaroo = kangaroo;
        this.slotId = slotId;
        this.stack = stack;
    }

    public MessageKangarooInventorySync() {
    }

    public static MessageKangarooInventorySync read(FriendlyByteBuf buf, net.minecraft.core.HolderLookup.Provider registryAccess) {
        int kangaroo = buf.readInt();
        int slotId = buf.readInt();
        net.minecraft.nbt.CompoundTag tag = buf.readNbt();
        return new MessageKangarooInventorySync(kangaroo, slotId, tag != null ? ItemStack.parseOptional(registryAccess, tag) : ItemStack.EMPTY);
    }

    public static void write(MessageKangarooInventorySync message, FriendlyByteBuf buf, net.minecraft.core.HolderLookup.Provider registryAccess) {
        buf.writeInt(message.kangaroo);
        buf.writeInt(message.slotId);
        buf.writeNbt(message.stack.isEmpty() ? null : message.stack.save(registryAccess));
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageKangarooInventorySync message, Supplier<AlexsMobs.PacketContext> context) {
            context.get().setPacketHandled(true);
            context.get().enqueueWork(() -> {
                Level level = null;
                Player player = context.get().getSender();
                if (context.get().isClient()) {
                    player = AlexsMobs.PROXY.getClientSidePlayer();
                }
                if (player != null) {
                    level = player.level();
                }

                if (level != null) {
                    Entity entity = level.getEntity(message.kangaroo);
                    if (entity instanceof EntityKangaroo kangaroo && kangaroo.kangarooInventory != null && message.slotId >= 0) {
                        kangaroo.kangarooInventory.setItem(message.slotId, message.stack);
                    }
                }
            });
        }
    }
}
