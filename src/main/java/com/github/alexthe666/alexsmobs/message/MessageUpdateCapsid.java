package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityCapsid;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.util.function.Supplier;

public class MessageUpdateCapsid  {

    public long blockPos;
    public ItemStack heldStack;

    public MessageUpdateCapsid(long blockPos, ItemStack heldStack) {
        this.blockPos = blockPos;
        this.heldStack = heldStack;

    }

    public MessageUpdateCapsid() {
    }

    public static MessageUpdateCapsid read(FriendlyByteBuf buf) {
        long pos = buf.readLong();
        net.minecraft.nbt.CompoundTag tag = buf.readNbt();
        return new MessageUpdateCapsid(pos, tag != null ? ItemStack.of(tag) : ItemStack.EMPTY);
    }

    public static void write(MessageUpdateCapsid message, FriendlyByteBuf buf) {
        buf.writeLong(message.blockPos);
        ItemStack stack = message.heldStack == null ? ItemStack.EMPTY : message.heldStack;
        if (stack.isEmpty()) {
            buf.writeNbt(new net.minecraft.nbt.CompoundTag());
        } else {
            buf.writeNbt(stack.save(new net.minecraft.nbt.CompoundTag()));
        }
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageUpdateCapsid message, Supplier<AlexsMobs.PacketContext> context) {
            context.get().setPacketHandled(true);
            context.get().enqueueWork(() -> {
                Player player = context.get().getSender();
                if (context.get().isClient()) {
                    player = AlexsMobs.PROXY.getClientSidePlayer();
                }
                if (player != null) {
                    if (player.level() != null) {
                        BlockPos pos = BlockPos.of(message.blockPos);
                        if (player.level().getBlockEntity(pos) != null) {
                            if (player.level().getBlockEntity(pos) instanceof TileEntityCapsid) {
                                TileEntityCapsid podium = (TileEntityCapsid) player.level().getBlockEntity(pos);
                                podium.setItem(0, message.heldStack);
                            }
                        }
                    }
                }
            });
        }
    }

}