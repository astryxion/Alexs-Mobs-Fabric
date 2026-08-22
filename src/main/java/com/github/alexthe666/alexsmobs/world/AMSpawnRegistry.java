package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.config.BiomeConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.citadel.config.biome.SpawnBiomeData;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.GenerationStep;
import org.apache.commons.lang3.tuple.Pair;

/** Fabric spawn registration with Forge-accurate {@link AMSpawnBiomeData} pool matching. */
public final class AMSpawnRegistry {
    private AMSpawnRegistry() {}

    public static void register() {
        addSpawns();
        addLeafcutterAnthillFeature();
        AMSpawnStructureRegistry.register();
    }

    static void addSpawns() {
        if (AMConfig.grizzlyBearSpawnWeight > 0) addMobSpawn(BiomeConfig.grizzlyBear, MobCategory.CREATURE, AMEntityRegistry.GRIZZLY_BEAR, AMConfig.grizzlyBearSpawnWeight, 2, 3);
        if (AMConfig.roadrunnerSpawnWeight > 0) addMobSpawn(BiomeConfig.roadrunner, MobCategory.CREATURE, AMEntityRegistry.ROADRUNNER, AMConfig.roadrunnerSpawnWeight, 2, 2);
        if (AMConfig.boneSerpentSpawnWeight > 0) addMobSpawn(BiomeConfig.boneSerpent, MobCategory.MONSTER, AMEntityRegistry.BONE_SERPENT, AMConfig.boneSerpentSpawnWeight, 1, 1);
        if (AMConfig.gazelleSpawnWeight > 0) addMobSpawn(BiomeConfig.gazelle, MobCategory.CREATURE, AMEntityRegistry.GAZELLE, AMConfig.gazelleSpawnWeight, 7, 7);
        if (AMConfig.crocodileSpawnWeight > 0) addMobSpawn(BiomeConfig.crocodile, MobCategory.CREATURE, AMEntityRegistry.CROCODILE, AMConfig.crocodileSpawnWeight, 1, 2);
        if (AMConfig.flySpawnWeight > 0) addMobSpawn(BiomeConfig.fly, MobCategory.AMBIENT, AMEntityRegistry.FLY, AMConfig.flySpawnWeight, 2, 3);
        if (AMConfig.hummingbirdSpawnWeight > 0) addMobSpawn(BiomeConfig.hummingbird, MobCategory.CREATURE, AMEntityRegistry.HUMMINGBIRD, AMConfig.hummingbirdSpawnWeight, 7, 7);
        if (AMConfig.orcaSpawnWeight > 0) addMobSpawn(BiomeConfig.orca, MobCategory.WATER_CREATURE, AMEntityRegistry.ORCA, AMConfig.orcaSpawnWeight, 3, 4);
        if (AMConfig.sunbirdSpawnWeight > 0) addMobSpawn(BiomeConfig.sunbird, MobCategory.CREATURE, AMEntityRegistry.SUNBIRD, AMConfig.sunbirdSpawnWeight, 1, 1);
        if (AMConfig.gorillaSpawnWeight > 0) addMobSpawn(BiomeConfig.gorilla, MobCategory.CREATURE, AMEntityRegistry.GORILLA, AMConfig.gorillaSpawnWeight, 7, 7);
        if (AMConfig.crimsonMosquitoSpawnWeight > 0) addMobSpawn(BiomeConfig.crimsonMosquito, MobCategory.MONSTER, AMEntityRegistry.CRIMSON_MOSQUITO, AMConfig.crimsonMosquitoSpawnWeight, 4, 4);
        if (AMConfig.rattlesnakeSpawnWeight > 0) addMobSpawn(BiomeConfig.rattlesnake, MobCategory.CREATURE, AMEntityRegistry.RATTLESNAKE, AMConfig.rattlesnakeSpawnWeight, 1, 2);
        if (AMConfig.endergradeSpawnWeight > 0) addMobSpawn(BiomeConfig.endergrade, MobCategory.CREATURE, AMEntityRegistry.ENDERGRADE, AMConfig.endergradeSpawnWeight, 2, 6);
        if (AMConfig.hammerheadSharkSpawnWeight > 0) addMobSpawn(BiomeConfig.hammerheadShark, MobCategory.WATER_CREATURE, AMEntityRegistry.HAMMERHEAD_SHARK, AMConfig.hammerheadSharkSpawnWeight, 2, 3);
        if (AMConfig.lobsterSpawnWeight > 0) addMobSpawn(BiomeConfig.lobster, MobCategory.WATER_AMBIENT, AMEntityRegistry.LOBSTER, AMConfig.lobsterSpawnWeight, 3, 5);
        if (AMConfig.komodoDragonSpawnWeight > 0) addMobSpawn(BiomeConfig.komodoDragon, MobCategory.CREATURE, AMEntityRegistry.KOMODO_DRAGON, AMConfig.komodoDragonSpawnWeight, 1, 2);
        if (AMConfig.capuchinMonkeySpawnWeight > 0) addMobSpawn(BiomeConfig.capuchinMonkey, MobCategory.CREATURE, AMEntityRegistry.CAPUCHIN_MONKEY, AMConfig.capuchinMonkeySpawnWeight, 9, 16);
        if (AMConfig.caveCentipedeSpawnWeight > 0) addMobSpawn(BiomeConfig.caveCentipede, MobCategory.MONSTER, AMEntityRegistry.CENTIPEDE_HEAD, AMConfig.caveCentipedeSpawnWeight, 1, 1);
        if (AMConfig.warpedToadSpawnWeight > 0) addMobSpawn(BiomeConfig.warpedToad, MobCategory.CREATURE, AMEntityRegistry.WARPED_TOAD, AMConfig.warpedToadSpawnWeight, 5, 5);
        if (AMConfig.mooseSpawnWeight > 0) addMobSpawn(BiomeConfig.moose, MobCategory.CREATURE, AMEntityRegistry.MOOSE, AMConfig.mooseSpawnWeight, 3, 4);
        if (AMConfig.mimicubeSpawnWeight > 0 && !AMConfig.mimicubeSpawnInEndCity) {
            BiomeModifications.addSpawn(ctx -> BiomeConfig.test(BiomeConfig.mimicube, ctx.getBiomeRegistryEntry(), ctx.getBiomeKey().location()), MobCategory.MONSTER, AMEntityRegistry.MIMICUBE, AMConfig.mimicubeSpawnWeight, 1, 3);
        }
        if (AMConfig.raccoonSpawnWeight > 0) addMobSpawn(BiomeConfig.raccoon, MobCategory.CREATURE, AMEntityRegistry.RACCOON, AMConfig.raccoonSpawnWeight, 2, 4);
        if (AMConfig.blobfishSpawnWeight > 0) addMobSpawn(BiomeConfig.blobfish, MobCategory.WATER_AMBIENT, AMEntityRegistry.BLOBFISH, AMConfig.blobfishSpawnWeight, 2, 2);
        if (AMConfig.sealSpawnWeight > 0) addMobSpawn(BiomeConfig.seal, MobCategory.CREATURE, AMEntityRegistry.SEAL, AMConfig.sealSpawnWeight, 3, 8);
        if (AMConfig.cockroachSpawnWeight > 0) addMobSpawn(BiomeConfig.cockroach, MobCategory.AMBIENT, AMEntityRegistry.COCKROACH, AMConfig.cockroachSpawnWeight, 5, 5);
        if (AMConfig.shoebillSpawnWeight > 0) addMobSpawn(BiomeConfig.shoebill, MobCategory.CREATURE, AMEntityRegistry.SHOEBILL, AMConfig.shoebillSpawnWeight, 1, 2);
        if (AMConfig.elephantSpawnWeight > 0) addMobSpawn(BiomeConfig.elephant, MobCategory.CREATURE, AMEntityRegistry.ELEPHANT, AMConfig.elephantSpawnWeight, 3, 5);
        if (AMConfig.soulVultureSpawnWeight > 0 && !AMConfig.soulVultureSpawnOnFossil) addMobSpawn(BiomeConfig.soulVulture, MobCategory.MONSTER, AMEntityRegistry.SOUL_VULTURE, AMConfig.soulVultureSpawnWeight, 2, 3);
        if (AMConfig.snowLeopardSpawnWeight > 0) addMobSpawn(BiomeConfig.snowLeopard, MobCategory.CREATURE, AMEntityRegistry.SNOW_LEOPARD, AMConfig.snowLeopardSpawnWeight, 1, 2);
        if (AMConfig.spectreSpawnWeight > 0) addMobSpawn(BiomeConfig.spectre, MobCategory.CREATURE, AMEntityRegistry.SPECTRE, AMConfig.spectreSpawnWeight, 1, 2);
        if (AMConfig.crowSpawnWeight > 0) addMobSpawn(BiomeConfig.crow, MobCategory.CREATURE, AMEntityRegistry.CROW, AMConfig.crowSpawnWeight, 3, 5);
        if (AMConfig.alligatorSnappingTurtleSpawnWeight > 0) addMobSpawn(BiomeConfig.alligatorSnappingTurtle, MobCategory.CREATURE, AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE, AMConfig.alligatorSnappingTurtleSpawnWeight, 1, 2);
        if (AMConfig.mungusSpawnWeight > 0) addMobSpawn(BiomeConfig.mungus, MobCategory.CREATURE, AMEntityRegistry.MUNGUS, AMConfig.mungusSpawnWeight, 3, 5);
        if (AMConfig.mantisShrimpSpawnWeight > 0) addMobSpawn(BiomeConfig.mantisShrimp, MobCategory.WATER_CREATURE, AMEntityRegistry.MANTIS_SHRIMP, AMConfig.mantisShrimpSpawnWeight, 1, 4);
        if (AMConfig.gusterSpawnWeight > 0) addMobSpawn(BiomeConfig.guster, MobCategory.MONSTER, AMEntityRegistry.GUSTER, AMConfig.gusterSpawnWeight, 1, 2);
        if (AMConfig.straddlerSpawnWeight > 0) addMobSpawn(BiomeConfig.straddler, MobCategory.MONSTER, AMEntityRegistry.STRADDLER, AMConfig.straddlerSpawnWeight, 1, 3);
        if (AMConfig.stradpoleSpawnWeight > 0) addMobSpawn(BiomeConfig.stradpole, MobCategory.WATER_CREATURE, AMEntityRegistry.STRADPOLE, AMConfig.stradpoleSpawnWeight, 1, 1);
        if (AMConfig.emuSpawnWeight > 0) addMobSpawn(BiomeConfig.emu, MobCategory.CREATURE, AMEntityRegistry.EMU, AMConfig.emuSpawnWeight, 2, 5);
        if (AMConfig.platypusSpawnWeight > 0) addMobSpawn(BiomeConfig.platypus, MobCategory.CREATURE, AMEntityRegistry.PLATYPUS, AMConfig.platypusSpawnWeight, 1, 2);
        if (AMConfig.dropbearSpawnWeight > 0) addMobSpawn(BiomeConfig.dropbear, MobCategory.MONSTER, AMEntityRegistry.DROPBEAR, AMConfig.dropbearSpawnWeight, 1, 1);
        if (AMConfig.tasmanianDevilSpawnWeight > 0) addMobSpawn(BiomeConfig.tasmanianDevil, MobCategory.CREATURE, AMEntityRegistry.TASMANIAN_DEVIL, AMConfig.tasmanianDevilSpawnWeight, 1, 2);
        if (AMConfig.kangarooSpawnWeight > 0) addMobSpawn(BiomeConfig.kangaroo, MobCategory.CREATURE, AMEntityRegistry.KANGAROO, AMConfig.kangarooSpawnWeight, 3, 5);
        if (AMConfig.cachalotWhaleSpawnWeight > 0) addMobSpawn(BiomeConfig.cachalot_whale_spawns, MobCategory.WATER_CREATURE, AMEntityRegistry.CACHALOT_WHALE, AMConfig.cachalotWhaleSpawnWeight, 1, 2);
        if (AMConfig.enderiophageSpawnWeight > 0) addMobSpawn(BiomeConfig.enderiophage_spawns, MobCategory.CREATURE, AMEntityRegistry.ENDERIOPHAGE, AMConfig.enderiophageSpawnWeight, 2, 2);
        if (AMConfig.baldEagleSpawnWeight > 0) addMobSpawn(BiomeConfig.baldEagle, MobCategory.CREATURE, AMEntityRegistry.BALD_EAGLE, AMConfig.baldEagleSpawnWeight, 2, 4);
        if (AMConfig.tigerSpawnWeight > 0) addMobSpawn(BiomeConfig.tiger, MobCategory.CREATURE, AMEntityRegistry.TIGER, AMConfig.tigerSpawnWeight, 1, 3);
        if (AMConfig.tarantulaHawkSpawnWeight > 0) addMobSpawn(BiomeConfig.tarantula_hawk, MobCategory.CREATURE, AMEntityRegistry.TARANTULA_HAWK, AMConfig.tarantulaHawkSpawnWeight, 1, 1);
        if (AMConfig.frilledSharkSpawnWeight > 0) addMobSpawn(BiomeConfig.frilled_shark, MobCategory.WATER_CREATURE, AMEntityRegistry.FRILLED_SHARK, AMConfig.frilledSharkSpawnWeight, 1, 1);
        if (AMConfig.mimicOctopusSpawnWeight > 0) addMobSpawn(BiomeConfig.mimic_octopus, MobCategory.WATER_CREATURE, AMEntityRegistry.MIMIC_OCTOPUS, AMConfig.mimicOctopusSpawnWeight, 1, 2);
        if (AMConfig.seagullSpawnWeight > 0) addMobSpawn(BiomeConfig.seagull, MobCategory.CREATURE, AMEntityRegistry.SEAGULL, AMConfig.seagullSpawnWeight, 3, 6);
        if (AMConfig.froststalkerSpawnWeight > 0) addMobSpawn(BiomeConfig.froststalker, MobCategory.CREATURE, AMEntityRegistry.FROSTSTALKER, AMConfig.froststalkerSpawnWeight, 5, 7);
        if (AMConfig.tusklinSpawnWeight > 0) addMobSpawn(BiomeConfig.tusklin, MobCategory.CREATURE, AMEntityRegistry.TUSKLIN, AMConfig.tusklinSpawnWeight, 3, 5);
        if (AMConfig.laviathanSpawnWeight > 0) addMobSpawn(BiomeConfig.laviathan, MobCategory.CREATURE, AMEntityRegistry.LAVIATHAN, AMConfig.laviathanSpawnWeight, 1, 1);
        if (AMConfig.cosmawSpawnWeight > 0) addMobSpawn(BiomeConfig.cosmaw, MobCategory.CREATURE, AMEntityRegistry.COSMAW, AMConfig.cosmawSpawnWeight, 1, 2);
        if (AMConfig.toucanSpawnWeight > 0) addMobSpawn(BiomeConfig.toucan, MobCategory.CREATURE, AMEntityRegistry.TOUCAN, AMConfig.toucanSpawnWeight, 5, 5);
        if (AMConfig.manedWolfSpawnWeight > 0) addMobSpawn(BiomeConfig.maned_wolf, MobCategory.CREATURE, AMEntityRegistry.MANED_WOLF, AMConfig.manedWolfSpawnWeight, 1, 1);
        if (AMConfig.anacondaSpawnWeight > 0) addMobSpawn(BiomeConfig.anaconda, MobCategory.CREATURE, AMEntityRegistry.ANACONDA, AMConfig.anacondaSpawnWeight, 1, 1);
        if (AMConfig.anteaterSpawnWeight > 0) addMobSpawn(BiomeConfig.anteater, MobCategory.CREATURE, AMEntityRegistry.ANTEATER, AMConfig.anteaterSpawnWeight, 1, 3);
        if (AMConfig.rockyRollerSpawnWeight > 0) addMobSpawn(BiomeConfig.rocky_roller, MobCategory.MONSTER, AMEntityRegistry.ROCKY_ROLLER, AMConfig.rockyRollerSpawnWeight, 1, 1);
        if (AMConfig.flutterSpawnWeight > 0) addMobSpawn(BiomeConfig.flutter, MobCategory.AMBIENT, AMEntityRegistry.FLUTTER, AMConfig.flutterSpawnWeight, 2, 4);
        if (AMConfig.geladaMonkeySpawnWeight > 0) addMobSpawn(BiomeConfig.gelada_monkey, MobCategory.CREATURE, AMEntityRegistry.GELADA_MONKEY, AMConfig.geladaMonkeySpawnWeight, 9, 16);
        if (AMConfig.jerboaSpawnWeight > 0) addMobSpawn(BiomeConfig.jerboa, MobCategory.AMBIENT, AMEntityRegistry.JERBOA, AMConfig.jerboaSpawnWeight, 1, 3);
        if (AMConfig.terrapinSpawnWeight > 0) addMobSpawn(BiomeConfig.terrapin, MobCategory.WATER_AMBIENT, AMEntityRegistry.TERRAPIN, AMConfig.terrapinSpawnWeight, 1, 2);
        if (AMConfig.combJellySpawnWeight > 0) addMobSpawn(BiomeConfig.comb_jelly, MobCategory.WATER_AMBIENT, AMEntityRegistry.COMB_JELLY, AMConfig.combJellySpawnWeight, 2, 3);
        if (AMConfig.cosmicCodSpawnWeight > 0) addMobSpawn(BiomeConfig.cosmic_cod, MobCategory.AMBIENT, AMEntityRegistry.COSMIC_COD, AMConfig.cosmicCodSpawnWeight, 9, 13);
        if (AMConfig.bunfungusSpawnWeight > 0) addMobSpawn(BiomeConfig.bunfungus, MobCategory.CREATURE, AMEntityRegistry.BUNFUNGUS, AMConfig.bunfungusSpawnWeight, 1, 1);
        if (AMConfig.bisonSpawnWeight > 0) addMobSpawn(BiomeConfig.bison, MobCategory.CREATURE, AMEntityRegistry.BISON, AMConfig.bisonSpawnWeight, 6, 10);
        if (AMConfig.giantSquidSpawnWeight > 0) addMobSpawn(BiomeConfig.giant_squid, MobCategory.WATER_CREATURE, AMEntityRegistry.GIANT_SQUID, AMConfig.giantSquidSpawnWeight, 1, 2);
        if (AMConfig.devilsHolePupfishSpawnWeight > 0) addMobSpawn(BiomeConfig.devils_hole_pupfish, MobCategory.WATER_AMBIENT, AMEntityRegistry.DEVILS_HOLE_PUPFISH, AMConfig.devilsHolePupfishSpawnWeight, 5, 12);
        if (AMConfig.catfishSpawnWeight > 0) addMobSpawn(BiomeConfig.catfish, MobCategory.WATER_AMBIENT, AMEntityRegistry.CATFISH, AMConfig.catfishSpawnWeight, 1, 3);
        if (AMConfig.flyingFishSpawnWeight > 0) addMobSpawn(BiomeConfig.flying_fish, MobCategory.WATER_AMBIENT, AMEntityRegistry.FLYING_FISH, AMConfig.flyingFishSpawnWeight, 3, 6);
        if (AMConfig.skelewagSpawnWeight > 0 && !AMConfig.restrictSkelewagSpawns) addMobSpawn(BiomeConfig.skelewag, MobCategory.MONSTER, AMEntityRegistry.SKELEWAG, AMConfig.skelewagSpawnWeight, 2, 3);
        if (AMConfig.rainFrogSpawnWeight > 0) addMobSpawn(BiomeConfig.rain_frog, MobCategory.AMBIENT, AMEntityRegistry.RAIN_FROG, AMConfig.rainFrogSpawnWeight, 1, 3);
        if (AMConfig.potooSpawnWeight > 0) addMobSpawn(BiomeConfig.potoo, MobCategory.CREATURE, AMEntityRegistry.POTOO, AMConfig.potooSpawnWeight, 1, 1);
        if (AMConfig.mudskipperSpawnWeight > 0) addMobSpawn(BiomeConfig.mudskipper, MobCategory.CREATURE, AMEntityRegistry.MUDSKIPPER, AMConfig.mudskipperSpawnWeight, 2, 4);
        if (AMConfig.rhinocerosSpawnWeight > 0) addMobSpawn(BiomeConfig.rhinoceros, MobCategory.CREATURE, AMEntityRegistry.RHINOCEROS, AMConfig.rhinocerosSpawnWeight, 3, 5);
        if (AMConfig.sugarGliderSpawnWeight > 0) addMobSpawn(BiomeConfig.sugar_glider, MobCategory.CREATURE, AMEntityRegistry.SUGAR_GLIDER, AMConfig.sugarGliderSpawnWeight, 2, 4);
        if (AMConfig.farseerSpawnWeight > 0) addMobSpawn(BiomeConfig.farseer, MobCategory.MONSTER, AMEntityRegistry.FARSEER, AMConfig.farseerSpawnWeight, 1, 1);
        if (AMConfig.skreecherSpawnWeight > 0) addMobSpawn(BiomeConfig.skreecher, MobCategory.MONSTER, AMEntityRegistry.SKREECHER, AMConfig.skreecherSpawnWeight, 1, 1);
        if (AMConfig.underminerSpawnWeight > 0 && !AMConfig.restrictUnderminerSpawns) addMobSpawn(BiomeConfig.underminer, MobCategory.AMBIENT, AMEntityRegistry.UNDERMINER, AMConfig.underminerSpawnWeight, 1, 1);
        if (AMConfig.murmurSpawnWeight > 0) addMobSpawn(BiomeConfig.murmur, MobCategory.MONSTER, AMEntityRegistry.MURMUR, AMConfig.murmurSpawnWeight, 1, 1);
        if (AMConfig.skunkSpawnWeight > 0) addMobSpawn(BiomeConfig.skunk, MobCategory.CREATURE, AMEntityRegistry.SKUNK, AMConfig.skunkSpawnWeight, 1, 2);
        if (AMConfig.bananaSlugSpawnWeight > 0) addMobSpawn(BiomeConfig.banana_slug, MobCategory.CREATURE, AMEntityRegistry.BANANA_SLUG, AMConfig.bananaSlugSpawnWeight, 2, 3);
        if (AMConfig.blueJaySpawnWeight > 0) addMobSpawn(BiomeConfig.blue_jay, MobCategory.CREATURE, AMEntityRegistry.BLUE_JAY, AMConfig.blueJaySpawnWeight, 2, 4);
        if (AMConfig.caimanSpawnWeight > 0) addMobSpawn(BiomeConfig.caiman, MobCategory.CREATURE, AMEntityRegistry.CAIMAN, AMConfig.caimanSpawnWeight, 2, 4);
        if (AMConfig.triopsSpawnWeight > 0) addMobSpawn(BiomeConfig.triops, MobCategory.WATER_AMBIENT, AMEntityRegistry.TRIOPS, AMConfig.triopsSpawnWeight, 2, 6);
    }

    static void addMobSpawn(Pair<String, SpawnBiomeData> biomes, MobCategory category, EntityType<?> type, int weight, int min, int max) {
        if (weight <= 0) {
            return;
        }
        BiomeModifications.addSpawn(ctx -> BiomeConfig.test(biomes, ctx.getBiomeRegistryEntry(), ctx.getBiomeKey().location()), category, type, weight, min, max);
    }

    static void addLeafcutterAnthillFeature() {
        if (AMConfig.leafcutterAnthillSpawnChance <= 0) {
            return;
        }
        ResourceKey<net.minecraft.world.level.levelgen.placement.PlacedFeature> anthill = ResourceKey.create(
                Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "leafcutter_anthill"));
        BiomeModifications.create(ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "leafcutter_anthill"))
                .add(ModificationPhase.ADDITIONS, ctx -> BiomeConfig.test(BiomeConfig.leafcutter_anthill_spawns, ctx.getBiomeRegistryEntry(), ctx.getBiomeKey().location()), (ctx, biomeCtx) -> {
                    biomeCtx.getGenerationSettings().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, anthill);
                });
    }

}
