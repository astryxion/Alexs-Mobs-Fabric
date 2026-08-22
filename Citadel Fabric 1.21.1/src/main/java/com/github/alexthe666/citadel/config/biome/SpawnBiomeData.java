package com.github.alexthe666.citadel.config.biome;

import com.github.alexthe666.citadel.Citadel;
import com.google.gson.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Deprecated(since = "2.6.2")
public class SpawnBiomeData {

    private List<List<SpawnBiomeEntry>> biomes = new ArrayList<>();

    public SpawnBiomeData() {
    }

    private SpawnBiomeData(SpawnBiomeEntry[][] biomesRead) {
        biomes = new ArrayList<>();
        for (SpawnBiomeEntry[] innerArray : biomesRead) {
            biomes.add(Arrays.asList(innerArray));
        }
    }

    public SpawnBiomeData addBiomeEntry(BiomeEntryType type, boolean negate, String value, int pool) {
        if (biomes.isEmpty() || biomes.size() < pool + 1) {
            biomes.add(new ArrayList<>());
        }
        biomes.get(pool).add(new SpawnBiomeEntry(type, negate, value));
        return this;
    }

    public boolean matches(@Nullable Holder<Biome> biomeHolder, ResourceLocation registryName) {
        for (List<SpawnBiomeEntry> all : biomes) {
            boolean overall = true;
            for (SpawnBiomeEntry cond : all) {
                if (!cond.matches(biomeHolder, registryName)) {
                    overall = false;
                }
            }
            if (overall) {
                return true;
            }
        }
        return false;
    }

    public static class Deserializer implements JsonDeserializer<SpawnBiomeData>, JsonSerializer<SpawnBiomeData> {

        @Override
        public SpawnBiomeData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonobject = json.getAsJsonObject();
            SpawnBiomeEntry[][] biomesRead = GsonHelper.getAsObject(jsonobject, "biomes", new SpawnBiomeEntry[0][0], context, SpawnBiomeEntry[][].class);
            return new SpawnBiomeData(biomesRead);
        }

        @Override
        public JsonElement serialize(SpawnBiomeData src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonobject = new JsonObject();
            jsonobject.add("biomes", context.serialize(src.biomes));
            return jsonobject;
        }
    }

    private class SpawnBiomeEntry {
        BiomeEntryType type;
        boolean negate;
        String value;

        public SpawnBiomeEntry(BiomeEntryType type, boolean remove, String value) {
            this.type = type;
            this.negate = remove;
            this.value = value;
        }

        public boolean matches(@Nullable Holder<Biome> biomeHolder, ResourceLocation registryName) {
            if (type.isDepreciated()) {
                Citadel.LOGGER.warn("biome config: BIOME_DICT and BIOME_CATEGORY are no longer valid in 1.19+. Please use BIOME_TAG instead.");
                return false;
            } else {
                if (type == BiomeEntryType.BIOME_TAG) {
                    if (biomeHolder != null && value != null) {
                        boolean matched = biomeHasTag(biomeHolder, value) || biomeHasTag(biomeHolder, remapLegacyTag(value));
                        return negate ? !matched : matched;
                    }
                    return negate;
                } else {
                    if (registryName.toString().equals(value)) {
                        return !negate;
                    }
                    return negate;
                }
            }
        }

        private boolean biomeHasTag(Holder<Biome> biomeHolder, String tagId) {
            ResourceLocation loc = ResourceLocation.tryParse(tagId.contains(":") ? tagId : "minecraft:" + tagId);
            if (loc == null) {
                return false;
            }
            return biomeHolder.is(TagKey.create(Registries.BIOME, loc));
        }

        private String remapLegacyTag(String id) {
            if (!id.startsWith("forge:")) {
                return id;
            }
            String path = id.substring("forge:".length());
            return switch (path) {
                case "is_swamp" -> "c:is_swamp";
                case "is_plains" -> "c:is_plains";
                case "is_cold" -> "c:is_cold";
                case "is_cold/overworld" -> "c:is_cold/overworld";
                case "is_hot/overworld" -> "c:is_hot/overworld";
                case "is_dry/overworld" -> "c:is_dry/overworld";
                case "is_sandy" -> "c:is_sandy";
                case "is_snowy" -> "c:is_snowy";
                case "is_mushroom" -> "c:is_mushroom";
                case "is_rare" -> "c:is_rare";
                case "is_dense/overworld" -> "c:is_dense_vegetation/overworld";
                case "is_coniferous" -> "c:is_tree/coniferous";
                case "is_wasteland" -> "c:is_wasteland";
                case "is_plateau" -> "c:is_plateau";
                case "no_default_monsters" -> "c:no_default_monsters";
                default -> id;
            };
        }
    }
}