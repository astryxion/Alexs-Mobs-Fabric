package com.github.alexthe666.alexsmobs.world;

import java.util.List;

/** Forge {@code SpawnBiomeData} equivalent: OR of pools, AND within each pool. */
public record AMSpawnBiomeData(List<List<Entry>> pools) {
    public enum EntryType {
        BIOME_TAG,
        REGISTRY_NAME
    }

    public record Entry(EntryType type, boolean negate, String value) {}
}
