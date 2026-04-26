package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Packet for syncing Citadel entity data from server to client.
 * Used by VineLassoUtil, TendonWhipUtil, and other features that need
 * to sync entity data across the network.
 */
public record MessageSyncEntityData(int entityId, CompoundTag data) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageSyncEntityData> ID = 
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "sync_entity_data"));

    public static final StreamCodec<FriendlyByteBuf, MessageSyncEntityData> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, MessageSyncEntityData::entityId,
        ByteBufCodecs.COMPOUND_TAG, MessageSyncEntityData::data,
        MessageSyncEntityData::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(final MessageSyncEntityData message, ClientPlayNetworking.Context context) {
                    if (Minecraft.getInstance().level != null) {
                Entity entity = Minecraft.getInstance().level.getEntity(message.entityId);
                if (entity instanceof LivingEntity living) {
                    CitadelEntityData.setCitadelTag(living, message.data);
                }
            }

    }
}
