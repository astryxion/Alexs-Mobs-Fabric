package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.server.message.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class CitadelNetworking {
    static final int PROPERTIES = 0;
    static final int ANIMATION = 1;
    static final int SYNC_CLIENT_TICK_RATE = 2;
    static final int DANCE_JUKEBOX = 3;
    static final int SYNC_PATH = 4;
    static final int SYNC_PATH_REACHED = 5;

    public static final CustomPacketPayload.Type<CitadelPayload> PAYLOAD_TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Citadel.MOD_ID, "main_channel"));

    public static void register() {
        PayloadTypeRegistry.playC2S().register(PAYLOAD_TYPE, CitadelPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(PAYLOAD_TYPE, CitadelPayload.STREAM_CODEC);
        registerServerReceiver();
        if (FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
            try {
                Class.forName("com.github.alexthe666.citadel.CitadelClientNetworking").getMethod("register").invoke(null);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to register Citadel client networking", e);
            }
        }
    }

    private static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(PAYLOAD_TYPE, (payload, context) -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.wrappedBuffer(payload.data()));
            int id = buf.readVarInt();
            context.server().execute(() -> {
                switch (id) {
                    case PROPERTIES -> {
                        PropertiesMessage msg = PropertiesMessage.read(buf);
                        PropertiesMessage.Handler.handle(msg, PacketContext.wrap(new FabricPacketContext(context.player(), PacketContext.PacketDirection.PLAY_TO_SERVER)));
                    }
                    case ANIMATION -> {
                        AnimationMessage msg = AnimationMessage.read(buf);
                        AnimationMessage.Handler.handle(msg, PacketContext.wrap(new FabricPacketContext(context.player(), PacketContext.PacketDirection.PLAY_TO_SERVER)));
                    }
                    case DANCE_JUKEBOX -> {
                        DanceJukeboxMessage msg = DanceJukeboxMessage.read(buf);
                        DanceJukeboxMessage.Handler.handle(msg, PacketContext.wrap(new FabricPacketContext(context.player(), PacketContext.PacketDirection.PLAY_TO_SERVER)));
                    }
                    default -> {}
                }
            });
        });
    }

    public static void sendToServer(Object message) {
        if (FabricLoader.getInstance().getEnvironmentType() != net.fabricmc.api.EnvType.CLIENT) return;
        try {
            Class.forName("com.github.alexthe666.citadel.CitadelClientNetworking")
                    .getMethod("sendToServer", byte[].class)
                    .invoke(null, writeMessageToBytes(message));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to send Citadel client packet", e);
        }
    }

    public static void sendToClient(Object message, ServerPlayer player) {
        ServerPlayNetworking.send(player, new CitadelPayload(writeMessageToBytes(message)));
    }

    private static byte[] writeMessageToBytes(Object message) {
        FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        if (message instanceof PropertiesMessage m) {
            buf.writeVarInt(PROPERTIES);
            PropertiesMessage.write(m, buf);
        } else if (message instanceof AnimationMessage m) {
            buf.writeVarInt(ANIMATION);
            AnimationMessage.write(m, buf);
        } else if (message instanceof SyncClientTickRateMessage m) {
            buf.writeVarInt(SYNC_CLIENT_TICK_RATE);
            SyncClientTickRateMessage.write(m, buf);
        } else if (message instanceof DanceJukeboxMessage m) {
            buf.writeVarInt(DANCE_JUKEBOX);
            DanceJukeboxMessage.write(m, buf);
        } else if (message instanceof MessageSyncPath m) {
            buf.writeVarInt(SYNC_PATH);
            m.write(buf);
        } else if (message instanceof MessageSyncPathReached m) {
            buf.writeVarInt(SYNC_PATH_REACHED);
            m.write(buf);
        }
        byte[] bytes = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), bytes);
        return bytes;
    }

    public record CitadelPayload(byte[] data) implements CustomPacketPayload {
        public static final StreamCodec<FriendlyByteBuf, CitadelPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeByteArray(payload.data()),
            buf -> new CitadelPayload(buf.readByteArray(32767))
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return PAYLOAD_TYPE;
        }
    }
}
