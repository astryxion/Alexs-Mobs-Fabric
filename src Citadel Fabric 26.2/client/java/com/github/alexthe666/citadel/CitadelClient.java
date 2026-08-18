package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.client.ClientEvents;
import com.github.alexthe666.citadel.server.message.AnimationMessage;
import com.github.alexthe666.citadel.server.message.DanceJukeboxMessage;
import com.github.alexthe666.citadel.server.message.SyncClientTickRateMessage;
import com.github.alexthe666.citadel.server.message.SyncPathReachedMessage;
import com.github.alexthe666.citadel.server.message.SyncePathMessage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class CitadelClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientProxy proxy = new ClientProxy();
        Citadel.setClientProxy(proxy);
        proxy.onClientInit();
        ClientEvents.registerRenderers();
        ClientEvents.registerPipelines();
        ClientEvents.registerItemModels();

        ClientPlayNetworking.registerGlobalReceiver(AnimationMessage.TYPE, (message, context) ->
                AnimationMessage.handleClient(message));
        ClientPlayNetworking.registerGlobalReceiver(DanceJukeboxMessage.TYPE, (message, context) ->
                DanceJukeboxMessage.handleClient(message));
        ClientPlayNetworking.registerGlobalReceiver(SyncePathMessage.TYPE, (message, context) ->
                SyncePathMessage.handleClient(message));
        ClientPlayNetworking.registerGlobalReceiver(SyncPathReachedMessage.TYPE, (message, context) ->
                SyncPathReachedMessage.handleClient(message));
        ClientPlayNetworking.registerGlobalReceiver(SyncClientTickRateMessage.TYPE, (message, context) ->
                SyncClientTickRateMessage.handleClient(message));
    }
}
