package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.GenerationStep;

/** Fabric spawn registration with Forge-accurate {@link AMSpawnBiomeData} pool matching. */
public final class AMSpawnRegistry {
    private AMSpawnRegistry() {}

    public static void register() {
        addSpawns();
        addLeafcutterAnthillFeature();
        AMSpawnStructureRegistry.register();
    }

    static void addSpawns() {
        if (AMConfig.grizzlyBearSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.ALL_FOREST, MobCategory.CREATURE, AMEntityRegistry.GRIZZLY_BEAR, AMConfig.grizzlyBearSpawnWeight, 2, 3);
        if (AMConfig.roadrunnerSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.ROADRUNNER, MobCategory.CREATURE, AMEntityRegistry.ROADRUNNER, AMConfig.roadrunnerSpawnWeight, 2, 2);
        if (AMConfig.boneSerpentSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.ALL_NETHER_MONSTER, MobCategory.MONSTER, AMEntityRegistry.BONE_SERPENT, AMConfig.boneSerpentSpawnWeight, 1, 1);
        if (AMConfig.gazelleSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.GAZELLE, MobCategory.CREATURE, AMEntityRegistry.GAZELLE, AMConfig.gazelleSpawnWeight, 7, 7);
        if (AMConfig.crocodileSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.CROCODILE, MobCategory.CREATURE, AMEntityRegistry.CROCODILE, AMConfig.crocodileSpawnWeight, 1, 2);
        if (AMConfig.flySpawnWeight > 0) addMobSpawn(AMDefaultBiomes.FLY, MobCategory.AMBIENT, AMEntityRegistry.FLY, AMConfig.flySpawnWeight, 2, 3);
        if (AMConfig.hummingbirdSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.HUMMINGBIRD, MobCategory.CREATURE, AMEntityRegistry.HUMMINGBIRD, AMConfig.hummingbirdSpawnWeight, 7, 7);
        if (AMConfig.orcaSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.ORCA, MobCategory.WATER_CREATURE, AMEntityRegistry.ORCA, AMConfig.orcaSpawnWeight, 3, 4);
        if (AMConfig.sunbirdSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.SUNBIRD, MobCategory.CREATURE, AMEntityRegistry.SUNBIRD, AMConfig.sunbirdSpawnWeight, 1, 1);
        if (AMConfig.gorillaSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.GORILLA, MobCategory.CREATURE, AMEntityRegistry.GORILLA, AMConfig.gorillaSpawnWeight, 7, 7);
        if (AMConfig.crimsonMosquitoSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.CRIMSON_MOSQUITO, MobCategory.MONSTER, AMEntityRegistry.CRIMSON_MOSQUITO, AMConfig.crimsonMosquitoSpawnWeight, 4, 4);
        if (AMConfig.rattlesnakeSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.RATTLESNAKE, MobCategory.CREATURE, AMEntityRegistry.RATTLESNAKE, AMConfig.rattlesnakeSpawnWeight, 1, 2);
        if (AMConfig.endergradeSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.ENDERGRADE, MobCategory.CREATURE, AMEntityRegistry.ENDERGRADE, AMConfig.endergradeSpawnWeight, 2, 6);
        if (AMConfig.hammerheadSharkSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.HAMMERHEAD, MobCategory.WATER_CREATURE, AMEntityRegistry.HAMMERHEAD_SHARK, AMConfig.hammerheadSharkSpawnWeight, 2, 3);
        if (AMConfig.lobsterSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.LOBSTER, MobCategory.WATER_AMBIENT, AMEntityRegistry.LOBSTER, AMConfig.lobsterSpawnWeight, 3, 5);
        if (AMConfig.komodoDragonSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.KOMODO_DRAGON, MobCategory.CREATURE, AMEntityRegistry.KOMODO_DRAGON, AMConfig.komodoDragonSpawnWeight, 1, 2);
        if (AMConfig.capuchinMonkeySpawnWeight > 0) addMobSpawn(AMDefaultBiomes.CAPUCHIN_MONKEY, MobCategory.CREATURE, AMEntityRegistry.CAPUCHIN_MONKEY, AMConfig.capuchinMonkeySpawnWeight, 9, 16);
        if (AMConfig.caveCentipedeSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.CAVES_MONSTER, MobCategory.MONSTER, AMEntityRegistry.CENTIPEDE_HEAD, AMConfig.caveCentipedeSpawnWeight, 1, 1);
        if (AMConfig.warpedToadSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.WARPED_TOAD, MobCategory.CREATURE, AMEntityRegistry.WARPED_TOAD, AMConfig.warpedToadSpawnWeight, 5, 5);
        if (AMConfig.mooseSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.MOOSE, MobCategory.CREATURE, AMEntityRegistry.MOOSE, AMConfig.mooseSpawnWeight, 3, 4);
        if (AMConfig.mimicubeSpawnWeight > 0 && !AMConfig.mimicubeSpawnInEndCity) {
            BiomeModifications.addSpawn(ctx -> ctx.hasTag(AMSpawnTags.Biomes.HAS_MIMICUBE), MobCategory.MONSTER, AMEntityRegistry.MIMICUBE, AMConfig.mimicubeSpawnWeight, 1, 3);
        }
        if (AMConfig.raccoonSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.RACCOON, MobCategory.CREATURE, AMEntityRegistry.RACCOON, AMConfig.raccoonSpawnWeight, 2, 4);
        if (AMConfig.blobfishSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.DEEP_SEA, MobCategory.WATER_AMBIENT, AMEntityRegistry.BLOBFISH, AMConfig.blobfishSpawnWeight, 2, 2);
        if (AMConfig.sealSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.SEAL, MobCategory.CREATURE, AMEntityRegistry.SEAL, AMConfig.sealSpawnWeight, 3, 8);
        if (AMConfig.cockroachSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.COCKROACH, MobCategory.AMBIENT, AMEntityRegistry.COCKROACH, AMConfig.cockroachSpawnWeight, 5, 5);
        if (AMConfig.shoebillSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.SHOEBILL, MobCategory.CREATURE, AMEntityRegistry.SHOEBILL, AMConfig.shoebillSpawnWeight, 1, 2);
        if (AMConfig.elephantSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.ELEPHANT, MobCategory.CREATURE, AMEntityRegistry.ELEPHANT, AMConfig.elephantSpawnWeight, 3, 5);
        if (AMConfig.soulVultureSpawnWeight > 0 && !AMConfig.soulVultureSpawnOnFossil) addMobSpawn(AMDefaultBiomes.SOUL_VULTURE, MobCategory.MONSTER, AMEntityRegistry.SOUL_VULTURE, AMConfig.soulVultureSpawnWeight, 2, 3);
        if (AMConfig.snowLeopardSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.SNOW_LEOPARD, MobCategory.CREATURE, AMEntityRegistry.SNOW_LEOPARD, AMConfig.snowLeopardSpawnWeight, 1, 2);
        if (AMConfig.spectreSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.SPECTRE, MobCategory.CREATURE, AMEntityRegistry.SPECTRE, AMConfig.spectreSpawnWeight, 1, 2);
        if (AMConfig.crowSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.CROW, MobCategory.CREATURE, AMEntityRegistry.CROW, AMConfig.crowSpawnWeight, 3, 5);
        if (AMConfig.alligatorSnappingTurtleSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.ALLIGATOR_SNAPPING_TURTLE, MobCategory.CREATURE, AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE, AMConfig.alligatorSnappingTurtleSpawnWeight, 1, 2);
        if (AMConfig.mungusSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.MUNGUS, MobCategory.CREATURE, AMEntityRegistry.MUNGUS, AMConfig.mungusSpawnWeight, 3, 5);
        if (AMConfig.mantisShrimpSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.MANTIS_SHRIMP, MobCategory.WATER_CREATURE, AMEntityRegistry.MANTIS_SHRIMP, AMConfig.mantisShrimpSpawnWeight, 1, 4);
        if (AMConfig.gusterSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.GUSTER, MobCategory.MONSTER, AMEntityRegistry.GUSTER, AMConfig.gusterSpawnWeight, 1, 2);
        if (AMConfig.straddlerSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.STRADDLER, MobCategory.MONSTER, AMEntityRegistry.STRADDLER, AMConfig.straddlerSpawnWeight, 1, 3);
        if (AMConfig.stradpoleSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.STRADDLER, MobCategory.WATER_CREATURE, AMEntityRegistry.STRADPOLE, AMConfig.stradpoleSpawnWeight, 1, 1);
        if (AMConfig.emuSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.SAVANNA_AND_MESA, MobCategory.CREATURE, AMEntityRegistry.EMU, AMConfig.emuSpawnWeight, 2, 5);
        if (AMConfig.platypusSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.ICE_FREE_RIVER, MobCategory.CREATURE, AMEntityRegistry.PLATYPUS, AMConfig.platypusSpawnWeight, 1, 2);
        if (AMConfig.dropbearSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.DROPBEAR, MobCategory.MONSTER, AMEntityRegistry.DROPBEAR, AMConfig.dropbearSpawnWeight, 1, 1);
        if (AMConfig.tasmanianDevilSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.TASMANIAN_DEVIL, MobCategory.CREATURE, AMEntityRegistry.TASMANIAN_DEVIL, AMConfig.tasmanianDevilSpawnWeight, 1, 2);
        if (AMConfig.kangarooSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.SAVANNA_AND_MESA, MobCategory.CREATURE, AMEntityRegistry.KANGAROO, AMConfig.kangarooSpawnWeight, 3, 5);
        if (AMConfig.cachalotWhaleSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.CACHALOT_WHALE, MobCategory.WATER_CREATURE, AMEntityRegistry.CACHALOT_WHALE, AMConfig.cachalotWhaleSpawnWeight, 1, 2);
        if (AMConfig.enderiophageSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.ENDERIOPHAGE, MobCategory.CREATURE, AMEntityRegistry.ENDERIOPHAGE, AMConfig.enderiophageSpawnWeight, 2, 2);
        if (AMConfig.baldEagleSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.BALD_EAGLE, MobCategory.CREATURE, AMEntityRegistry.BALD_EAGLE, AMConfig.baldEagleSpawnWeight, 2, 4);
        if (AMConfig.tigerSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.TIGER, MobCategory.CREATURE, AMEntityRegistry.TIGER, AMConfig.tigerSpawnWeight, 1, 3);
        if (AMConfig.tarantulaHawkSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.DESERT, MobCategory.CREATURE, AMEntityRegistry.TARANTULA_HAWK, AMConfig.tarantulaHawkSpawnWeight, 1, 1);
        if (AMConfig.frilledSharkSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.DEEP_SEA, MobCategory.WATER_CREATURE, AMEntityRegistry.FRILLED_SHARK, AMConfig.frilledSharkSpawnWeight, 1, 1);
        if (AMConfig.mimicOctopusSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.MIMIC_OCTOPUS, MobCategory.WATER_CREATURE, AMEntityRegistry.MIMIC_OCTOPUS, AMConfig.mimicOctopusSpawnWeight, 1, 2);
        if (AMConfig.seagullSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.SEAGULL, MobCategory.CREATURE, AMEntityRegistry.SEAGULL, AMConfig.seagullSpawnWeight, 3, 6);
        if (AMConfig.froststalkerSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.FROSTSTALKER, MobCategory.CREATURE, AMEntityRegistry.FROSTSTALKER, AMConfig.froststalkerSpawnWeight, 5, 7);
        if (AMConfig.tusklinSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.TUSKLIN, MobCategory.CREATURE, AMEntityRegistry.TUSKLIN, AMConfig.tusklinSpawnWeight, 3, 5);
        if (AMConfig.laviathanSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.ALL_NETHER, MobCategory.CREATURE, AMEntityRegistry.LAVIATHAN, AMConfig.laviathanSpawnWeight, 1, 1);
        if (AMConfig.cosmawSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.COSMAW, MobCategory.CREATURE, AMEntityRegistry.COSMAW, AMConfig.cosmawSpawnWeight, 1, 2);
        if (AMConfig.toucanSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.TOUCAN, MobCategory.CREATURE, AMEntityRegistry.TOUCAN, AMConfig.toucanSpawnWeight, 5, 5);
        if (AMConfig.manedWolfSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.MANED_WOLF, MobCategory.CREATURE, AMEntityRegistry.MANED_WOLF, AMConfig.manedWolfSpawnWeight, 1, 1);
        if (AMConfig.anacondaSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.ANACONDA, MobCategory.CREATURE, AMEntityRegistry.ANACONDA, AMConfig.anacondaSpawnWeight, 1, 1);
        if (AMConfig.anteaterSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.ANTEATER, MobCategory.CREATURE, AMEntityRegistry.ANTEATER, AMConfig.anteaterSpawnWeight, 1, 3);
        if (AMConfig.rockyRollerSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.ROCKY_ROLLER, MobCategory.MONSTER, AMEntityRegistry.ROCKY_ROLLER, AMConfig.rockyRollerSpawnWeight, 1, 1);
        if (AMConfig.flutterSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.FLUTTER, MobCategory.AMBIENT, AMEntityRegistry.FLUTTER, AMConfig.flutterSpawnWeight, 2, 4);
        if (AMConfig.geladaMonkeySpawnWeight > 0) addMobSpawn(AMDefaultBiomes.MEADOWS, MobCategory.CREATURE, AMEntityRegistry.GELADA_MONKEY, AMConfig.geladaMonkeySpawnWeight, 9, 16);
        if (AMConfig.jerboaSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.DESERT, MobCategory.AMBIENT, AMEntityRegistry.JERBOA, AMConfig.jerboaSpawnWeight, 1, 3);
        if (AMConfig.terrapinSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.ICE_FREE_RIVER, MobCategory.WATER_AMBIENT, AMEntityRegistry.TERRAPIN, AMConfig.terrapinSpawnWeight, 1, 2);
        if (AMConfig.combJellySpawnWeight > 0) addMobSpawn(AMDefaultBiomes.COMB_JELLY, MobCategory.WATER_AMBIENT, AMEntityRegistry.COMB_JELLY, AMConfig.combJellySpawnWeight, 2, 3);
        if (AMConfig.cosmicCodSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.COSMIC_COD, MobCategory.AMBIENT, AMEntityRegistry.COSMIC_COD, AMConfig.cosmicCodSpawnWeight, 9, 13);
        if (AMConfig.bunfungusSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.MUNGUS, MobCategory.CREATURE, AMEntityRegistry.BUNFUNGUS, AMConfig.bunfungusSpawnWeight, 1, 1);
        if (AMConfig.bisonSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.BISON, MobCategory.CREATURE, AMEntityRegistry.BISON, AMConfig.bisonSpawnWeight, 6, 10);
        if (AMConfig.giantSquidSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.GIANT_SQUID, MobCategory.WATER_CREATURE, AMEntityRegistry.GIANT_SQUID, AMConfig.giantSquidSpawnWeight, 1, 2);
        if (AMConfig.devilsHolePupfishSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.ALL_OVERWORLD, MobCategory.WATER_AMBIENT, AMEntityRegistry.DEVILS_HOLE_PUPFISH, AMConfig.devilsHolePupfishSpawnWeight, 5, 12);
        if (AMConfig.catfishSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.CATFISH, MobCategory.WATER_AMBIENT, AMEntityRegistry.CATFISH, AMConfig.catfishSpawnWeight, 1, 3);
        if (AMConfig.flyingFishSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.FLYING_FISH, MobCategory.WATER_AMBIENT, AMEntityRegistry.FLYING_FISH, AMConfig.flyingFishSpawnWeight, 3, 6);
        if (AMConfig.skelewagSpawnWeight > 0 && !AMConfig.restrictSkelewagSpawns) addMobSpawn(AMDefaultBiomes.SKELEWAG, MobCategory.MONSTER, AMEntityRegistry.SKELEWAG, AMConfig.skelewagSpawnWeight, 2, 3);
        if (AMConfig.rainFrogSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.DESERT, MobCategory.AMBIENT, AMEntityRegistry.RAIN_FROG, AMConfig.rainFrogSpawnWeight, 1, 3);
        if (AMConfig.potooSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.POTOO, MobCategory.CREATURE, AMEntityRegistry.POTOO, AMConfig.potooSpawnWeight, 1, 1);
        if (AMConfig.mudskipperSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.MANGROVE, MobCategory.CREATURE, AMEntityRegistry.MUDSKIPPER, AMConfig.mudskipperSpawnWeight, 2, 4);
        if (AMConfig.rhinocerosSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.RHINOCEROS, MobCategory.CREATURE, AMEntityRegistry.RHINOCEROS, AMConfig.rhinocerosSpawnWeight, 3, 5);
        if (AMConfig.sugarGliderSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.SUGAR_GLIDER, MobCategory.CREATURE, AMEntityRegistry.SUGAR_GLIDER, AMConfig.sugarGliderSpawnWeight, 2, 4);
        if (AMConfig.farseerSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.FARSEER, MobCategory.MONSTER, AMEntityRegistry.FARSEER, AMConfig.farseerSpawnWeight, 1, 1);
        if (AMConfig.skreecherSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.SKREECHER, MobCategory.MONSTER, AMEntityRegistry.SKREECHER, AMConfig.skreecherSpawnWeight, 1, 1);
        if (AMConfig.underminerSpawnWeight > 0 && !AMConfig.restrictUnderminerSpawns) addMobSpawn(AMDefaultBiomes.CAVES, MobCategory.AMBIENT, AMEntityRegistry.UNDERMINER, AMConfig.underminerSpawnWeight, 1, 1);
        if (AMConfig.murmurSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.CAVES_MONSTER, MobCategory.MONSTER, AMEntityRegistry.MURMUR, AMConfig.murmurSpawnWeight, 1, 1);
        if (AMConfig.skunkSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.SKUNK, MobCategory.CREATURE, AMEntityRegistry.SKUNK, AMConfig.skunkSpawnWeight, 1, 2);
        if (AMConfig.bananaSlugSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.BANANA_SLUG, MobCategory.CREATURE, AMEntityRegistry.BANANA_SLUG, AMConfig.bananaSlugSpawnWeight, 2, 3);
        if (AMConfig.blueJaySpawnWeight > 0) addMobSpawn(AMDefaultBiomes.ALL_FOREST, MobCategory.CREATURE, AMEntityRegistry.BLUE_JAY, AMConfig.blueJaySpawnWeight, 2, 4);
        if (AMConfig.caimanSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.MANGROVE, MobCategory.CREATURE, AMEntityRegistry.CAIMAN, AMConfig.caimanSpawnWeight, 2, 4);
        if (AMConfig.triopsSpawnWeight > 0) addMobSpawn(AMDefaultBiomes.DESERT, MobCategory.WATER_AMBIENT, AMEntityRegistry.TRIOPS, AMConfig.triopsSpawnWeight, 2, 6);
    }

    static void addMobSpawn(AMSpawnBiomeData biomes, MobCategory category, EntityType<?> type, int weight, int min, int max) {
        if (weight <= 0) {
            return;
        }
        BiomeModifications.addSpawn(ctx -> AMSpawnBiomeMatcher.matches(ctx, biomes), category, type, weight, min, max);
    }

    static void addLeafcutterAnthillFeature() {
        if (AMConfig.leafcutterAnthillSpawnChance <= 0) {
            return;
        }
        ResourceKey<net.minecraft.world.level.levelgen.placement.PlacedFeature> anthill = ResourceKey.create(
                Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "leafcutter_anthill"));
        BiomeModifications.create(ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "leafcutter_anthill"))
                .add(ModificationPhase.ADDITIONS, ctx -> AMSpawnBiomeMatcher.matches(ctx, AMDefaultBiomes.LEAFCUTTER_ANTHILL), (ctx, biomeCtx) -> {
                    biomeCtx.getGenerationSettings().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, anthill);
                });
    }

}
