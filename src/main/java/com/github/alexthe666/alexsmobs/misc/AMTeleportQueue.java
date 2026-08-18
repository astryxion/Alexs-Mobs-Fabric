package com.github.alexthe666.alexsmobs.misc;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.tuple.Triple;

/**
 * Player dimension changes must not run mid-tick (chunk/entity iteration). Queue them and apply after the level tick.
 */
public final class AMTeleportQueue {
    public static final ObjectList<Triple<ServerPlayer, ServerLevel, BlockPos>> PLAYERS = new ObjectArrayList<>();

    private AMTeleportQueue() {
    }
}
