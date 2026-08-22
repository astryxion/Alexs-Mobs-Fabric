package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.server.message.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Client-only networking. Loaded via reflection so dedicated servers never touch ClientPlayNetworking.
 */
public final class CitadelClientNetworking {
    private CitadelClientNetworking() {}

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(CitadelNetworking.PAYLOAD_TYPE, (payload, context) -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.wrappedBuffer(payload.data()));
            int id = buf.readVarInt();
            context.client().execute(() -> {
                switch (id) {
                    case CitadelNetworking.PROPERTIES -> {
                        PropertiesMessage msg = PropertiesMessage.read(buf);
                        PropertiesMessage.Handler.handle(msg, PacketContext.wrap(new FabricPacketContext(null, PacketContext.PacketDirection.PLAY_TO_CLIENT)));
                    }
                    case CitadelNetworking.ANIMATION -> {
                        AnimationMessage msg = AnimationMessage.read(buf);
                        AnimationMessage.Handler.handle(msg, PacketContext.wrap(new FabricPacketContext(null, PacketContext.PacketDirection.PLAY_TO_CLIENT)));
                    }
                    case CitadelNetworking.SYNC_CLIENT_TICK_RATE -> {
                        SyncClientTickRateMessage msg = SyncClientTickRateMessage.read(buf);
                        SyncClientTickRateMessage.Handler.handle(msg, PacketContext.wrap(new FabricPacketContext(null, PacketContext.PacketDirection.PLAY_TO_CLIENT)));
                    }
                    case CitadelNetworking.DANCE_JUKEBOX -> {
                        DanceJukeboxMessage msg = DanceJukeboxMessage.read(buf);
                        DanceJukeboxMessage.Handler.handle(msg, PacketContext.wrap(new FabricPacketContext(null, PacketContext.PacketDirection.PLAY_TO_CLIENT)));
                    }
                    case CitadelNetworking.SYNC_PATH -> {
                        MessageSyncPath msg = MessageSyncPath.read(buf);
                        MessageSyncPath.Handler.handle(msg, PacketContext.wrap(new FabricPacketContext(null, PacketContext.PacketDirection.PLAY_TO_CLIENT)));
                    }
                    case CitadelNetworking.SYNC_PATH_REACHED -> {
                        MessageSyncPathReached msg = MessageSyncPathReached.read(buf);
                        MessageSyncPathReached.Handler.handle(msg, PacketContext.wrap(new FabricPacketContext(null, PacketContext.PacketDirection.PLAY_TO_CLIENT)));
                    }
                    default -> {}
                }
            });
        });
    }

    public static void sendToServer(byte[] data) {
        ClientPlayNetworking.send(new CitadelNetworking.CitadelPayload(data));
    }
}
