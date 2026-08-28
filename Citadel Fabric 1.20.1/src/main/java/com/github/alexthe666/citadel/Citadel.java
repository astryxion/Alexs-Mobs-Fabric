package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.compat.ModCompatBridge;
import com.github.alexthe666.citadel.config.ConfigHolder;
import com.github.alexthe666.citadel.config.ServerConfig;
import com.github.alexthe666.citadel.item.ItemCitadelBook;
import com.github.alexthe666.citadel.item.ItemCitadelDebug;
import com.github.alexthe666.citadel.item.ItemCustomRender;
import com.github.alexthe666.citadel.server.CitadelEvents;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlock;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlockEntity;
import com.github.alexthe666.citadel.server.block.LecternBooks;
import com.github.alexthe666.citadel.server.generation.CitadelSurfaceRuleWrapper;
import com.github.alexthe666.citadel.server.generation.SpawnProbabilityModifier;
import com.github.alexthe666.citadel.server.generation.VillageHouseManager;
import com.github.alexthe666.citadel.server.message.*;
import com.github.alexthe666.citadel.web.WebHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Citadel implements ModInitializer {
    public static final String MOD_ID = "citadel";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    public static final ResourceLocation PACKET_CHANNEL = new ResourceLocation(MOD_ID, "main_channel");

    public static ServerProxy PROXY = createProxy();
    public static List<String> PATREONS = new ArrayList<>();

    private static ServerProxy createProxy() {
        if (FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
            try {
                return (ServerProxy) Class.forName("com.github.alexthe666.citadel.ClientProxy").getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to create Citadel client proxy", e);
            }
        }
        return new ServerProxy();
    }

    public static final Item DEBUG_ITEM = registerItem("debug", new ItemCitadelDebug(new Item.Properties()));
    public static final Item CITADEL_BOOK = registerItem("citadel_book", new ItemCitadelBook(new Item.Properties().stacksTo(1)));
    public static final Item EFFECT_ITEM = registerItem("effect_item", new ItemCustomRender(new Item.Properties().stacksTo(1)));
    public static final Item FANCY_ITEM = registerItem("fancy_item", new ItemCustomRender(new Item.Properties().stacksTo(1)));
    public static final Item ICON_ITEM = registerItem("icon_item", new ItemCustomRender(new Item.Properties().stacksTo(1)));

    public static final Block LECTERN = registerBlock("lectern", new CitadelLecternBlock(BlockBehaviour.Properties.copy(Blocks.LECTERN)));

    public static final BlockEntityType<CitadelLecternBlockEntity> LECTERN_BE = registerBlockEntityType("lectern", BlockEntityType.Builder.of(CitadelLecternBlockEntity::new, LECTERN).build(null));

    private static Item registerItem(String name, Item item) {
        return net.minecraft.core.Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, name), item);
    }

    private static Block registerBlock(String name, Block block) {
        return net.minecraft.core.Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(MOD_ID, name), block);
    }

    private static BlockEntityType<CitadelLecternBlockEntity> registerBlockEntityType(String name, BlockEntityType<CitadelLecternBlockEntity> type) {
        return net.minecraft.core.Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(MOD_ID, name), type);
    }

    @Override
    public void onInitialize() {
        ConfigHolder.loadConfig();
        rebakeConfig();
        CitadelNetworking.register();
        PROXY.onPreInit();
        LecternBooks.init();
        loadPatreons();
        if (FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
            PROXY.onClientInit();
        }
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            RegistryAccess registryAccess = server.registryAccess();
            VillageHouseManager.addAllHouses(registryAccess);
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> ServerProxy.setMinecraftServer(null));
        CitadelEvents.register();
        SpawnProbabilityModifier.register();
        ModCompatBridge.afterAllModsLoaded();
    }

    private static void loadPatreons() {
        BufferedReader urlContents = WebHelper.getURLContents("https://raw.githubusercontent.com/Alex-the-666/Citadel/master/src/main/resources/assets/citadel/patreon.txt", "assets/citadel/patreon.txt");
        if (urlContents != null) {
            try {
                String line;
                while ((line = urlContents.readLine()) != null) {
                    PATREONS.add(line);
                }
            } catch (IOException e) {
                LOGGER.warn("Failed to load patreon contributor perks");
            }
        } else {
            LOGGER.warn("Failed to load patreon contributor perks");
        }
    }

    public static void rebakeConfig() {
        ServerConfig.skipWarnings = ConfigHolder.SERVER.skipDatapackWarnings;
        ServerConfig.citadelEntityTrack = ConfigHolder.SERVER.citadelEntityTracker;
        ServerConfig.chunkGenSpawnModifierVal = ConfigHolder.SERVER.chunkGenSpawnModifier;
        ServerConfig.aprilFools = ConfigHolder.SERVER.aprilFoolsContent;
    }

    public static <MSG> void sendMSGToServer(MSG message) {
        CitadelNetworking.sendToServer(message);
    }

    public static <MSG> void sendMSGToAll(MSG message) {
        MinecraftServer server = PROXY.getMinecraftServer();
        if (server != null) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                sendNonLocal(message, player);
            }
        }
    }

    public static <MSG> void sendNonLocal(MSG msg, ServerPlayer player) {
        CitadelNetworking.sendToClient(msg, player);
    }
}
