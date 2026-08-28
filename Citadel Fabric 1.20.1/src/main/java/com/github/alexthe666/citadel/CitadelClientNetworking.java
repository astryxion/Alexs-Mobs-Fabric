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
        ClientPlayNetworking.registerGlobalReceiver(CitadelNetworking.CHANNEL, (client, handler, buf, responseSender) -> {
            FriendlyByteBuf packet = new FriendlyByteBuf(buf.copy());
            int id = packet.readVarInt();
            client.execute(() -> {
                switch (id) {
                    case CitadelNetworking.PROPERTIES -> {
                        PropertiesMessage msg = PropertiesMessage.read(packet);
                        PropertiesMessage.Handler.handle(msg, PacketContext.wrap(new FabricPacketContext(null, PacketContext.PacketDirection.PLAY_TO_CLIENT)));
                    }
                    case CitadelNetworking.ANIMATION -> {
                        AnimationMessage msg = AnimationMessage.read(packet);
                        AnimationMessage.Handler.handle(msg, PacketContext.wrap(new FabricPacketContext(null, PacketContext.PacketDirection.PLAY_TO_CLIENT)));
                    }
                    case CitadelNetworking.SYNC_CLIENT_TICK_RATE -> {
                        SyncClientTickRateMessage msg = SyncClientTickRateMessage.read(packet);
                        SyncClientTickRateMessage.Handler.handle(msg, PacketContext.wrap(new FabricPacketContext(null, PacketContext.PacketDirection.PLAY_TO_CLIENT)));
                    }
                    case CitadelNetworking.DANCE_JUKEBOX -> {
                        DanceJukeboxMessage msg = DanceJukeboxMessage.read(packet);
                        DanceJukeboxMessage.Handler.handle(msg, PacketContext.wrap(new FabricPacketContext(null, PacketContext.PacketDirection.PLAY_TO_CLIENT)));
                    }
                    case CitadelNetworking.SYNC_PATH -> {
                        MessageSyncPath msg = MessageSyncPath.read(packet);
                        MessageSyncPath.Handler.handle(msg, PacketContext.wrap(new FabricPacketContext(null, PacketContext.PacketDirection.PLAY_TO_CLIENT)));
                    }
                    case CitadelNetworking.SYNC_PATH_REACHED -> {
                        MessageSyncPathReached msg = MessageSyncPathReached.read(packet);
                        MessageSyncPathReached.Handler.handle(msg, PacketContext.wrap(new FabricPacketContext(null, PacketContext.PacketDirection.PLAY_TO_CLIENT)));
                    }
                    default -> {}
                }
            });
        });
    }

    public static void sendToServer(FriendlyByteBuf buf) {
        ClientPlayNetworking.send(CitadelNetworking.CHANNEL, buf);
    }
}
