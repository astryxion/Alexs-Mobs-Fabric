package com.github.alexthe666.alexsmobs.world;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Fabric-native biome matching for spawn registration. Use this instead of Citadel's
 * SpawnBiomeData.matches() which fails on Fabric (NoSuchFieldException: ROOT when
 * resolving biome registry for tag checks), causing zero mod mob spawns.
 * <p>
 * Uses only Holder.is(TagKey) and biome ResourceLocation so no registry reflection is needed.
 * Some mobs use special-case rules aligned with {@link com.github.alexthe666.alexsmobs.config.DefaultBiomes}.
 */
public final class AMFabricBiomeMatcher {

    private static final Map<String, TagKey<Biome>> TAG_CACHE = new HashMap<>();

    private static final ResourceLocation MUSHROOM_FIELDS = ResourceLocation.fromNamespaceAndPath("minecraft", "mushroom_fields");
    private static final ResourceLocation DEEP_DARK = ResourceLocation.fromNamespaceAndPath("minecraft", "deep_dark");

    private static final TagKey<Biome> SKREECHERS_CAN_SPAWN_WARDENS =
            TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("alexsmobs", "skreechers_can_spawn_wardens"));

    private AMFabricBiomeMatcher() {
    }

    private static TagKey<Biome> tag(String id) {
        return TAG_CACHE.computeIfAbsent(id, s -> {
            ResourceLocation loc = ResourceLocation.tryParse(s);
            return loc != null ? TagKey.create(Registries.BIOME, loc) : null;
        });
    }

    private static ResourceLocation loc(String id) {
        return ResourceLocation.tryParse(id);
    }

    private static boolean isFarseerKey(String configKey) {
        return "alexsmobs:farseer".equals(configKey) || "alexsmobs:farseer_spawns".equals(configKey);
    }

    private static boolean isSkreecherKey(String configKey) {
        return "alexsmobs:skreecher".equals(configKey) || "alexsmobs:skreecher_spawns".equals(configKey);
    }

    private static boolean isUnderminerKey(String configKey) {
        return "alexsmobs:underminer".equals(configKey) || "alexsmobs:underminer_spawns".equals(configKey);
    }

    private static boolean isMurmurKey(String configKey) {
        return "alexsmobs:murmur".equals(configKey) || "alexsmobs:murmur_spawns".equals(configKey);
    }

    /** Matches {@link com.github.alexthe666.alexsmobs.config.DefaultBiomes#CAVES} / CAVES_MONSTER vanilla deny list (surface oceans, mushroom, deep dark). */
    private static boolean matchesCaveSpawnSurface(Holder<Biome> biome, ResourceLocation biomeLoc) {
        TagKey<Biome> overworld = tag("minecraft:is_overworld");
        if (overworld == null || !biome.is(overworld)) {
            return false;
        }
        TagKey<Biome> ocean = tag("minecraft:is_ocean");
        if (ocean != null && biome.is(ocean)) {
            return false;
        }
        if (MUSHROOM_FIELDS.equals(biomeLoc) || DEEP_DARK.equals(biomeLoc)) {
            return false;
        }
        return true;
    }

    /** Returns true if the biome matches the spawn config. Uses only vanilla tags and biome IDs so spawns work on Fabric. */
    public static boolean test(String configKey, Holder<Biome> biome) {
        ResourceLocation biomeLoc = biome.unwrapKey().map(k -> k.location()).orElse(null);
        if (biomeLoc == null) {
            return false;
        }

        // DefaultBiomes.EMPTY: no biome list in Forge; do not inject natural spawns.
        if ("alexsmobs:void_worm_spawns".equals(configKey) || "alexsmobs:warped_mosco_spawns".equals(configKey)) {
            return false;
        }

        if (isFarseerKey(configKey)) {
            return !MUSHROOM_FIELDS.equals(biomeLoc);
        }

        if (isSkreecherKey(configKey)) {
            return biome.is(SKREECHERS_CAN_SPAWN_WARDENS);
        }

        if (isUnderminerKey(configKey) || isMurmurKey(configKey)) {
            return matchesCaveSpawnSurface(biome, biomeLoc);
        }

        String[] tags = tagsFor(configKey);
        String[] biomeIds = biomeIdsFor(configKey);

        if (tags == null && biomeIds == null) {
            return false;
        }

        if (tags != null && tags.length > 0) {
            for (String t : tags) {
                TagKey<Biome> key = tag(t);
                if (key != null && biome.is(key)) {
                    return true;
                }
            }
        }
        Set<ResourceLocation> ids = biomeIds != null
                ? Arrays.stream(biomeIds).map(AMFabricBiomeMatcher::loc).filter(l -> l != null).collect(Collectors.toSet())
                : Set.of();
        return ids.contains(biomeLoc);
    }

    private static String[] tagsFor(String configKey) {
        switch (configKey) {
            case "alexsmobs:grizzly_bear_spawns":
                return new String[]{"minecraft:is_forest", "minecraft:is_taiga"};
            case "alexsmobs:raccoon_spawns":
                return new String[]{"minecraft:is_forest", "minecraft:is_taiga", "minecraft:is_plains"};
            case "alexsmobs:roadrunner_spawns":
                return new String[]{"minecraft:is_badlands", "minecraft:is_savanna", "minecraft:is_beach"};
            case "alexsmobs:bone_serpent_spawns":
                return new String[]{"minecraft:is_nether"};
            case "alexsmobs:gazelle_spawns":
            case "alexsmobs:elephant_spawns":
                return new String[]{"minecraft:is_savanna"};
            case "alexsmobs:crocodile_spawns":
                return new String[]{"minecraft:allows_surface_slime_spawns", "minecraft:is_river"};
            case "alexsmobs:fly_spawns":
                return new String[]{"minecraft:is_overworld"};
            case "alexsmobs:hummingbird_spawns":
                return new String[]{"minecraft:is_jungle"};
            case "alexsmobs:orca_spawns":
                return new String[]{"minecraft:is_ocean"};
            case "alexsmobs:sunbird_spawns":
                // DefaultBiomes.SUNBIRD: mountains / high terrain (not beach-only).
                return new String[]{"minecraft:is_mountain", "minecraft:is_badlands"};
            case "alexsmobs:gorilla_spawns":
                return new String[]{"minecraft:is_jungle"};
            case "alexsmobs:crimson_mosquito_spawns":
                return new String[]{"minecraft:is_nether"};
            case "alexsmobs:rattlesnake_spawns":
                return new String[]{"minecraft:is_badlands", "minecraft:is_desert"};
            case "alexsmobs:endergrade_spawns":
                return new String[]{"minecraft:is_end"};
            case "alexsmobs:hammerhead_shark_spawns":
                return new String[]{"minecraft:is_ocean"};
            case "alexsmobs:lobster_spawns":
                return new String[]{"minecraft:is_ocean"};
            case "alexsmobs:komodo_dragon_spawns":
                return new String[]{"minecraft:is_overworld"};
            case "alexsmobs:capuchin_monkey_spawns":
                return new String[]{"minecraft:is_jungle"};
            case "alexsmobs:cave_centipede_spawns":
                return new String[]{"minecraft:is_overworld"};
            case "alexsmobs:warped_toad_spawns":
                return null; // nether warped only; use biome IDs
            case "alexsmobs:moose_spawns":
                return new String[]{"minecraft:is_taiga"};
            case "alexsmobs:mimicube_spawns":
                return new String[]{"minecraft:is_end"};
            case "alexsmobs:blobfish_spawns":
                return new String[]{"minecraft:is_deep_ocean"};
            case "alexsmobs:seal_spawns":
                return new String[]{"minecraft:is_beach", "minecraft:is_ocean"};
            case "alexsmobs:cockroach_spawns":
                return new String[]{"minecraft:is_overworld"};
            case "alexsmobs:shoebill_spawns":
                return new String[]{"minecraft:allows_surface_slime_spawns"};
            case "alexsmobs:soul_vulture_spawns":
                return new String[]{"minecraft:is_nether"};
            case "alexsmobs:snow_leopard_spawns":
                return new String[]{"minecraft:is_taiga"};
            case "alexsmobs:spectre_spawns":
                return new String[]{"minecraft:is_end"};
            case "alexsmobs:crow_spawns":
                return new String[]{"minecraft:is_forest", "minecraft:is_plains", "minecraft:is_taiga"};
            case "alexsmobs:alligator_snapping_turtle_spawns":
                return new String[]{"minecraft:allows_surface_slime_spawns"};
            case "alexsmobs:mungus_spawns":
            case "alexsmobs:bunfungus_spawns":
                // DefaultBiomes.MUNGUS: mushroom_fields only (tags + ids are OR; use ids-only).
                return new String[0];
            case "alexsmobs:mantis_shrimp_spawns":
                return new String[]{"minecraft:is_ocean"};
            case "alexsmobs:guster_spawns":
                return new String[]{"minecraft:is_badlands", "minecraft:is_savanna", "minecraft:is_beach"};
            case "alexsmobs:straddler_spawns":
            case "alexsmobs:stradpole_spawns":
                // DefaultBiomes.STRADDLER: basalt deltas / nether mod biomes (not End).
                return new String[]{"minecraft:is_nether"};
            case "alexsmobs:emu_spawns":
            case "alexsmobs:kangaroo_spawns":
                return new String[]{"minecraft:is_badlands", "minecraft:is_savanna"};
            case "alexsmobs:platypus_spawns":
                return new String[]{"minecraft:is_river"};
            case "alexsmobs:dropbear_spawns":
                return new String[]{"minecraft:is_nether"};
            case "alexsmobs:tasmanian_devil_spawns":
                return new String[]{"minecraft:is_forest", "minecraft:is_taiga"};
            case "alexsmobs:cachalot_whale_spawns":
                return new String[]{"minecraft:is_ocean"};
            case "alexsmobs:leafcutter_anthill_spawns":
                return new String[]{"minecraft:is_jungle"};
            case "alexsmobs:enderiophage_spawns":
                return new String[]{"minecraft:is_end"};
            case "alexsmobs:bald_eagle_spawns":
                return new String[]{"minecraft:is_forest", "minecraft:is_river", "minecraft:is_ocean"};
            case "alexsmobs:tiger_spawns":
                return new String[]{"minecraft:is_jungle"};
            case "alexsmobs:tarantula_hawk_spawns":
                return new String[]{"minecraft:is_desert"};
            case "alexsmobs:frilled_shark_spawns":
                return new String[]{"minecraft:is_deep_ocean"};
            case "alexsmobs:mimic_octopus_spawns":
                return new String[]{"minecraft:is_ocean"};
            case "alexsmobs:seagull_spawns":
                return new String[]{"minecraft:is_beach", "minecraft:is_ocean"};
            case "alexsmobs:froststalker_spawns":
                return new String[]{"minecraft:is_taiga"};
            case "alexsmobs:tusklin_spawns":
                return new String[]{"minecraft:is_taiga"};
            case "alexsmobs:laviathan_spawns":
                return new String[]{"minecraft:is_nether"};
            case "alexsmobs:cosmaw_spawns":
                return new String[]{"minecraft:is_end"};
            case "alexsmobs:toucan_spawns":
                return new String[]{"minecraft:is_jungle"};
            case "alexsmobs:maned_wolf_spawns":
                return new String[]{"minecraft:is_savanna"};
            case "alexsmobs:anaconda_spawns":
                return new String[]{"minecraft:is_jungle", "minecraft:allows_surface_slime_spawns"};
            case "alexsmobs:anteater_spawns":
                return new String[]{"minecraft:is_savanna", "minecraft:is_jungle"};
            case "alexsmobs:rocky_roller_spawns":
                return new String[]{"minecraft:is_badlands"};
            case "alexsmobs:flutter_spawns":
                return new String[]{"minecraft:is_forest"};
            case "alexsmobs:gelada_monkey_spawns":
                return new String[]{"minecraft:is_mountain"};
            case "alexsmobs:jerboa_spawns":
                return new String[]{"minecraft:is_desert"};
            case "alexsmobs:terrapin_spawns":
                return new String[]{"minecraft:allows_surface_slime_spawns"};
            case "alexsmobs:comb_jelly_spawns":
                return new String[]{"minecraft:is_ocean"};
            case "alexsmobs:cosmic_cod_spawns":
                return new String[]{"minecraft:is_end"};
            case "alexsmobs:bison_spawns":
                return new String[]{"minecraft:is_plains"};
            case "alexsmobs:giant_squid_spawns":
                return new String[]{"minecraft:is_deep_ocean"};
            case "alexsmobs:devils_hole_pupfish_spawns":
                // DefaultBiomes.ALL_OVERWORLD
                return new String[]{"minecraft:is_overworld"};
            case "alexsmobs:catfish_spawns":
                return new String[]{"minecraft:is_river", "minecraft:allows_surface_slime_spawns"};
            case "alexsmobs:flying_fish_spawns":
                return new String[]{"minecraft:is_ocean"};
            case "alexsmobs:skelewag_spawns":
                return new String[]{"minecraft:is_ocean"};
            case "alexsmobs:rain_frog_spawns":
                return new String[]{"minecraft:is_jungle"};
            case "alexsmobs:potoo_spawns":
                // DefaultBiomes.POTOO: dark_forest (id match in biomeIdsFor).
                return null;
            case "alexsmobs:mudskipper_spawns":
                return new String[]{"minecraft:allows_surface_slime_spawns"};
            case "alexsmobs:rhinoceros_spawns":
                return new String[]{"minecraft:is_savanna"};
            case "alexsmobs:sugar_glider_spawns":
                return new String[]{"minecraft:is_forest", "minecraft:is_jungle"};
            case "alexsmobs:skunk_spawns":
                return new String[]{"minecraft:is_forest"};
            case "alexsmobs:banana_slug_spawns":
                return new String[]{"minecraft:is_forest"};
            case "alexsmobs:blue_jay_spawns":
                return new String[]{"minecraft:is_forest"};
            case "alexsmobs:caiman_spawns":
                return new String[]{"minecraft:allows_surface_slime_spawns"};
            case "alexsmobs:triops_spawns":
                return new String[]{"minecraft:is_desert"};
            case "alexsmobs:void_worm_spawns":
            case "alexsmobs:warped_mosco_spawns":
                // DefaultBiomes.EMPTY: no natural biome spawns (boss / special).
                return new String[0];
            default:
                return null;
        }
    }

    private static String[] biomeIdsFor(String configKey) {
        switch (configKey) {
            case "alexsmobs:grizzly_bear_spawns":
                return new String[]{"minecraft:sparse_jungle", "minecraft:cherry_grove"};
            case "alexsmobs:raccoon_spawns":
                return new String[]{"minecraft:plains", "minecraft:sunflower_plains", "minecraft:cherry_grove"};
            case "alexsmobs:moose_spawns":
                return new String[]{"minecraft:snowy_plains", "minecraft:ice_spikes", "minecraft:frozen_river", "minecraft:snowy_taiga", "minecraft:snowy_beach", "minecraft:frozen_peaks", "minecraft:snowy_slopes"};
            case "alexsmobs:crocodile_spawns":
                return new String[]{"minecraft:mangrove_swamp"};
            case "alexsmobs:hummingbird_spawns":
                return new String[]{"minecraft:flower_forest", "minecraft:sunflower_plains", "minecraft:meadow", "minecraft:cherry_grove"};
            case "alexsmobs:orca_spawns":
                return new String[]{"minecraft:ocean", "minecraft:cold_ocean", "minecraft:frozen_ocean", "minecraft:deep_ocean", "minecraft:deep_cold_ocean", "minecraft:deep_frozen_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:warm_ocean"};
            case "alexsmobs:warped_toad_spawns":
                return new String[]{"minecraft:warped_forest"};
            case "alexsmobs:seal_spawns":
                return new String[]{"minecraft:snowy_plains", "minecraft:ice_spikes", "minecraft:frozen_river", "minecraft:snowy_taiga", "minecraft:snowy_beach", "minecraft:frozen_peaks", "minecraft:snowy_slopes", "minecraft:stony_shore"};
            case "alexsmobs:shoebill_spawns":
                return new String[]{"minecraft:swamp", "minecraft:mangrove_swamp"};
            case "alexsmobs:soul_vulture_spawns":
                return new String[]{"minecraft:soul_sand_valley"};
            case "alexsmobs:snow_leopard_spawns":
                return new String[]{"minecraft:snowy_plains", "minecraft:ice_spikes", "minecraft:snowy_taiga", "minecraft:frozen_peaks", "minecraft:snowy_slopes"};
            case "alexsmobs:platypus_spawns":
                return new String[]{"minecraft:river", "minecraft:frozen_river"};
            case "alexsmobs:dropbear_spawns":
                return new String[]{"minecraft:nether_wastes"};
            case "alexsmobs:cachalot_whale_spawns":
                return new String[]{"minecraft:ocean", "minecraft:deep_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:lukewarm_ocean", "minecraft:cold_ocean", "minecraft:deep_cold_ocean", "minecraft:frozen_ocean", "minecraft:deep_frozen_ocean"};
            case "alexsmobs:cachalot_whale_beached_spawns":
                return new String[]{"minecraft:beach", "minecraft:snowy_beach", "minecraft:stony_shore"};
            case "alexsmobs:leafcutter_anthill_spawns":
                return new String[]{"minecraft:jungle", "minecraft:bamboo_jungle", "minecraft:sparse_jungle"};
            case "alexsmobs:bald_eagle_spawns":
                return new String[]{"minecraft:meadow", "minecraft:river", "minecraft:forest", "minecraft:taiga"};
            case "alexsmobs:tiger_spawns":
                return new String[]{"minecraft:jungle", "minecraft:bamboo_jungle", "minecraft:sparse_jungle"};
            case "alexsmobs:sunbird_spawns":
                return null;
            case "alexsmobs:devils_hole_pupfish_spawns":
                return new String[]{"minecraft:desert"};
            case "alexsmobs:straddler_spawns":
            case "alexsmobs:stradpole_spawns":
                return new String[]{"minecraft:basalt_deltas"};
            case "alexsmobs:mungus_spawns":
            case "alexsmobs:bunfungus_spawns":
                return new String[]{"minecraft:mushroom_fields"};
            case "alexsmobs:potoo_spawns":
                return new String[]{"minecraft:dark_forest"};
            case "alexsmobs:void_worm_spawns":
            case "alexsmobs:warped_mosco_spawns":
                return new String[0];
            default:
                return null;
        }
    }
}
