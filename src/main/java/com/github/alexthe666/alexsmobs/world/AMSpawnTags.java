package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

/** Naturalist-style biome spawn tags ({@code alexsmobs:has_*}) for {@link AMSpawnRegistry}. */
public final class AMSpawnTags {
    private AMSpawnTags() {}

    public static final class Biomes {
        public static final TagKey<Biome> HAS_ALLIGATOR_SNAPPING_TURTLE = tag("has_alligator_snapping_turtle");
        public static final TagKey<Biome> HAS_ANACONDA = tag("has_anaconda");
        public static final TagKey<Biome> HAS_ANTEATER = tag("has_anteater");
        public static final TagKey<Biome> HAS_BALD_EAGLE = tag("has_bald_eagle");
        public static final TagKey<Biome> HAS_BANANA_SLUG = tag("has_banana_slug");
        public static final TagKey<Biome> HAS_BISON = tag("has_bison");
        public static final TagKey<Biome> HAS_BLOBFISH = tag("has_blobfish");
        public static final TagKey<Biome> HAS_BLUE_JAY = tag("has_blue_jay");
        public static final TagKey<Biome> HAS_BONE_SERPENT = tag("has_bone_serpent");
        public static final TagKey<Biome> HAS_BUNFUNGUS = tag("has_bunfungus");
        public static final TagKey<Biome> HAS_CACHALOT_WHALE = tag("has_cachalot_whale");
        public static final TagKey<Biome> HAS_CACHALOT_WHALE_BEACHED = tag("has_cachalot_whale_beached");
        public static final TagKey<Biome> HAS_CAIMAN = tag("has_caiman");
        public static final TagKey<Biome> HAS_CAPUCHIN_MONKEY = tag("has_capuchin_monkey");
        public static final TagKey<Biome> HAS_CATFISH = tag("has_catfish");
        public static final TagKey<Biome> HAS_CAVE_CENTIPEDE = tag("has_cave_centipede");
        public static final TagKey<Biome> HAS_COCKROACH = tag("has_cockroach");
        public static final TagKey<Biome> HAS_COMB_JELLY = tag("has_comb_jelly");
        public static final TagKey<Biome> HAS_COSMAW = tag("has_cosmaw");
        public static final TagKey<Biome> HAS_COSMIC_COD = tag("has_cosmic_cod");
        public static final TagKey<Biome> HAS_CRIMSON_MOSQUITO = tag("has_crimson_mosquito");
        public static final TagKey<Biome> HAS_CROCODILE = tag("has_crocodile");
        public static final TagKey<Biome> HAS_CROW = tag("has_crow");
        public static final TagKey<Biome> HAS_DEVILS_HOLE_PUPFISH = tag("has_devils_hole_pupfish");
        public static final TagKey<Biome> HAS_DROPBEAR = tag("has_dropbear");
        public static final TagKey<Biome> HAS_ELEPHANT = tag("has_elephant");
        public static final TagKey<Biome> HAS_EMU = tag("has_emu");
        public static final TagKey<Biome> HAS_ENDERGRADE = tag("has_endergrade");
        public static final TagKey<Biome> HAS_ENDERIOPHAGE = tag("has_enderiophage");
        public static final TagKey<Biome> HAS_FARSEER = tag("has_farseer");
        public static final TagKey<Biome> HAS_FLUTTER = tag("has_flutter");
        public static final TagKey<Biome> HAS_FLY = tag("has_fly");
        public static final TagKey<Biome> HAS_FLYING_FISH = tag("has_flying_fish");
        public static final TagKey<Biome> HAS_FRILLED_SHARK = tag("has_frilled_shark");
        public static final TagKey<Biome> HAS_FROSTSTALKER = tag("has_froststalker");
        public static final TagKey<Biome> HAS_GAZELLE = tag("has_gazelle");
        public static final TagKey<Biome> HAS_GELADA_MONKEY = tag("has_gelada_monkey");
        public static final TagKey<Biome> HAS_GIANT_SQUID = tag("has_giant_squid");
        public static final TagKey<Biome> HAS_GORILLA = tag("has_gorilla");
        public static final TagKey<Biome> HAS_GRIZZLY_BEAR = tag("has_grizzly_bear");
        public static final TagKey<Biome> HAS_GUSTER = tag("has_guster");
        public static final TagKey<Biome> HAS_HAMMERHEAD_SHARK = tag("has_hammerhead_shark");
        public static final TagKey<Biome> HAS_HUMMINGBIRD = tag("has_hummingbird");
        public static final TagKey<Biome> HAS_JERBOA = tag("has_jerboa");
        public static final TagKey<Biome> HAS_KANGAROO = tag("has_kangaroo");
        public static final TagKey<Biome> HAS_KOMODO_DRAGON = tag("has_komodo_dragon");
        public static final TagKey<Biome> HAS_LAVIATHAN = tag("has_laviathan");
        public static final TagKey<Biome> HAS_LEAFCUTTER_ANTHILL = tag("has_leafcutter_anthill");
        public static final TagKey<Biome> HAS_LOBSTER = tag("has_lobster");
        public static final TagKey<Biome> HAS_MANED_WOLF = tag("has_maned_wolf");
        public static final TagKey<Biome> HAS_MANTIS_SHRIMP = tag("has_mantis_shrimp");
        public static final TagKey<Biome> HAS_MIMIC_OCTOPUS = tag("has_mimic_octopus");
        public static final TagKey<Biome> HAS_MIMICUBE = tag("has_mimicube");
        public static final TagKey<Biome> HAS_MOOSE = tag("has_moose");
        public static final TagKey<Biome> HAS_MUDSKIPPER = tag("has_mudskipper");
        public static final TagKey<Biome> HAS_MUNGUS = tag("has_mungus");
        public static final TagKey<Biome> HAS_MURMUR = tag("has_murmur");
        public static final TagKey<Biome> HAS_ORCA = tag("has_orca");
        public static final TagKey<Biome> HAS_PLATYPUS = tag("has_platypus");
        public static final TagKey<Biome> HAS_POTOO = tag("has_potoo");
        public static final TagKey<Biome> HAS_RACCOON = tag("has_raccoon");
        public static final TagKey<Biome> HAS_RAIN_FROG = tag("has_rain_frog");
        public static final TagKey<Biome> HAS_RATTLESNAKE = tag("has_rattlesnake");
        public static final TagKey<Biome> HAS_RHINOCEROS = tag("has_rhinoceros");
        public static final TagKey<Biome> HAS_ROADRUNNER = tag("has_roadrunner");
        public static final TagKey<Biome> HAS_ROCKY_ROLLER = tag("has_rocky_roller");
        public static final TagKey<Biome> HAS_SEAGULL = tag("has_seagull");
        public static final TagKey<Biome> HAS_SEAL = tag("has_seal");
        public static final TagKey<Biome> HAS_SHOEBILL = tag("has_shoebill");
        public static final TagKey<Biome> HAS_SKELEWAG = tag("has_skelewag");
        public static final TagKey<Biome> HAS_SKREECHER = tag("has_skreecher");
        public static final TagKey<Biome> HAS_SKUNK = tag("has_skunk");
        public static final TagKey<Biome> HAS_SNOW_LEOPARD = tag("has_snow_leopard");
        public static final TagKey<Biome> HAS_SOUL_VULTURE = tag("has_soul_vulture");
        public static final TagKey<Biome> HAS_SPECTRE = tag("has_spectre");
        public static final TagKey<Biome> HAS_STRADDLER = tag("has_straddler");
        public static final TagKey<Biome> HAS_STRADPOLE = tag("has_stradpole");
        public static final TagKey<Biome> HAS_SUGAR_GLIDER = tag("has_sugar_glider");
        public static final TagKey<Biome> HAS_SUNBIRD = tag("has_sunbird");
        public static final TagKey<Biome> HAS_TARANTULA_HAWK = tag("has_tarantula_hawk");
        public static final TagKey<Biome> HAS_TASMANIAN_DEVIL = tag("has_tasmanian_devil");
        public static final TagKey<Biome> HAS_TERRAPIN = tag("has_terrapin");
        public static final TagKey<Biome> HAS_TIGER = tag("has_tiger");
        public static final TagKey<Biome> HAS_TOUCAN = tag("has_toucan");
        public static final TagKey<Biome> HAS_TRIOPS = tag("has_triops");
        public static final TagKey<Biome> HAS_TUSKLIN = tag("has_tusklin");
        public static final TagKey<Biome> HAS_UNDERMINER = tag("has_underminer");
        public static final TagKey<Biome> HAS_VOID_WORM = tag("has_void_worm");
        public static final TagKey<Biome> HAS_WARPED_MOSCO = tag("has_warped_mosco");
        public static final TagKey<Biome> HAS_WARPED_TOAD = tag("has_warped_toad");

        private static TagKey<Biome> tag(String name) {
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, name));
        }
    }
}
