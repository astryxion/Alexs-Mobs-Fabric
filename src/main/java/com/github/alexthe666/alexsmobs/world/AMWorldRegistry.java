package com.github.alexthe666.alexsmobs.world;

import net.minecraft.util.valueproviders.UniformInt;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.config.BiomeConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.config.biome.SpawnBiomeData;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.core.registries.Registries;
import org.apache.commons.lang3.tuple.Pair;

// @Mod.EventBusSubscriber removed - use direct registration(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMWorldRegistry {

    private static final ResourceKey<PlacedFeature> LEAFCUTTER_ANTHILL_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "leafcutter_anthill"));

    private static Identifier getBiomeName(Holder<Biome> biome) {
        return biome.unwrap().map((resourceKey) -> resourceKey.identifier(), (noKey) -> null);
    }

    public static boolean testBiome(Pair<String, SpawnBiomeData> entry, Holder<Biome> biome) {
        boolean result = false;
        try {
            result = BiomeConfig.test(entry, biome, getBiomeName(biome));
        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("could not test biome config for " + entry.getLeft() + ", defaulting to no spawns for mob");
            result = false;
        }
        return result;
    }

    public static void addBiomeSpawns(Holder<Biome> biome, BiomeModificationContext.MobSpawnSettingsContext builder) {
        if (testBiome(BiomeConfig.grizzlyBear, biome) && AMConfig.grizzlyBearSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.GRIZZLY_BEAR, UniformInt.of(2, 3)), AMConfig.grizzlyBearSpawnWeight);
        }
        if (testBiome(BiomeConfig.roadrunner, biome) && AMConfig.roadrunnerSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.ROADRUNNER, UniformInt.of(2, 2)), AMConfig.roadrunnerSpawnWeight);
        }
        if (testBiome(BiomeConfig.boneSerpent, biome) && AMConfig.boneSerpentSpawnWeight > 0) {
            builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.BONE_SERPENT, UniformInt.of(1, 1)), AMConfig.boneSerpentSpawnWeight);
        }
        if (testBiome(BiomeConfig.gazelle, biome) && AMConfig.gazelleSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.GAZELLE, UniformInt.of(7, 7)), AMConfig.gazelleSpawnWeight);
        }
        if (testBiome(BiomeConfig.crocodile, biome) && AMConfig.crocodileSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.CROCODILE, UniformInt.of(1, 2)), AMConfig.crocodileSpawnWeight);
        }
        if (testBiome(BiomeConfig.fly, biome) && AMConfig.flySpawnWeight > 0) {
            builder.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(AMEntityRegistry.FLY, UniformInt.of(2, 3)), AMConfig.flySpawnWeight);
        }
        if (testBiome(BiomeConfig.hummingbird, biome) && AMConfig.hummingbirdSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.HUMMINGBIRD, UniformInt.of(7, 7)), AMConfig.hummingbirdSpawnWeight);
        }
        if (testBiome(BiomeConfig.orca, biome) && AMConfig.orcaSpawnWeight > 0) {
            builder.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.ORCA, UniformInt.of(3, 4)), AMConfig.orcaSpawnWeight);
        }
        if (testBiome(BiomeConfig.sunbird, biome) && AMConfig.sunbirdSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.SUNBIRD, UniformInt.of(1, 1)), AMConfig.sunbirdSpawnWeight);
        }
        if (testBiome(BiomeConfig.gorilla, biome) && AMConfig.gorillaSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.GORILLA, UniformInt.of(7, 7)), AMConfig.gorillaSpawnWeight);
        }
        if (testBiome(BiomeConfig.crimsonMosquito, biome) && AMConfig.crimsonMosquitoSpawnWeight > 0) {
            builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.CRIMSON_MOSQUITO, UniformInt.of(4, 4)), AMConfig.crimsonMosquitoSpawnWeight);
        }
        if (testBiome(BiomeConfig.rattlesnake, biome) && AMConfig.rattlesnakeSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.RATTLESNAKE, UniformInt.of(1, 2)), AMConfig.rattlesnakeSpawnWeight);
        }
        if (testBiome(BiomeConfig.endergrade, biome) && AMConfig.endergradeSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.ENDERGRADE, UniformInt.of(2, 6)), AMConfig.endergradeSpawnWeight);
        }
        if (testBiome(BiomeConfig.hammerheadShark, biome) && AMConfig.hammerheadSharkSpawnWeight > 0) {
            builder.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.HAMMERHEAD_SHARK, UniformInt.of(2, 3)), AMConfig.hammerheadSharkSpawnWeight);
        }
        if (testBiome(BiomeConfig.lobster, biome) && AMConfig.lobsterSpawnWeight > 0) {
            builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(AMEntityRegistry.LOBSTER, UniformInt.of(3, 5)), AMConfig.lobsterSpawnWeight);
        }
        if (testBiome(BiomeConfig.komodoDragon, biome) && AMConfig.komodoDragonSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.KOMODO_DRAGON, UniformInt.of(1, 2)), AMConfig.komodoDragonSpawnWeight);
        }
        if (testBiome(BiomeConfig.capuchinMonkey, biome) && AMConfig.capuchinMonkeySpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.CAPUCHIN_MONKEY, UniformInt.of(9, 16)), AMConfig.capuchinMonkeySpawnWeight);
        }
        if (testBiome(BiomeConfig.caveCentipede, biome) && AMConfig.caveCentipedeSpawnWeight > 0) {
            builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.CENTIPEDE_HEAD, UniformInt.of(1, 1)), AMConfig.caveCentipedeSpawnWeight);
        }
        if (testBiome(BiomeConfig.warpedToad, biome) && AMConfig.warpedToadSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.WARPED_TOAD, UniformInt.of(5, 5)), AMConfig.warpedToadSpawnWeight);
        }
        if (testBiome(BiomeConfig.moose, biome) && AMConfig.mooseSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.MOOSE, UniformInt.of(3, 4)), AMConfig.mooseSpawnWeight);
        }
        if (testBiome(BiomeConfig.mimicube, biome) && AMConfig.mimicubeSpawnWeight > 0 && !AMConfig.mimicubeSpawnInEndCity) {
            builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.MIMICUBE, UniformInt.of(1, 3)), AMConfig.mimicubeSpawnWeight);
        }
        if (testBiome(BiomeConfig.raccoon, biome) && AMConfig.raccoonSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.RACCOON, UniformInt.of(2, 4)), AMConfig.raccoonSpawnWeight);
        }
        if (testBiome(BiomeConfig.blobfish, biome) && AMConfig.blobfishSpawnWeight > 0) {
            builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(AMEntityRegistry.BLOBFISH, UniformInt.of(2, 2)), AMConfig.blobfishSpawnWeight);
        }
        if (testBiome(BiomeConfig.seal, biome) && AMConfig.sealSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.SEAL, UniformInt.of(3, 8)), AMConfig.sealSpawnWeight);
        }
        if (testBiome(BiomeConfig.cockroach, biome) && AMConfig.cockroachSpawnWeight > 0) {
            builder.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(AMEntityRegistry.COCKROACH, UniformInt.of(5, 5)), AMConfig.cockroachSpawnWeight);
        }
        if (testBiome(BiomeConfig.shoebill, biome) && AMConfig.shoebillSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.SHOEBILL, UniformInt.of(1, 2)), AMConfig.shoebillSpawnWeight);
        }
        if (testBiome(BiomeConfig.elephant, biome) && AMConfig.elephantSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.ELEPHANT, UniformInt.of(3, 5)), AMConfig.elephantSpawnWeight);
        }
        if (testBiome(BiomeConfig.soulVulture, biome) && AMConfig.soulVultureSpawnWeight > 0 && !AMConfig.soulVultureSpawnOnFossil) {
            builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.SOUL_VULTURE, UniformInt.of(2, 3)), AMConfig.soulVultureSpawnWeight);
        }
        if (testBiome(BiomeConfig.snowLeopard, biome) && AMConfig.snowLeopardSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.SNOW_LEOPARD, UniformInt.of(1, 2)), AMConfig.snowLeopardSpawnWeight);
        }
        if (testBiome(BiomeConfig.spectre, biome) && AMConfig.spectreSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.SPECTRE, UniformInt.of(1, 2)), AMConfig.spectreSpawnWeight);
        }
        if (testBiome(BiomeConfig.crow, biome) && AMConfig.crowSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.CROW, UniformInt.of(3, 5)), AMConfig.crowSpawnWeight);
        }
        if (testBiome(BiomeConfig.alligatorSnappingTurtle, biome) && AMConfig.alligatorSnappingTurtleSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE, UniformInt.of(1, 2)), AMConfig.alligatorSnappingTurtleSpawnWeight);
        }
        if (testBiome(BiomeConfig.mungus, biome) && AMConfig.mungusSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.MUNGUS, UniformInt.of(3, 5)), AMConfig.mungusSpawnWeight);
        }
        if (testBiome(BiomeConfig.mantisShrimp, biome) && AMConfig.mantisShrimpSpawnWeight > 0) {
            builder.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.MANTIS_SHRIMP, UniformInt.of(1, 4)), AMConfig.mantisShrimpSpawnWeight);
        }
        if (testBiome(BiomeConfig.guster, biome) && AMConfig.gusterSpawnWeight > 0) {
            builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.GUSTER, UniformInt.of(1, 2)), AMConfig.gusterSpawnWeight);
        }
        if (testBiome(BiomeConfig.warpedMosco, biome) && AMConfig.warpedMoscoSpawnWeight > 0) {
            builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.WARPED_MOSCO, UniformInt.of(1, 1)), AMConfig.warpedMoscoSpawnWeight);
        }
        if (testBiome(BiomeConfig.straddler, biome) && AMConfig.straddlerSpawnWeight > 0) {
            builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.STRADDLER, UniformInt.of(1, 3)), AMConfig.straddlerSpawnWeight);
        }
        if (testBiome(BiomeConfig.stradpole, biome) && AMConfig.stradpoleSpawnWeight > 0) {
            builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(AMEntityRegistry.STRADPOLE, UniformInt.of(1, 1)), AMConfig.stradpoleSpawnWeight);
        }
        if (testBiome(BiomeConfig.emu, biome) && AMConfig.emuSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.EMU, UniformInt.of(2, 5)), AMConfig.emuSpawnWeight);
        }
        if (testBiome(BiomeConfig.platypus, biome) && AMConfig.platypusSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.PLATYPUS, UniformInt.of(1, 2)), AMConfig.platypusSpawnWeight);
        }
        if (testBiome(BiomeConfig.dropbear, biome) && AMConfig.dropbearSpawnWeight > 0) {
            builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.DROPBEAR, UniformInt.of(1, 1)), AMConfig.dropbearSpawnWeight);
        }
        if (testBiome(BiomeConfig.tasmanianDevil, biome) && AMConfig.tasmanianDevilSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.TASMANIAN_DEVIL, UniformInt.of(1, 2)), AMConfig.tasmanianDevilSpawnWeight);
        }
        if (testBiome(BiomeConfig.kangaroo, biome) && AMConfig.kangarooSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.KANGAROO, UniformInt.of(3, 5)), AMConfig.kangarooSpawnWeight);
        }
        if (testBiome(BiomeConfig.cachalot_whale_spawns, biome) && AMConfig.cachalotWhaleSpawnWeight > 0) {
            builder.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.CACHALOT_WHALE, UniformInt.of(1, 2)), AMConfig.cachalotWhaleSpawnWeight);
        }
        if (testBiome(BiomeConfig.enderiophage_spawns, biome) && AMConfig.enderiophageSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.ENDERIOPHAGE, UniformInt.of(2, 2)), AMConfig.enderiophageSpawnWeight);
        }
        if (testBiome(BiomeConfig.baldEagle, biome) && AMConfig.baldEagleSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.BALD_EAGLE, UniformInt.of(2, 4)), AMConfig.baldEagleSpawnWeight);
        }
        if (testBiome(BiomeConfig.tiger, biome) && AMConfig.tigerSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.TIGER, UniformInt.of(1, 3)), AMConfig.tigerSpawnWeight);
        }
        if (testBiome(BiomeConfig.tarantula_hawk, biome) && AMConfig.tarantulaHawkSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.TARANTULA_HAWK, UniformInt.of(1, 1)), AMConfig.tarantulaHawkSpawnWeight);
        }
        if (testBiome(BiomeConfig.void_worm, biome) && AMConfig.voidWormSpawnWeight > 0) {
            builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.VOID_WORM, UniformInt.of(1, 1)), AMConfig.voidWormSpawnWeight);
        }
        if (testBiome(BiomeConfig.frilled_shark, biome) && AMConfig.frilledSharkSpawnWeight > 0) {
            builder.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.FRILLED_SHARK, UniformInt.of(1, 1)), AMConfig.frilledSharkSpawnWeight);
        }
        if (testBiome(BiomeConfig.mimic_octopus, biome) && AMConfig.mimicOctopusSpawnWeight > 0) {
            builder.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.MIMIC_OCTOPUS, UniformInt.of(1, 2)), AMConfig.mimicOctopusSpawnWeight);
        }
        if (testBiome(BiomeConfig.seagull, biome) && AMConfig.seagullSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.SEAGULL, UniformInt.of(3, 6)), AMConfig.seagullSpawnWeight);
        }
        if (testBiome(BiomeConfig.froststalker, biome) && AMConfig.froststalkerSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.FROSTSTALKER, UniformInt.of(5, 7)), AMConfig.froststalkerSpawnWeight);
        }
        if (testBiome(BiomeConfig.tusklin, biome) && AMConfig.tusklinSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.TUSKLIN, UniformInt.of(3, 5)), AMConfig.tusklinSpawnWeight);
        }
        if (testBiome(BiomeConfig.laviathan, biome) && AMConfig.laviathanSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.LAVIATHAN, UniformInt.of(1, 1)), AMConfig.laviathanSpawnWeight);
        }
        if (testBiome(BiomeConfig.cosmaw, biome) && AMConfig.cosmawSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.COSMAW, UniformInt.of(1, 2)), AMConfig.cosmawSpawnWeight);
        }
        if (testBiome(BiomeConfig.toucan, biome) && AMConfig.toucanSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.TOUCAN, UniformInt.of(5, 5)), AMConfig.toucanSpawnWeight);
        }
        if (testBiome(BiomeConfig.maned_wolf, biome) && AMConfig.manedWolfSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.MANED_WOLF, UniformInt.of(1, 1)), AMConfig.manedWolfSpawnWeight);
        }
        if (testBiome(BiomeConfig.anaconda, biome) && AMConfig.anacondaSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.ANACONDA, UniformInt.of(1, 1)), AMConfig.anacondaSpawnWeight);
        }
        if (testBiome(BiomeConfig.anteater, biome) && AMConfig.anteaterSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.ANTEATER, UniformInt.of(1, 3)), AMConfig.anteaterSpawnWeight);
        }
        if (testBiome(BiomeConfig.rocky_roller, biome) && AMConfig.rockyRollerSpawnWeight > 0) {
            builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.ROCKY_ROLLER, UniformInt.of(1, 1)), AMConfig.rockyRollerSpawnWeight);
        }
        if (testBiome(BiomeConfig.flutter, biome) && AMConfig.flutterSpawnWeight > 0) {
            builder.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(AMEntityRegistry.FLUTTER, UniformInt.of(2, 4)), AMConfig.flutterSpawnWeight);
        }
        if (testBiome(BiomeConfig.gelada_monkey, biome) && AMConfig.geladaMonkeySpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.GELADA_MONKEY, UniformInt.of(9, 16)), AMConfig.geladaMonkeySpawnWeight);
        }
        if (testBiome(BiomeConfig.jerboa, biome) && AMConfig.jerboaSpawnWeight > 0) {
            builder.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(AMEntityRegistry.JERBOA, UniformInt.of(1, 3)), AMConfig.jerboaSpawnWeight);
        }
        if (testBiome(BiomeConfig.terrapin, biome) && AMConfig.terrapinSpawnWeight > 0) {
            builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(AMEntityRegistry.TERRAPIN, UniformInt.of(1, 2)), AMConfig.terrapinSpawnWeight);
        }
        if (testBiome(BiomeConfig.comb_jelly, biome) && AMConfig.combJellySpawnWeight > 0) {
            builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(AMEntityRegistry.COMB_JELLY, UniformInt.of(2, 3)), AMConfig.combJellySpawnWeight);
        }
        if (testBiome(BiomeConfig.cosmic_cod, biome) && AMConfig.cosmicCodSpawnWeight > 0) {
            builder.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(AMEntityRegistry.COSMIC_COD, UniformInt.of(9, 13)), AMConfig.cosmicCodSpawnWeight);
        }
        if (testBiome(BiomeConfig.bunfungus, biome) && AMConfig.bunfungusSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.BUNFUNGUS, UniformInt.of(1, 1)), AMConfig.bunfungusSpawnWeight);
        }
        if (testBiome(BiomeConfig.bison, biome) && AMConfig.bisonSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.BISON, UniformInt.of(6, 10)), AMConfig.bisonSpawnWeight);
        }
        if (testBiome(BiomeConfig.giant_squid, biome) && AMConfig.giantSquidSpawnWeight > 0) {
            builder.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.GIANT_SQUID, UniformInt.of(1, 2)), AMConfig.giantSquidSpawnWeight);
        }
        if (testBiome(BiomeConfig.devils_hole_pupfish, biome) && AMConfig.devilsHolePupfishSpawnWeight > 0) {
            builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(AMEntityRegistry.DEVILS_HOLE_PUPFISH, UniformInt.of(5, 12)), AMConfig.devilsHolePupfishSpawnWeight);
        }
        if (testBiome(BiomeConfig.catfish, biome) && AMConfig.catfishSpawnWeight > 0) {
            builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(AMEntityRegistry.CATFISH, UniformInt.of(1, 3)), AMConfig.catfishSpawnWeight);
        }
        if (testBiome(BiomeConfig.flying_fish, biome) && AMConfig.flyingFishSpawnWeight > 0) {
            builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(AMEntityRegistry.FLYING_FISH, UniformInt.of(3, 6)), AMConfig.flyingFishSpawnWeight);
        }
        if (testBiome(BiomeConfig.skelewag, biome) && AMConfig.skelewagSpawnWeight > 0 && !AMConfig.restrictSkelewagSpawns) {
            builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.SKELEWAG, UniformInt.of(2, 3)), AMConfig.skelewagSpawnWeight);
        }
        if (testBiome(BiomeConfig.rain_frog, biome) && AMConfig.rainFrogSpawnWeight > 0) {
            builder.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(AMEntityRegistry.RAIN_FROG, UniformInt.of(1, 3)), AMConfig.rainFrogSpawnWeight);
        }
        if (testBiome(BiomeConfig.potoo, biome) && AMConfig.potooSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.POTOO, UniformInt.of(1, 1)), AMConfig.potooSpawnWeight);
        }
        if (testBiome(BiomeConfig.mudskipper, biome) && AMConfig.mudskipperSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.MUDSKIPPER, UniformInt.of(2, 4)), AMConfig.mudskipperSpawnWeight);
        }
        if (testBiome(BiomeConfig.rhinoceros, biome) && AMConfig.rhinocerosSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.RHINOCEROS, UniformInt.of(3, 5)), AMConfig.rhinocerosSpawnWeight);
        }
        if (testBiome(BiomeConfig.sugar_glider, biome) && AMConfig.sugarGliderSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.SUGAR_GLIDER, UniformInt.of(2, 4)), AMConfig.sugarGliderSpawnWeight);
        }
        if (testBiome(BiomeConfig.farseer, biome) && AMConfig.farseerSpawnWeight > 0) {
            builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.FARSEER, UniformInt.of(1, 1)), AMConfig.farseerSpawnWeight);
        }
        if (testBiome(BiomeConfig.skreecher, biome) && AMConfig.skreecherSpawnWeight > 0) {
            builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.SKREECHER, UniformInt.of(1, 1)), AMConfig.skreecherSpawnWeight);
        }
        if (testBiome(BiomeConfig.underminer, biome) && AMConfig.underminerSpawnWeight > 0 && !AMConfig.restrictUnderminerSpawns) {
            builder.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(AMEntityRegistry.UNDERMINER, UniformInt.of(1, 1)), AMConfig.underminerSpawnWeight);
        }
        if (testBiome(BiomeConfig.murmur, biome) && AMConfig.murmurSpawnWeight > 0) {
            builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.MURMUR, UniformInt.of(1, 1)), AMConfig.murmurSpawnWeight);
        }
        if (testBiome(BiomeConfig.skunk, biome) && AMConfig.skunkSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.SKUNK, UniformInt.of(1, 2)), AMConfig.skunkSpawnWeight);
        }
        if (testBiome(BiomeConfig.banana_slug, biome) && AMConfig.bananaSlugSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.BANANA_SLUG, UniformInt.of(2, 3)), AMConfig.bananaSlugSpawnWeight);
        }
        if (testBiome(BiomeConfig.blue_jay, biome) && AMConfig.blueJaySpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.BLUE_JAY, UniformInt.of(2, 4)), AMConfig.blueJaySpawnWeight);
        }
        if (testBiome(BiomeConfig.caiman, biome) && AMConfig.caimanSpawnWeight > 0) {
            builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(AMEntityRegistry.CAIMAN, UniformInt.of(2, 4)), AMConfig.caimanSpawnWeight);
        }
        if (testBiome(BiomeConfig.triops, biome) && AMConfig.triopsSpawnWeight > 0) {
            builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(AMEntityRegistry.TRIOPS, UniformInt.of(2, 6)), AMConfig.triopsSpawnWeight);
        }
    }

    public static void addLeafcutterAntSpawns(Holder<Biome> biome, BiomeModificationContext.GenerationSettingsContext builder) {
        if (testBiome(BiomeConfig.leafcutter_anthill_spawns, biome) && AMConfig.leafcutterAnthillSpawnChance > 0) {
            builder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, LEAFCUTTER_ANTHILL_PLACED);
        }
    }

    public static void init() {
        BiomeModifications.create(Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "am_biome_spawns"))
                .add(net.fabricmc.fabric.api.biome.v1.ModificationPhase.ADDITIONS, ctx -> true,
                        (selection, ctx) -> addBiomeSpawns(selection.getBiomeHolder(), ctx.getMobSpawnSettings()));
        BiomeModifications.create(Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "am_leafcutter_anthill"))
                .add(net.fabricmc.fabric.api.biome.v1.ModificationPhase.ADDITIONS, ctx -> true,
                        (selection, ctx) -> addLeafcutterAntSpawns(selection.getBiomeHolder(), ctx.getGenerationSettings()));
        BiomeModifications.addSpawn(ctx -> AMConfig.mimicubeSpawnInEndCity && ctx.validForStructure(BuiltinStructures.END_CITY),
                MobCategory.MONSTER, AMEntityRegistry.MIMICUBE, AMConfig.mimicubeSpawnWeight, 1, 3);
        BiomeModifications.addSpawn(ctx -> AMConfig.soulVultureSpawnOnFossil && ctx.validForStructure(BuiltinStructures.NETHER_FOSSIL),
                MobCategory.MONSTER, AMEntityRegistry.SOUL_VULTURE, AMConfig.soulVultureSpawnWeight, 1, 1);
        BiomeModifications.addSpawn(ctx -> AMConfig.restrictSkelewagSpawns && ctx.validForStructure(BuiltinStructures.SHIPWRECK),
                MobCategory.MONSTER, AMEntityRegistry.SKELEWAG, AMConfig.skelewagSpawnWeight, 1, 2);
        BiomeModifications.addSpawn(
                ctx -> AMConfig.restrictUnderminerSpawns
                        && (ctx.validForStructure(BuiltinStructures.MINESHAFT)
                        || ctx.validForStructure(BuiltinStructures.MINESHAFT_MESA)),
                MobCategory.AMBIENT, AMEntityRegistry.UNDERMINER, AMConfig.underminerSpawnWeight, 1, 1);
    }
}
