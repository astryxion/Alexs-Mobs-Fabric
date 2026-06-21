package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.config.ConfigHolder;
import com.github.alexthe666.citadel.config.ServerConfig;
import com.github.alexthe666.citadel.item.CitadelDataComponents;
import com.github.alexthe666.citadel.item.ItemCitadelBook;
import com.github.alexthe666.citadel.item.ItemCitadelDebug;
import com.github.alexthe666.citadel.item.ItemCustomRender;
import com.github.alexthe666.citadel.server.CitadelEvents;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlock;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlockEntity;
import com.github.alexthe666.citadel.server.block.LecternBooks;
import com.github.alexthe666.citadel.server.generation.SpawnProbabilityModifier;
import com.github.alexthe666.citadel.server.generation.SurfaceRuleInitializer;
import com.github.alexthe666.citadel.server.generation.VillageHouseManager;
import com.github.alexthe666.citadel.server.message.*;
import com.github.alexthe666.citadel.web.WebHelper;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Citadel implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("citadel");
    private static final String PROTOCOL_VERSION = Integer.toString(1);

    public static ServerProxy PROXY = new ServerProxy();
    public static volatile net.minecraft.server.MinecraftServer CURRENT_SERVER;
    public static List<String> PATREONS = new ArrayList<>();
    public static ItemCitadelDebug DEBUG_ITEM;
    public static ItemCitadelBook CITADEL_BOOK;
    public static ItemCustomRender EFFECT_ITEM;
    public static ItemCustomRender FANCY_ITEM;
    public static ItemCustomRender ICON_ITEM;
    public static CitadelLecternBlock LECTERN;
    public static BlockEntityType<CitadelLecternBlockEntity> LECTERN_BE;

    @Override
    public void onInitialize() {
        ConfigHolder.SERVER.bake();
        registerContent();
        registerNetwork();
        registerEvents();
        commonSetup();
    }

    public static void setClientProxy(ServerProxy clientProxy) {
        PROXY = clientProxy;
    }

    private static Item.Properties citadelItemProperties(String path) {
        Identifier id = Identifier.fromNamespaceAndPath("citadel", path);
        return new Item.Properties().setId(net.minecraft.resources.ResourceKey.create(Registries.ITEM, id));
    }

    private static BlockBehaviour.Properties citadelBlockProperties(String path) {
        Identifier id = Identifier.fromNamespaceAndPath("citadel", path);
        return BlockBehaviour.Properties.ofFullCopy(Blocks.LECTERN)
                .setId(net.minecraft.resources.ResourceKey.create(Registries.BLOCK, id));
    }

    private static void registerContent() {
        DEBUG_ITEM = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("citadel", "debug"), new ItemCitadelDebug(citadelItemProperties("debug")));
        CITADEL_BOOK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("citadel", "citadel_book"), new ItemCitadelBook(citadelItemProperties("citadel_book").stacksTo(1)));
        EFFECT_ITEM = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("citadel", "effect_item"), new ItemCustomRender(citadelItemProperties("effect_item").stacksTo(1)));
        FANCY_ITEM = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("citadel", "fancy_item"), new ItemCustomRender(citadelItemProperties("fancy_item").stacksTo(1)));
        ICON_ITEM = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("citadel", "icon_item"), new ItemCustomRender(citadelItemProperties("icon_item").stacksTo(1)));

        LECTERN = Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath("citadel", "lectern"), new CitadelLecternBlock(citadelBlockProperties("lectern")));
        LECTERN_BE = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath("citadel", "lectern"), createLecternBlockEntityType());

        CitadelDataComponents.register();
    }

    private static void registerNetwork() {
        PayloadTypeRegistry.clientboundPlay().register(AnimationMessage.TYPE, AnimationMessage.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(DanceJukeboxMessage.TYPE, DanceJukeboxMessage.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncePathMessage.TYPE, SyncePathMessage.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncPathReachedMessage.TYPE, SyncPathReachedMessage.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncClientTickRateMessage.TYPE, SyncClientTickRateMessage.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(PropertiesMessage.TYPE, PropertiesMessage.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(DanceJukeboxMessage.TYPE, DanceJukeboxMessage.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(PropertiesMessage.TYPE, (message, context) -> PropertiesMessage.handleServer(message, context.player()));
        ServerPlayNetworking.registerGlobalReceiver(DanceJukeboxMessage.TYPE, (message, context) -> DanceJukeboxMessage.handleServer(message, context.player()));
    }

    private static void registerEvents() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();
            if (level.getBlockState(pos).is(Blocks.LECTERN)) {
                ItemStack stack = player.getItemInHand(hand);
                if (LecternBooks.isLecternBook(stack)) {
                    player.getCooldowns().addCooldown(stack, 1);
                    BlockState oldLectern = level.getBlockState(pos);
                    if (level.getBlockEntity(pos) instanceof LecternBlockEntity oldBe && !oldBe.hasBook()) {
                        BlockState newLectern = LECTERN.defaultBlockState().setValue(CitadelLecternBlock.FACING, oldLectern.getValue(LecternBlock.FACING)).setValue(CitadelLecternBlock.POWERED, oldLectern.getValue(LecternBlock.POWERED)).setValue(CitadelLecternBlock.HAS_BOOK, true);
                        level.setBlockAndUpdate(pos, newLectern);
                        CitadelLecternBlockEntity newBe = new CitadelLecternBlockEntity(pos, newLectern);
                        ItemStack bookCopy = stack.copy();
                        bookCopy.setCount(1);
                        newBe.setBook(bookCopy);
                        if (!player.isCreative()) {
                            stack.shrink(1);
                        }
                        level.setBlockEntity(newBe);
                        player.swing(hand, true);
                        level.playSound(null, pos, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.PASS;
        });

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            CURRENT_SERVER = server;
            RegistryAccess registryAccess = server.registryAccess();
            VillageHouseManager.addAllHouses(registryAccess);
            SurfaceRuleInitializer.initializeOnServerStart(server);
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> CURRENT_SERVER = null);

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (server.isRunning()) {
                var tickRateTracker = com.github.alexthe666.citadel.server.world.CitadelServerData.get(server).getOrCreateTickRateTracker();
                if (server instanceof com.github.alexthe666.citadel.server.world.ModifiableTickRateServer modifiableServer) {
                    long l = tickRateTracker.getServerTickLengthMs();
                    if (l == com.github.alexthe666.citadel.server.tick.ServerTickRateTracker.MS_PER_TICK) {
                        modifiableServer.resetGlobalTickLengthMs();
                    } else {
                        modifiableServer.setGlobalTickLengthMs(tickRateTracker.getServerTickLengthMs());
                    }
                    if (!server.isShutdown()) {
                        tickRateTracker.masterTick();
                    }
                }
            }
        });
    }

    private static void commonSetup() {
        PROXY.onPreInit();
        LecternBooks.init();
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

    @SuppressWarnings("unchecked")
    private static BlockEntityType<CitadelLecternBlockEntity> createLecternBlockEntityType() {
        try {
            Class<?> supplierClass = Class.forName("net.minecraft.world.level.block.entity.BlockEntityType$BlockEntitySupplier");
            Object supplier = Proxy.newProxyInstance(
                    Citadel.class.getClassLoader(),
                    new Class<?>[]{supplierClass},
                    (proxy, method, args) -> new CitadelLecternBlockEntity((BlockPos) args[0], (BlockState) args[1]));
            Constructor<?> constructor = BlockEntityType.class.getDeclaredConstructor(supplierClass, Set.class);
            constructor.setAccessible(true);
            return (BlockEntityType<CitadelLecternBlockEntity>) constructor.newInstance(supplier, Set.of(LECTERN));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create Citadel lectern block entity type", e);
        }
    }
}