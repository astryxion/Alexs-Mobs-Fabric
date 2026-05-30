package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

/** Syncs Citadel entity data from server to client (vine lasso, etc.). */
public class MessageSyncEntityData {

    public int entityId;
    public CompoundTag data;

    public MessageSyncEntityData(int entityId, CompoundTag data) {
        this.entityId = entityId;
        this.data = data;
    }

    public MessageSyncEntityData() {
    }

    public static MessageSyncEntityData read(FriendlyByteBuf buf) {
        return new MessageSyncEntityData(buf.readVarInt(), buf.readNbt());
    }

    public static void write(MessageSyncEntityData message, FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityId);
        buf.writeNbt(message.data);
    }

    public static class Handler {

        public static void handle(MessageSyncEntityData message, Supplier<AlexsMobs.PacketContext> context) {
            context.get().setPacketHandled(true);
            context.get().enqueueWork(() -> {
                if (!context.get().isClient()) {
                    return;
                }
                Player player = AlexsMobs.PROXY.getClientSidePlayer();
                if (player != null && player.level() != null) {
                    Entity entity = player.level().getEntity(message.entityId);
                    if (entity instanceof LivingEntity living) {
                        CitadelEntityData.setCitadelTag(living, message.data);
                    }
                }
            });
        }
    }
}
