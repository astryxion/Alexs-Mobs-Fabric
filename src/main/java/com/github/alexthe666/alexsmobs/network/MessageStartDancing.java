package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.IDancingMob;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/**
 * Dancing state packet. Clients send it when a frog/wolf hears a jukebox; the server then syncs other clients.
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

    public static void handleServer(MessageStartDancing payload, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();
        if (player == null || player.level() == null) {
            return;
        }
        Entity entity = player.level().getEntity(payload.entityID());
        if (entity instanceof IDancingMob mob) {
            mob.setDancing(payload.dance());
            mob.setJukeboxPos(payload.dance() ? payload.jukeBox() : null);
            AlexsMobs.sendMSGToAll(payload);
        }
    }
}
