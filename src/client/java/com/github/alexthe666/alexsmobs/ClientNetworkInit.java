package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.message.AlexsMobsPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

/**
 * Client-only entrypoint: registers client packet handler and send-to-server callback
 * so the main source set does not depend on client networking API.
 */
public class ClientNetworkInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AlexsMobs.setProxy(new ClientProxy());
        AlexsMobs.clientSendToServer = msg ->
                ClientPlayNetworking.send(new AlexsMobsPacket(AlexsMobs.serializeMessage(msg)));
        ClientPlayNetworking.registerGlobalReceiver(AlexsMobsPacket.TYPE, (packet, player, responseSender) -> {
            byte[] data = packet.data();
            Minecraft.getInstance().execute(() -> AlexsMobs.handleClientPacket(data));
        });
        AlexsMobs.PROXY.clientInit();
    }
}
