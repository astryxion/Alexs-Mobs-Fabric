package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.client.render.RenderMultipartCollisionPart;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.network.AMNetworking;
import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.message.AnimationMessage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class AlexsMobsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AlexsMobs.PROXY.clientInit();
        AMNetworking.registerClientReceivers();
        // Citadel's 26.1 Fabric port never wires its own client networking: setClientProxy() is never called
        // (so Citadel.PROXY stays the no-op ServerProxy) and no client receiver is registered for its S2C
        // AnimationMessage. As a result server->client animation packets are decoded but dropped, and Citadel
        // mobs play no scripted animations on a client (e.g. Elephant trumpet/charge/stomp on a server/LAN).
        // Wire it from here: install the client proxy and route the packet to it on the render thread.
        Citadel.setClientProxy(new com.github.alexthe666.citadel.ClientProxy());
        ClientPlayNetworking.registerGlobalReceiver(AnimationMessage.TYPE, (message, context) ->
                context.client().execute(() -> AnimationMessage.handleClient(message)));
        EntityRendererRegistry.register(AMEntityRegistry.CACHALOT_PART, RenderMultipartCollisionPart::new);
        EntityRendererRegistry.register(AMEntityRegistry.GIANT_SQUID_PART, RenderMultipartCollisionPart::new);
        EntityRendererRegistry.register(AMEntityRegistry.LAVIATHAN_PART, RenderMultipartCollisionPart::new);
    }
}
