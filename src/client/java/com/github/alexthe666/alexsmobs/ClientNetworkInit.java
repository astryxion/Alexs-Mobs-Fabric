package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.misc.AMLootRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Client-only entrypoint: registers client packet handler and send-to-server callback
 * so the main source set does not depend on client networking API.
 */
public class ClientNetworkInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            AMLootRegistry.init();
        });
        AlexsMobs.setProxy(new ClientProxy());
        AlexsMobs.clientSendToServer = msg -> {
            ClientPlayNetworking.send(AlexsMobs.PACKET_CHANNEL, AlexsMobs.writeMessageToBuf(msg));
        };
        ClientPlayNetworking.registerGlobalReceiver(AlexsMobs.PACKET_CHANNEL, (client, handler, buf, responseSender) -> {
            FriendlyByteBuf packet = new FriendlyByteBuf(buf.copy());
            client.execute(() -> AlexsMobs.handleClientPacket(packet));
        });
        AlexsMobs.PROXY.clientInit();
    }
}
