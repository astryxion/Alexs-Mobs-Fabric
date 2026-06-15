package com.github.alexthe666.alexsmobs.client.network;

import com.github.alexthe666.alexsmobs.network.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Client-side networking registration. Only loaded on the client.
 */
@Environment(EnvType.CLIENT)
public final class AMClientNetworking {

    private AMClientNetworking() {}

    public static void registerClientReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(MessageSyncEntityPos.ID, AMClientPacketHandlers::handleSyncEntityPos);
        ClientPlayNetworking.registerGlobalReceiver(MessageStartDancing.ID, AMClientPacketHandlers::handleStartDancing);
        ClientPlayNetworking.registerGlobalReceiver(MessageTarantulaHawkSting.ID, AMClientPacketHandlers::handleTarantulaHawkSting);
        ClientPlayNetworking.registerGlobalReceiver(MessageMosquitoMountPlayer.ID, AMClientPacketHandlers::handleMosquitoMountPlayer);
        ClientPlayNetworking.registerGlobalReceiver(MessageCrowMountPlayer.ID, AMClientPacketHandlers::handleCrowMountPlayer);
        ClientPlayNetworking.registerGlobalReceiver(MessageCrowDismount.ID, AMClientPacketHandlers::handleCrowDismount);
        ClientPlayNetworking.registerGlobalReceiver(MessageMungusBiomeChange.ID, AMClientPacketHandlers::handleMungusBiomeChange);
        ClientPlayNetworking.registerGlobalReceiver(MessageUpdateCapsid.ID, AMClientPacketHandlers::handleUpdateCapsid);
        ClientPlayNetworking.registerGlobalReceiver(MessageKangarooInventorySync.ID, AMClientPacketHandlers::handleKangarooInventorySync);
        ClientPlayNetworking.registerGlobalReceiver(MessageKangarooEat.ID, AMClientPacketHandlers::handleKangarooEat);
        ClientPlayNetworking.registerGlobalReceiver(MessageSendVisualFlagFromServer.ID, AMClientPacketHandlers::handleSendVisualFlagFromServer);
        ClientPlayNetworking.registerGlobalReceiver(MessageSetPupfishChunkOnClient.ID, AMClientPacketHandlers::handleSetPupfishChunkOnClient);
        ClientPlayNetworking.registerGlobalReceiver(MessageUpdateTransmutablesToDisplay.ID, AMClientPacketHandlers::handleUpdateTransmutablesToDisplay);
        ClientPlayNetworking.registerGlobalReceiver(MessageSyncEntityData.ID, AMClientPacketHandlers::handleSyncEntityData);
        ClientPlayNetworking.registerGlobalReceiver(MessageMosquitoDismount.ID, AMClientPacketHandlers::handleMosquitoDismount);
        ClientPlayNetworking.registerGlobalReceiver(MessageHurtMultipart.ID, AMClientPacketHandlers::handleHurtMultipart);
        ClientPlayNetworking.registerGlobalReceiver(MessageInteractMultipart.ID, AMClientPacketHandlers::handleInteractMultipart);
    }
}