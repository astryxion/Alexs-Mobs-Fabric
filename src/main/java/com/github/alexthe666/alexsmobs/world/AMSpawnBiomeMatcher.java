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
            case BIOME_TAG -> hasBiomeTag(ctx, entry.value());
            case REGISTRY_NAME -> ctx.getBiomeKey().location().toString().equals(entry.value());
        };
        return entry.negate() ? !matched : matched;
    }

    private static boolean entryMatches(Holder<Biome> biome, ResourceLocation biomeId, AMSpawnBiomeData.Entry entry) {
        boolean matched = switch (entry.type()) {
            case BIOME_TAG -> biome.is(tag(entry.value())) || biome.is(fabricConventionTag(entry.value()));
            case REGISTRY_NAME -> biomeId.toString().equals(entry.value());
        };
        return entry.negate() ? !matched : matched;
    }

    private static boolean hasBiomeTag(BiomeSelectionContext ctx, String id) {
        return ctx.hasTag(tag(id)) || ctx.hasTag(fabricConventionTag(id));
    }

    private static TagKey<Biome> tag(String id) {
        ResourceLocation loc = ResourceLocation.tryParse(id.contains(":") ? id : "minecraft:" + id);
        return TagKey.create(Registries.BIOME, loc);
    }

    /** Maps legacy Forge biome tags to Fabric convention tags when no forge shim is loaded. */
    private static TagKey<Biome> fabricConventionTag(String id) {
        if (!id.startsWith("forge:")) {
            return tag(id);
        }
        String path = id.substring("forge:".length());
        return switch (path) {
            case "is_swamp" -> tag("c:is_swamp");
            case "is_plains" -> tag("c:is_plains");
            case "is_cold" -> tag("c:is_cold");
            case "is_cold/overworld" -> tag("c:is_cold/overworld");
            case "is_hot/overworld" -> tag("c:is_hot/overworld");
            case "is_dry/overworld" -> tag("c:is_dry/overworld");
            case "is_sandy" -> tag("c:is_sandy");
            case "is_snowy" -> tag("c:is_snowy");
            case "is_mushroom" -> tag("c:is_mushroom");
            case "is_rare" -> tag("c:is_rare");
            case "is_dense/overworld" -> tag("c:is_dense_vegetation/overworld");
            case "is_coniferous" -> tag("c:is_tree/coniferous");
            case "is_wasteland" -> tag("c:is_wasteland");
            case "is_plateau" -> tag("c:is_plateau");
            case "no_default_monsters" -> tag("c:no_default_monsters");
            default -> tag(id);
        };
    }
}
