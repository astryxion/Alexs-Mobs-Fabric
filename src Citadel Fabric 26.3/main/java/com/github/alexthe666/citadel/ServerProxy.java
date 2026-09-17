package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.server.entity.IDancesToJukebox;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.MNode;
import com.github.alexthe666.citadel.server.event.EventChangeEntityTickRate;
import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Set;

public class ServerProxy {
    public ServerProxy() {
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

    public void handleSyncPathPacket(Set<MNode> lastDebugNodesVisited, Set<MNode> lastDebugNodesNotVisited, Set<MNode> lastDebugNodesPath) {
    }

    public void handleSyncPathReachedPacket(Set<BlockPos> reached) {
    }


    public void openBookGUI(ItemStack book) {
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
                event.post();
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
}
