package com.github.alexthe666.citadel.server.world;

import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

public class CitadelServerData extends SavedData {

    private static final String IDENTIFIER = "citadel_world_data";

    private final MinecraftServer server;

    private ServerTickRateTracker tickRateTracker = null;

    public CitadelServerData(MinecraftServer server) {
        super();
        this.server = server;
    }

    public CitadelServerData(MinecraftServer server, CompoundTag tag) {
        this(server);
        if (tag.contains("TickRateTracker")) {
            tickRateTracker = new ServerTickRateTracker(server, tag.getCompound("TickRateTracker"));
        } else {
            tickRateTracker = new ServerTickRateTracker(server);
        }
    }

    @NotNull
    public static CitadelServerData get(MinecraftServer server) {
        DimensionDataStorage storage = server.getLevel(Level.OVERWORLD).getDataStorage();
        SavedData.Factory<CitadelServerData> factory = new SavedData.Factory<>(
                () -> new CitadelServerData(server),
                (tag, lookup) -> new CitadelServerData(server, tag),
                DataFixTypes.LEVEL
        );
        CitadelServerData data = storage.computeIfAbsent(factory, IDENTIFIER);
        data.setDirty();
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        if (tickRateTracker != null) {
            tag.put("TickRateTracker", tickRateTracker.toTag());
        }
        return tag;
    }


    public ServerTickRateTracker getOrCreateTickRateTracker() {
        if (tickRateTracker == null) {
            tickRateTracker = new ServerTickRateTracker(server);
        }
        return tickRateTracker;
    }
}
