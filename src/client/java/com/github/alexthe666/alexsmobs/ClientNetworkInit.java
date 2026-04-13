package com.github.alexthe666.alexsmobs;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Client-only entrypoint: registers client packet handler and send-to-server callback
 * so the main source set does not depend on client networking API.
 */
public class ClientNetworkInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AlexsMobs.setProxy(new ClientProxy());
        AlexsMobs.clientSendToServer = msg ->
                ClientPlayNetworking.send(AlexsMobs.PACKET_CHANNEL, AlexsMobs.writeMessageToBuf(msg));
        ClientPlayNetworking.registerGlobalReceiver(AlexsMobs.PACKET_CHANNEL, (client, handler, buf, responseSender) ->
                client.execute(() -> AlexsMobs.handleClientPacket(buf)));
        AlexsMobs.PROXY.clientInit();
    }
}
