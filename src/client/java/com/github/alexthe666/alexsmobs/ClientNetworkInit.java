package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.misc.AMBannerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMLootRegistry;
import com.github.alexthe666.alexsmobs.misc.AMPaintingRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

/**
 * Client-only entrypoint: registers client packet handler and send-to-server callback
 * so the main source set does not depend on client networking API.
 */
public class ClientNetworkInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            AMPaintingRegistry.init();
            AMBannerRegistry.init();
            AMLootRegistry.init();
        });
        AlexsMobs.setProxy(new ClientProxy());
        AlexsMobs.clientSendToServer = msg -> {
            var conn = Minecraft.getInstance().getConnection();
            var reg = conn != null ? conn.registryAccess() : net.minecraft.core.RegistryAccess.EMPTY;
            var buf = AlexsMobs.writeMessageToBuf(msg, reg);
            byte[] data = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), data);
            ClientPlayNetworking.send(new AlexsMobs.AlexsMobsPayload(data));
        };
        ClientPlayNetworking.registerGlobalReceiver(AlexsMobs.AlexsMobsPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                var buf = payload.createBuffer();
                var reg = context.player() != null ? context.player().connection.registryAccess() : net.minecraft.core.RegistryAccess.EMPTY;
                AlexsMobs.handleClientPacket(buf, reg);
            });
        });
        AlexsMobs.PROXY.clientInit();
    }
}
