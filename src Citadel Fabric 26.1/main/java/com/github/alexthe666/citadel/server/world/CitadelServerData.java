package com.github.alexthe666.citadel.server.world;

import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.Nullable;

public class CitadelServerData extends SavedData {
    private static final Map<MinecraftServer, CitadelServerData> dataMap = new HashMap<>();
    private static final Codec<CitadelServerData> CODEC = CompoundTag.CODEC.flatXmap(
            tag -> {
                MinecraftServer server = Objects.requireNonNull(com.github.alexthe666.citadel.Citadel.CURRENT_SERVER, "server");
                return DataResult.success(load(server, tag));
            },
            data -> DataResult.success(data.writePersistentTag(data.server.registryAccess())));

    public static final SavedDataType<CitadelServerData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("citadel", "citadel_world_data"),
            () -> new CitadelServerData(Objects.requireNonNull(com.github.alexthe666.citadel.Citadel.CURRENT_SERVER, "server")),
            CODEC,
            null);

    private final MinecraftServer server;

    private ServerTickRateTracker tickRateTracker = null;

    public CitadelServerData(MinecraftServer server) {
        this.server = server;
    }

    public static CitadelServerData get(MinecraftServer server) {
        CitadelServerData fromMap = dataMap.get(server);
        if (fromMap == null) {
            CitadelServerData data = server.overworld().getDataStorage().computeIfAbsent(TYPE);
            data.setDirty(true);
            dataMap.put(server, data);
            return data;
        }
        return fromMap;
    }

    public static CitadelServerData load(MinecraftServer server, CompoundTag tag) {
        CitadelServerData data = new CitadelServerData(server);
        if (tag.contains("TickRateTracker")) {
            data.tickRateTracker = new ServerTickRateTracker(server, tag.getCompoundOrEmpty("TickRateTracker"));
        } else {
            data.tickRateTracker = new ServerTickRateTracker(server);
        }
        return data;
    }

    public ServerTickRateTracker getOrCreateTickRateTracker() {
        if (tickRateTracker == null) {
            tickRateTracker = new ServerTickRateTracker(server);
        }
        return tickRateTracker;
    }

    private CompoundTag writePersistentTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        if (tickRateTracker != null) {
            tag.put("TickRateTracker", tickRateTracker.toTag());
        }
        return tag;
    }
}
