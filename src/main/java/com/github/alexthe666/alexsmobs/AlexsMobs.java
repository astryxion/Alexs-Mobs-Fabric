package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.config.ConfigHolder;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.enchantment.AMEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.event.ServerEvents;
import com.github.alexthe666.alexsmobs.inventory.AMMenuRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.message.*;
import com.github.alexthe666.alexsmobs.misc.*;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.world.AMFeatureRegistry;
import com.github.alexthe666.alexsmobs.world.AMSpawnRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.core.LogEvent;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AlexsMobs implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(AlexsMobs.MODID);

    static {
        // Suppress "No data fixer registered for alexsmobs:…" (mod entities are not in vanilla schema)
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        Filter filter = new AbstractFilter() {
            @Override
            public Result filter(LogEvent event) {
                if (event != null && event.getMessage() != null) {
                    String msg = event.getMessage().getFormattedMessage();
                    if (msg != null && msg.startsWith("No data fixer registered for alexsmobs:")) {
                        return Result.DENY;
                    }
                }
                return Result.NEUTRAL;
            }
        };
        config.getRootLogger().addFilter(filter);
        ctx.updateLoggers();
    }
    public static final String MODID = "alexsmobs";
    public static final ResourceLocation PACKET_CHANNEL = new ResourceLocation(MODID, "main_channel");

    public static CommonProxy PROXY = new CommonProxy();

    /** Called from client entrypoint to set client proxy. */
    public static void setProxy(CommonProxy proxy) {
        PROXY = proxy;
    }

    /** Set from client-only code (ClientNetworkInit) so main source set does not depend on client networking API. */
    public static Consumer<Object> clientSendToServer = msg -> {};

    private static MinecraftServer server;
    private static boolean isAprilFools = false;
    private static boolean isHalloween = false;

    /** Fabric equivalent of Forge NetworkEvent.Context for packet handlers (no new file). */
    public static final class PacketContext {
        private final net.minecraft.world.entity.player.Player sender;
        private final boolean isClient;

        public PacketContext(net.minecraft.world.entity.player.Player sender, boolean isClient) {
            this.sender = sender;
            this.isClient = isClient;
        }

        public void setPacketHandled(boolean handled) {}
        public void enqueueWork(Runnable r) { r.run(); }
        public net.minecraft.world.entity.player.Player getSender() { return sender; }
        public boolean isClient() { return isClient; }
    }

    public static MinecraftServer getServer() { return server; }

    @Override
    public void onInitialize() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        isAprilFools = calendar.get(Calendar.MONTH) + 1 == 4 && calendar.get(Calendar.DATE) == 1;
        isHalloween = calendar.get(Calendar.MONTH) + 1 == 10 && calendar.get(Calendar.DATE) >= 29 && calendar.get(Calendar.DATE) <= 31;

        ConfigHolder.loadConfig();

        AMEntityRegistry.init();
        AMBlockRegistry.init();
        AMEffectRegistry.init();
        AMItemRegistry.init();
        AMTileEntityRegistry.init();
        AMPointOfInterestRegistry.init();
        AMFeatureRegistry.init();
        AMSoundRegistry.init();
        AMParticleRegistry.init();
        AMPaintingRegistry.init();
        AMEffectRegistry.registerBrewing();
        AMEnchantmentRegistry.init();
        AMMenuRegistry.init();
        AMRecipeRegistry.init();
        AMLootRegistry.init();
        AMBannerRegistry.init();
        AMCreativeTabRegistry.init();
        AMSpawnRegistry.register();

        registerNetworking();
        PROXY.init();
        AMItemRegistry.initDispenser();
        AMAdvancementTriggerRegistry.init();
        PROXY.initPathfinding();

        ServerLifecycleEvents.SERVER_STARTING.register(s -> server = s);
        ServerLifecycleEvents.SERVER_STOPPING.register(s -> server = null);
        ServerEvents.register();
    }

    public static boolean isAprilFools() {
        return isAprilFools || AMConfig.superSecretSettings;
    }

    public static boolean isHalloween() {
        return isHalloween || AMConfig.superSecretSettings;
    }

    /** Fabric 1.20.1: Entity.getEncodeId() is protected; use registry for 1:1 behavior. */
    public static String getEntityEncodeId(Entity entity) {
        return entity != null ? BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString() : null;
    }

    public static void sendMSGToServer(Object message) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return;
        clientSendToServer.accept(message);
    }

    public static void sendMSGToAll(Object message) {
        MinecraftServer s = server;
        if (s != null) {
            for (ServerPlayer player : s.getPlayerList().getPlayers()) {
                sendNonLocal(message, player);
            }
        }
    }

    public static void sendNonLocal(Object msg, ServerPlayer player) {
        ServerPlayNetworking.send(player, PACKET_CHANNEL, writeMessageToBuf(msg));
    }

    private static final int ID_MOSQUITO_MOUNT = 0, ID_MOSQUITO_DISMOUNT = 1, ID_HURT_MULTIPART = 2, ID_CROW_MOUNT = 3, ID_CROW_DISMOUNT = 4;
    private static final int ID_MUNGUS_BIOME = 5, ID_KANGAROO_SYNC = 6, ID_KANGAROO_EAT = 7, ID_UPDATE_CAPSID = 8, ID_SWING_ARM = 9;
    private static final int ID_EAGLE_CONTROLS = 10, ID_SYNC_ENTITY_POS = 11, ID_TARANTULA_STING = 12, ID_START_DANCING = 13, ID_INTERACT_MULTIPART = 14;
    private static final int ID_VISUAL_FLAG = 15, ID_PUPFISH_CHUNK = 16, ID_TRANSMUTABLES = 17, ID_TRANSMUTE_MENU = 18;

    private static void registerNetworking() {
        ServerPlayNetworking.registerGlobalReceiver(PACKET_CHANNEL, (s, player, handler, buf, responseSender) -> {
            int id = buf.readVarInt();
            s.execute(() -> {
                PacketContext ctx = new PacketContext(player, false);
                Supplier<PacketContext> sup = () -> ctx;
                switch (id) {
                    case ID_MOSQUITO_MOUNT -> MessageMosquitoMountPlayer.Handler.handle(MessageMosquitoMountPlayer.read(buf), sup);
                    case ID_MOSQUITO_DISMOUNT -> MessageMosquitoDismount.Handler.handle(MessageMosquitoDismount.read(buf), sup);
                    case ID_HURT_MULTIPART -> MessageHurtMultipart.Handler.handle(MessageHurtMultipart.read(buf), sup);
                    case ID_CROW_MOUNT -> MessageCrowMountPlayer.Handler.handle(MessageCrowMountPlayer.read(buf), sup);
                    case ID_CROW_DISMOUNT -> MessageCrowDismount.Handler.handle(MessageCrowDismount.read(buf), sup);
                    case ID_MUNGUS_BIOME -> MessageMungusBiomeChange.Handler.handle(MessageMungusBiomeChange.read(buf), sup);
                    case ID_KANGAROO_SYNC -> MessageKangarooInventorySync.Handler.handle(MessageKangarooInventorySync.read(buf), sup);
                    case ID_KANGAROO_EAT -> MessageKangarooEat.Handler.handle(MessageKangarooEat.read(buf), sup);
                    case ID_UPDATE_CAPSID -> MessageUpdateCapsid.Handler.handle(MessageUpdateCapsid.read(buf), sup);
                    case ID_SWING_ARM -> MessageSwingArm.Handler.handle(MessageSwingArm.read(buf), sup);
                    case ID_EAGLE_CONTROLS -> MessageUpdateEagleControls.Handler.handle(MessageUpdateEagleControls.read(buf), sup);
                    case ID_SYNC_ENTITY_POS -> MessageSyncEntityPos.Handler.handle(MessageSyncEntityPos.read(buf), sup);
                    case ID_TARANTULA_STING -> MessageTarantulaHawkSting.Handler.handle(MessageTarantulaHawkSting.read(buf), sup);
                    case ID_START_DANCING -> MessageStartDancing.Handler.handle(MessageStartDancing.read(buf), sup);
                    case ID_INTERACT_MULTIPART -> MessageInteractMultipart.Handler.handle(MessageInteractMultipart.read(buf), sup);
                    case ID_TRANSMUTE_MENU -> MessageTransmuteFromMenu.Handler.handle(MessageTransmuteFromMenu.read(buf), sup);
                    default -> {}
                }
            });
        });
        // Client receiver is registered in client-only ClientNetworkInit (no client API in main source set).
    }

    /** Called from client-only ClientNetworkInit when a server->client packet is received. */
    public static void handleClientPacket(net.minecraft.network.FriendlyByteBuf buf) {
        int id = buf.readVarInt();
        PacketContext ctx = new PacketContext(null, true);
        Supplier<PacketContext> sup = () -> ctx;
        switch (id) {
            case ID_VISUAL_FLAG -> MessageSendVisualFlagFromServer.Handler.handle(MessageSendVisualFlagFromServer.read(buf), sup);
            case ID_PUPFISH_CHUNK -> MessageSetPupfishChunkOnClient.Handler.handle(MessageSetPupfishChunkOnClient.read(buf), sup);
            case ID_TRANSMUTABLES -> MessageUpdateTransmutablesToDisplay.Handler.handle(MessageUpdateTransmutablesToDisplay.read(buf), sup);
            default -> {}
        }
    }

    public static FriendlyByteBuf writeMessageToBuf(Object message) {
        FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        if (message instanceof MessageMosquitoMountPlayer m) { buf.writeVarInt(ID_MOSQUITO_MOUNT); MessageMosquitoMountPlayer.write(m, buf); }
        else if (message instanceof MessageMosquitoDismount m) { buf.writeVarInt(ID_MOSQUITO_DISMOUNT); MessageMosquitoDismount.write(m, buf); }
        else if (message instanceof MessageHurtMultipart m) { buf.writeVarInt(ID_HURT_MULTIPART); MessageHurtMultipart.write(m, buf); }
        else if (message instanceof MessageCrowMountPlayer m) { buf.writeVarInt(ID_CROW_MOUNT); MessageCrowMountPlayer.write(m, buf); }
        else if (message instanceof MessageCrowDismount m) { buf.writeVarInt(ID_CROW_DISMOUNT); MessageCrowDismount.write(m, buf); }
        else if (message instanceof MessageMungusBiomeChange m) { buf.writeVarInt(ID_MUNGUS_BIOME); MessageMungusBiomeChange.write(m, buf); }
        else if (message instanceof MessageKangarooInventorySync m) { buf.writeVarInt(ID_KANGAROO_SYNC); MessageKangarooInventorySync.write(m, buf); }
        else if (message instanceof MessageKangarooEat m) { buf.writeVarInt(ID_KANGAROO_EAT); MessageKangarooEat.write(m, buf); }
        else if (message instanceof MessageUpdateCapsid m) { buf.writeVarInt(ID_UPDATE_CAPSID); MessageUpdateCapsid.write(m, buf); }
        else if (message instanceof MessageSwingArm m) { buf.writeVarInt(ID_SWING_ARM); MessageSwingArm.write(m, buf); }
        else if (message instanceof MessageUpdateEagleControls m) { buf.writeVarInt(ID_EAGLE_CONTROLS); MessageUpdateEagleControls.write(m, buf); }
        else if (message instanceof MessageSyncEntityPos m) { buf.writeVarInt(ID_SYNC_ENTITY_POS); MessageSyncEntityPos.write(m, buf); }
        else if (message instanceof MessageTarantulaHawkSting m) { buf.writeVarInt(ID_TARANTULA_STING); MessageTarantulaHawkSting.write(m, buf); }
        else if (message instanceof MessageStartDancing m) { buf.writeVarInt(ID_START_DANCING); MessageStartDancing.write(m, buf); }
        else if (message instanceof MessageInteractMultipart m) { buf.writeVarInt(ID_INTERACT_MULTIPART); MessageInteractMultipart.write(m, buf); }
        else if (message instanceof MessageSendVisualFlagFromServer m) { buf.writeVarInt(ID_VISUAL_FLAG); MessageSendVisualFlagFromServer.write(m, buf); }
        else if (message instanceof MessageSetPupfishChunkOnClient m) { buf.writeVarInt(ID_PUPFISH_CHUNK); MessageSetPupfishChunkOnClient.write(m, buf); }
        else if (message instanceof MessageUpdateTransmutablesToDisplay m) { buf.writeVarInt(ID_TRANSMUTABLES); MessageUpdateTransmutablesToDisplay.write(m, buf); }
        else if (message instanceof MessageTransmuteFromMenu m) { buf.writeVarInt(ID_TRANSMUTE_MENU); MessageTransmuteFromMenu.write(m, buf); }
        return buf;
    }
}
