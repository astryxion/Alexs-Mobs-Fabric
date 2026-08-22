package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.server.entity.IDancesToJukebox;
import com.github.alexthe666.citadel.server.event.EventChangeEntityTickRate;
import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import com.github.alexthe666.citadel.server.world.CitadelServerData;
import com.github.alexthe666.citadel.server.world.ModifiableTickRateServer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.jetbrains.annotations.Nullable;

public class ServerProxy {

    private static MinecraftServer minecraftServer;

    public ServerProxy() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (server.isRunning()) {
                CitadelServerData citadelServerData = CitadelServerData.get(server);
                ServerTickRateTracker tickRateTracker = citadelServerData.getOrCreateTickRateTracker();
                if (server instanceof ModifiableTickRateServer modifiableServer) {
                    long l = tickRateTracker.getServerTickLengthMs();
                    if (l == 50L) {
                        modifiableServer.resetGlobalTickLengthMs();
                    } else {
                        modifiableServer.setGlobalTickLengthMs(tickRateTracker.getServerTickLengthMs());
                    }
                    if (!server.isStopped()) {
                        tickRateTracker.masterTick();
                    }
                }
            }
        });
    }

    public void onPreInit() {
    }

    public void handleAnimationPacket(int entityId, int index) {

    }

    public void handlePropertiesPacket(String propertyID, CompoundTag compound, int entityID) {
    }

    public void handleClientTickRatePacket(CompoundTag compound) {
    }

    public void handleJukeboxPacket(Level level, int entityId, BlockPos jukeBox, boolean dancing) {
        Entity entity = level.getEntity(entityId);
        if (entity instanceof IDancesToJukebox dancer) {
            dancer.setDancing(dancing);
            dancer.setJukeboxPos(dancing ? jukeBox : null);
        }
    }


    public void openBookGUI(ItemStack book) {
    }

    public Object getISTERProperties() {
        return null;
    }

    public void onClientInit() {
    }

    public boolean canEntityTickClient(Level level, Entity entity) {
        return true;
    }

    public boolean canEntityTickServer(Level level, Entity entity) {
        if (level instanceof ServerLevel) {
            ServerTickRateTracker tracker = ServerTickRateTracker.getForServer(((ServerLevel) level).getServer());
            if (tracker.isTickingHandled(entity)) {
                return false;
            } else if (!tracker.hasNormalTickRate(entity)) {
                EventChangeEntityTickRate event = new EventChangeEntityTickRate(entity, tracker.getEntityTickLengthModifier(entity));
                EventChangeEntityTickRate.post(event);
                if (event.isCanceled()) {
                    return true;
                } else {
                    tracker.addTickBlockedEntity(entity);
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isGamePaused() {
        return false;
    }

    public float getMouseOverProgress(ItemStack itemStack) {
        return 0.0F;
    }

    public Player getClientSidePlayer() {
        return null;
    }

    @Nullable
    public MinecraftServer getMinecraftServer() {
        return minecraftServer;
    }

    public static void setMinecraftServer(MinecraftServer server) {
        minecraftServer = server;
    }
}
