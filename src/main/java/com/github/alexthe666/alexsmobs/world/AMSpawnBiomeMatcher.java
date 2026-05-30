package com.github.alexthe666.alexsmobs.world;

import java.util.List;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public final class AMSpawnBiomeMatcher {
    private AMSpawnBiomeMatcher() {}

    public static boolean matches(BiomeSelectionContext ctx, AMSpawnBiomeData data) {
        for (List<AMSpawnBiomeData.Entry> pool : data.pools()) {
            if (poolMatches(ctx, pool)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matches(Holder<Biome> biome, ResourceLocation biomeId, AMSpawnBiomeData data) {
        for (List<AMSpawnBiomeData.Entry> pool : data.pools()) {
            if (poolMatches(biome, biomeId, pool)) {
                return true;
            }
        }
        return false;
    }

    private static boolean poolMatches(BiomeSelectionContext ctx, List<AMSpawnBiomeData.Entry> pool) {
        for (AMSpawnBiomeData.Entry entry : pool) {
            if (!entryMatches(ctx, entry)) {
                return false;
            }
        }
        return true;
    }

    private static boolean poolMatches(Holder<Biome> biome, ResourceLocation biomeId, List<AMSpawnBiomeData.Entry> pool) {
        for (AMSpawnBiomeData.Entry entry : pool) {
            if (!entryMatches(biome, biomeId, entry)) {
                return false;
            }
        }
        return true;
    }

    private static boolean entryMatches(BiomeSelectionContext ctx, AMSpawnBiomeData.Entry entry) {
        boolean matched = switch (entry.type()) {
            case BIOME_TAG -> ctx.hasTag(tag(entry.value()));
            case REGISTRY_NAME -> ctx.getBiomeKey().location().toString().equals(entry.value());
        };
        return entry.negate() ? !matched : matched;
    }

    private static boolean entryMatches(Holder<Biome> biome, ResourceLocation biomeId, AMSpawnBiomeData.Entry entry) {
        boolean matched = switch (entry.type()) {
            case BIOME_TAG -> biome.is(tag(entry.value()));
            case REGISTRY_NAME -> biomeId.toString().equals(entry.value());
        };
        return entry.negate() ? !matched : matched;
    }

    private static TagKey<Biome> tag(String id) {
        ResourceLocation loc = ResourceLocation.tryParse(id.contains(":") ? id : "minecraft:" + id);
        return TagKey.create(Registries.BIOME, loc);
    }
}
