package com.github.alexthe666.citadel.server.message;

import com.github.alexthe666.citadel.Citadel;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class AnimationMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AnimationMessage> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("citadel", "animation"));
    public static final StreamCodec<FriendlyByteBuf, AnimationMessage> CODEC = StreamCodec.ofMember(AnimationMessage::write, AnimationMessage::read);

    private int entityID;
    private int index;

    public AnimationMessage(int entityID, int index) {
        this.entityID = entityID;
        this.index = index;
    }

    public static AnimationMessage read(FriendlyByteBuf buf) {
        return new AnimationMessage(buf.readInt(), buf.readInt());
    }

    public static void write(AnimationMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.entityID);
        buf.writeInt(message.index);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(final AnimationMessage message) {
        Citadel.PROXY.handleAnimationPacket(message.entityID, message.index);
    }

    public static void sendToAllPlayers(final AnimationMessage message, Iterable<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            // During join/respawn transitions a player can exist server-side before play payload channels are fully ready.
            // Guarding with canSend avoids encoder disconnects like custom_payload (citadel:animation).
            if (ServerPlayNetworking.canSend(player, TYPE)) {
                ServerPlayNetworking.send(player, message);
            }
        }
    }
}
