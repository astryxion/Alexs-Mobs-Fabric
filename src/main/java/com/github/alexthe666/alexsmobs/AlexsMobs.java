package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.config.BiomeConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.AMDataComponents;
import com.github.alexthe666.alexsmobs.misc.AMCreativeTabRegistry;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.network.AMNetworking;
import com.github.alexthe666.alexsmobs.event.ServerEvents;
import com.github.alexthe666.alexsmobs.world.AMFeatureRegistry;
import com.github.alexthe666.alexsmobs.world.AMWorldRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.Date;

public class AlexsMobs implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "alexsmobs";
    public static final CommonProxy PROXY = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? new ClientProxy() : new CommonProxy();
    private static boolean isAprilFools = false;
    private static boolean isHalloween = false;
    private static MinecraftServer currentServer;

    @Override
    public void onInitialize() {
        AMNetworking.registerPayloadTypes();
        AMNetworking.registerServerReceivers();
        AMConfig.bake();
        BiomeConfig.init();
        AMDataComponents.init();
        AMAdvancementTriggerRegistry.init();
        AMPointOfInterestRegistry.init();
        AMSoundRegistry.init();
        AMFeatureRegistry.init();

        AMEntityRegistry.registerSpawnPlacements();
        AMEntityRegistry.registerDefaultAttributes();

        AMItemRegistry.init();
        AMItemRegistry.initDispenser();
        AMEffectRegistry.init();
        AMCreativeTabRegistry.init();
        PROXY.init();
        PROXY.initPathfinding();
        AMWorldRegistry.init();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> currentServer = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            if (currentServer == server) {
                currentServer = null;
            }
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                ServerEvents.onPlayerLoggedIn(handler.getPlayer()));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        isAprilFools = calendar.get(Calendar.MONTH) + 1 == 4 && calendar.get(Calendar.DATE) == 1;
        isHalloween = calendar.get(Calendar.MONTH) + 1 == 10 && calendar.get(Calendar.DATE) >= 29 && calendar.get(Calendar.DATE) <= 31;
    }

    public static boolean isAprilFools() {
        return isAprilFools || AMConfig.superSecretSettings;
    }

    public static boolean isHalloween() {
        return isHalloween || AMConfig.superSecretSettings;
    }

    public static void sendMSGToServer(CustomPacketPayload message) {
        PROXY.sendToServer(message);
    }

    public static void sendMSGToAll(CustomPacketPayload message) {
        if (currentServer != null) {
            for (ServerPlayer player : currentServer.getPlayerList().getPlayers()) {
                ServerPlayNetworking.send(player, message);
            }
        }
    }

    public static void sendNonLocal(CustomPacketPayload message, ServerPlayer player) {
        if (player != null) {
            ServerPlayNetworking.send(player, message);
        }
    }
}
