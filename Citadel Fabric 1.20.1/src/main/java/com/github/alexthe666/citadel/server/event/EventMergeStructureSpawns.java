package com.github.alexthe666.citadel.server.event;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventMergeStructureSpawns {
    public enum Result { DEFAULT, ALLOW, DENY }
    private static final List<Consumer<EventMergeStructureSpawns>> LISTENERS = new ArrayList<>();
    public static void addListener(Consumer<EventMergeStructureSpawns> listener) { LISTENERS.add(listener); }
    public static void post(EventMergeStructureSpawns event) { for (Consumer<EventMergeStructureSpawns> l : LISTENERS) l.accept(event); }

    private final StructureManager structureManager;
    private final BlockPos pos;
    private final MobCategory category;
    private WeightedRandomList<MobSpawnSettings.SpawnerData> structureSpawns;
    private final WeightedRandomList<MobSpawnSettings.SpawnerData> biomeSpawns;
    private Result result = Result.DEFAULT;

    public EventMergeStructureSpawns(StructureManager structureManager, BlockPos pos, MobCategory category, WeightedRandomList<MobSpawnSettings.SpawnerData> structureSpawns, WeightedRandomList<MobSpawnSettings.SpawnerData> biomeSpawns) {
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
        return structureManager.getStructureWithPieceAt(pos, tagKey).isValid();
    }

    public WeightedRandomList<MobSpawnSettings.SpawnerData> getStructureSpawns() {
        return structureSpawns;
    }

    public void setStructureSpawns(WeightedRandomList<MobSpawnSettings.SpawnerData> spawns) {
        structureSpawns = spawns;
    }

    public void mergeSpawns() {
        List<MobSpawnSettings.SpawnerData> list = new ArrayList<>(biomeSpawns.unwrap());
        for (MobSpawnSettings.SpawnerData structureSpawn : structureSpawns.unwrap()) {
            if (!list.contains(structureSpawn)) {
                list.add(structureSpawn);
            }
        }
        this.setStructureSpawns(WeightedRandomList.create(list));
    }

    public WeightedRandomList<MobSpawnSettings.SpawnerData> getBiomeSpawns() {
        return biomeSpawns;
    }

    public Result getResult() { return result; }
    public void setResult(Result result) { this.result = result; }
}
