package com.github.alexthe666.citadel.server.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.util.TriState;

import java.util.ArrayList;
import java.util.List;

public class EventMergeStructureSpawns {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(
            Listener.class,
            listeners -> event -> {
                for (Listener listener : listeners) {
                    listener.onMerge(event);
                }
            });

    private StructureManager structureManager;
    private BlockPos pos;
    private MobCategory category;
    private WeightedList<MobSpawnSettings.SpawnerData> structureSpawns;
    private WeightedList<MobSpawnSettings.SpawnerData> biomeSpawns;
    private TriState result = TriState.DEFAULT;

    public EventMergeStructureSpawns(StructureManager structureManager, BlockPos pos, MobCategory category, WeightedList<MobSpawnSettings.SpawnerData> structureSpawns, WeightedList<MobSpawnSettings.SpawnerData> biomeSpawns) {
        this.structureManager = structureManager;
        this.pos = pos;
        this.category = category;
        this.structureSpawns = structureSpawns;
        this.biomeSpawns = biomeSpawns;
    }

    public StructureManager getStructureManager() {
        return structureManager;
    }

    public BlockPos getPos() {
        return pos;
    }

    public MobCategory getCategory() {
        return category;
    }

    public boolean isStructureTagged(TagKey<Structure> tagKey) {
        var start = structureManager.getStructureWithPieceAt(pos, tagKey);
        return start != null && start.isValid();
    }

    public WeightedList<MobSpawnSettings.SpawnerData> getStructureSpawns() {
        return structureSpawns;
    }

    public void setStructureSpawns(WeightedList<MobSpawnSettings.SpawnerData> spawns) {
        structureSpawns = spawns;
    }

    public void mergeSpawns() {
        List<Weighted<MobSpawnSettings.SpawnerData>> list = new ArrayList<>(biomeSpawns.unwrap());
        outer:
        for (Weighted<MobSpawnSettings.SpawnerData> structureSpawn : structureSpawns.unwrap()) {
            for (Weighted<MobSpawnSettings.SpawnerData> existing : list) {
                if (existing.value().equals(structureSpawn.value())) {
                    continue outer;
                }
            }
            list.add(structureSpawn);
        }
        this.setStructureSpawns(WeightedList.of(list));
    }

    public WeightedList<MobSpawnSettings.SpawnerData> getBiomeSpawns() {
        return biomeSpawns;
    }

    public TriState getResult() {
        return this.result;
    }

    public void setResult(TriState result) {
        this.result = result;
    }

    public void post() {
        EVENT.invoker().onMerge(this);
    }

    @FunctionalInterface
    public interface Listener {
        void onMerge(EventMergeStructureSpawns event);
    }
}
