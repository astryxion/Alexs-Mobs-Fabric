package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/**
 * Bidirectional packet to dismount a crow from a player's shoulder.
 */
public record MessageCrowDismount(int rider, int mount) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageCrowDismount> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("alexsmobs", "crow_dismount"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageCrowDismount> CODEC = new StreamCodec<>() {
        @Override
        public MessageCrowDismount decode(RegistryFriendlyByteBuf buf) {
            int rider = buf.readInt();
            int mount = buf.readInt();
            return new MessageCrowDismount(rider, mount);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageCrowDismount packet) {
            buf.writeInt(packet.rider);
            buf.writeInt(packet.mount);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleServer(MessageCrowDismount payload, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();
        if (player == null || player.level() == null || player.getId() != payload.mount()) {
            return;
        }
        Entity entity = player.level().getEntity(payload.rider());
        if (entity instanceof EntityCrow crow && crow.getVehicle() == player) {
            crow.tryDismountFromShoulder(player);
        }
    }
}