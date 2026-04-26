package com.github.alexthe666.alexsmobs.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * Fabric play networking registration for Alex's Mobs custom payloads.
 */
public final class AMNetworking {

    private AMNetworking() {
    }

    public static void registerPayloadTypes() {
        PayloadTypeRegistry.clientboundPlay().register(MessageSyncEntityPos.ID, MessageSyncEntityPos.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(MessageStartDancing.ID, MessageStartDancing.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(MessageTarantulaHawkSting.ID, MessageTarantulaHawkSting.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(MessageMosquitoMountPlayer.ID, MessageMosquitoMountPlayer.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(MessageCrowMountPlayer.ID, MessageCrowMountPlayer.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(MessageCrowDismount.ID, MessageCrowDismount.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(MessageMungusBiomeChange.ID, MessageMungusBiomeChange.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(MessageUpdateCapsid.ID, MessageUpdateCapsid.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(MessageKangarooInventorySync.ID, MessageKangarooInventorySync.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(MessageKangarooEat.ID, MessageKangarooEat.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(MessageSendVisualFlagFromServer.ID, MessageSendVisualFlagFromServer.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(MessageSetPupfishChunkOnClient.ID, MessageSetPupfishChunkOnClient.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(MessageUpdateTransmutablesToDisplay.ID, MessageUpdateTransmutablesToDisplay.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(MessageSyncEntityData.ID, MessageSyncEntityData.CODEC);

        PayloadTypeRegistry.serverboundPlay().register(MessageSwingArm.ID, MessageSwingArm.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(MessageUpdateEagleControls.ID, MessageUpdateEagleControls.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(MessageTransmuteFromMenu.ID, MessageTransmuteFromMenu.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(MessageMosquitoDismount.ID, MessageMosquitoDismount.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(MessageHurtMultipart.ID, MessageHurtMultipart.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(MessageInteractMultipart.ID, MessageInteractMultipart.CODEC);

        PayloadTypeRegistry.clientboundPlay().register(MessageMosquitoDismount.ID, MessageMosquitoDismount.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(MessageHurtMultipart.ID, MessageHurtMultipart.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(MessageInteractMultipart.ID, MessageInteractMultipart.CODEC);
    }

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(MessageSwingArm.ID, MessageSwingArm::handleServer);
        ServerPlayNetworking.registerGlobalReceiver(MessageUpdateEagleControls.ID, MessageUpdateEagleControls::handleServer);
        ServerPlayNetworking.registerGlobalReceiver(MessageTransmuteFromMenu.ID, MessageTransmuteFromMenu::handleServer);
        ServerPlayNetworking.registerGlobalReceiver(MessageMosquitoDismount.ID, MessageMosquitoDismount::handleServer);
        ServerPlayNetworking.registerGlobalReceiver(MessageHurtMultipart.ID, MessageHurtMultipart::handleServer);
        ServerPlayNetworking.registerGlobalReceiver(MessageInteractMultipart.ID, MessageInteractMultipart::handleServer);
    }

    public static void registerClientReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(MessageSyncEntityPos.ID, MessageSyncEntityPos::handleClient);
        ClientPlayNetworking.registerGlobalReceiver(MessageStartDancing.ID, MessageStartDancing::handleClient);
        ClientPlayNetworking.registerGlobalReceiver(MessageTarantulaHawkSting.ID, MessageTarantulaHawkSting::handleClient);
        ClientPlayNetworking.registerGlobalReceiver(MessageMosquitoMountPlayer.ID, MessageMosquitoMountPlayer::handleClient);
        ClientPlayNetworking.registerGlobalReceiver(MessageCrowMountPlayer.ID, MessageCrowMountPlayer::handleClient);
        ClientPlayNetworking.registerGlobalReceiver(MessageCrowDismount.ID, MessageCrowDismount::handleClient);
        ClientPlayNetworking.registerGlobalReceiver(MessageMungusBiomeChange.ID, MessageMungusBiomeChange::handleClient);
        ClientPlayNetworking.registerGlobalReceiver(MessageUpdateCapsid.ID, MessageUpdateCapsid::handleClient);
        ClientPlayNetworking.registerGlobalReceiver(MessageKangarooInventorySync.ID, MessageKangarooInventorySync::handleClient);
        ClientPlayNetworking.registerGlobalReceiver(MessageKangarooEat.ID, MessageKangarooEat::handleClient);
        ClientPlayNetworking.registerGlobalReceiver(MessageSendVisualFlagFromServer.ID, MessageSendVisualFlagFromServer::handleClient);
        ClientPlayNetworking.registerGlobalReceiver(MessageSetPupfishChunkOnClient.ID, MessageSetPupfishChunkOnClient::handleClient);
        ClientPlayNetworking.registerGlobalReceiver(MessageUpdateTransmutablesToDisplay.ID, MessageUpdateTransmutablesToDisplay::handleClient);
        ClientPlayNetworking.registerGlobalReceiver(MessageSyncEntityData.ID, MessageSyncEntityData::handleClient);
        ClientPlayNetworking.registerGlobalReceiver(MessageMosquitoDismount.ID, MessageMosquitoDismount::handle);
        ClientPlayNetworking.registerGlobalReceiver(MessageHurtMultipart.ID, MessageHurtMultipart::handle);
        ClientPlayNetworking.registerGlobalReceiver(MessageInteractMultipart.ID, MessageInteractMultipart::handle);
    }
}
