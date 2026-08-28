package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.util.function.Supplier;

public class MessageUpdateTransmutablesToDisplay {

    private int playerId;
    public ItemStack stack1;
    public ItemStack stack2;
    public ItemStack stack3;

    public MessageUpdateTransmutablesToDisplay(int playerId, ItemStack stack1, ItemStack stack2, ItemStack stack3) {
        this.stack1 = stack1;
        this.stack2 = stack2;
        this.stack3 = stack3;
        this.playerId = playerId;
    }

    public MessageUpdateTransmutablesToDisplay() {
    }

    public static MessageUpdateTransmutablesToDisplay read(FriendlyByteBuf buf) {
        int playerId = buf.readInt();
        net.minecraft.nbt.CompoundTag t1 = buf.readNbt(), t2 = buf.readNbt(), t3 = buf.readNbt();
        return new MessageUpdateTransmutablesToDisplay(
                playerId,
                t1 != null ? ItemStack.of(t1) : ItemStack.EMPTY,
                t2 != null ? ItemStack.of(t2) : ItemStack.EMPTY,
                t3 != null ? ItemStack.of(t3) : ItemStack.EMPTY);
    }

    public static void write(MessageUpdateTransmutablesToDisplay message, FriendlyByteBuf buf) {
        buf.writeInt(message.playerId);
        buf.writeNbt(message.stack1.isEmpty() ? null : message.stack1.save(new net.minecraft.nbt.CompoundTag()));
        buf.writeNbt(message.stack2.isEmpty() ? null : message.stack2.save(new net.minecraft.nbt.CompoundTag()));
        buf.writeNbt(message.stack3.isEmpty() ? null : message.stack3.save(new net.minecraft.nbt.CompoundTag()));
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageUpdateTransmutablesToDisplay message, Supplier<AlexsMobs.PacketContext> context) {
            context.get().setPacketHandled(true);
            context.get().enqueueWork(() -> {
                Player player = context.get().getSender();
                if (context.get().isClient()) {
                    player = AlexsMobs.PROXY.getClientSidePlayer();
                }
                if (player.getId() == message.playerId) {
                    AlexsMobs.PROXY.setDisplayTransmuteResult(0, message.stack1);
                    AlexsMobs.PROXY.setDisplayTransmuteResult(1, message.stack2);
                    AlexsMobs.PROXY.setDisplayTransmuteResult(2, message.stack3);
                }
            });
        }
    }
}