package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Server -> Client packet to set pupfish chunk location.
 */
public record MessageSetPupfishChunkOnClient(int chunkX, int chunkZ) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageSetPupfishChunkOnClient> ID =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("alexsmobs", "set_pupfish_chunk"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSetPupfishChunkOnClient> CODEC = new StreamCodec<>() {
        @Override
        public MessageSetPupfishChunkOnClient decode(RegistryFriendlyByteBuf buf) {
            int chunkX = buf.readInt();
            int chunkZ = buf.readInt();
            return new MessageSetPupfishChunkOnClient(chunkX, chunkZ);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageSetPupfishChunkOnClient packet) {
            buf.writeInt(packet.chunkX);
            buf.writeInt(packet.chunkZ);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(MessageSetPupfishChunkOnClient payload, ClientPlayNetworking.Context context) {
                    AlexsMobs.PROXY.setPupfishChunkForItem(payload.chunkX, payload.chunkZ);

    }
}
