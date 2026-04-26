package com.github.alexthe666.citadel.server.entity;

import net.minecraft.core.BlockPos;

/**
 * Implementations update local dance state; send {@link com.github.alexthe666.citadel.server.message.DanceJukeboxMessage}
 * from the client via {@code net.neoforged.neoforge.client.network.ClientPacketDistributor} when syncing to the server.
 */
public interface IDancesToJukebox {

    void setDancing(boolean dancing);

    void setJukeboxPos(BlockPos pos);
}
