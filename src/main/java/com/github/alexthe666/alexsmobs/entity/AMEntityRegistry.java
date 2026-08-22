package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.google.common.base.Predicates;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;

import java.util.function.Predicate;

public class AMEntityRegistry {

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, path);
    }

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id(name));
        @SuppressWarnings("unchecked")
        EntityType<T> type = (EntityType<T>) builder.build(id(name).toString());
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type);
    }

    public static EntityType<EntityGrizzlyBear> GRIZZLY_BEAR;
    public static EntityType<EntityRoadrunner> ROADRUNNER;
    public static EntityType<EntityBoneSerpent> BONE_SERPENT;
    public static EntityType<EntityBoneSerpentPart> BONE_SERPENT_PART;
    public static EntityType<EntityGazelle> GAZELLE;
    public static EntityType<EntityCrocodile> CROCODILE;
    public static EntityType<EntityFly> FLY;
    public static EntityType<EntityHummingbird> HUMMINGBIRD;
    public static EntityType<EntityOrca> ORCA;
    public static EntityType<EntitySunbird> SUNBIRD;
    public static EntityType<EntityGorilla> GORILLA;
    public static EntityType<EntityCrimsonMosquito> CRIMSON_MOSQUITO;
    public static EntityType<EntityMosquitoSpit> MOSQUITO_SPIT;
    public static EntityType<EntityRattlesnake> RATTLESNAKE;
    public static EntityType<EntityEndergrade> ENDERGRADE;
    public static EntityType<EntityHammerheadShark> HAMMERHEAD_SHARK;
    public static EntityType<EntitySharkToothArrow> SHARK_TOOTH_ARROW;
    public static EntityType<EntityLobster> LOBSTER;
    public static EntityType<EntityKomodoDragon> KOMODO_DRAGON;
    public static EntityType<EntityCapuchinMonkey> CAPUCHIN_MONKEY;
    public static EntityType<EntityTossedItem> TOSSED_ITEM;
    public static EntityType<EntityCentipedeHead> CENTIPEDE_HEAD;
    public static EntityType<EntityCentipedeBody> CENTIPEDE_BODY;
    public static EntityType<EntityCentipedeTail> CENTIPEDE_TAIL;
    public static EntityType<EntityWarpedToad> WARPED_TOAD;
    public static EntityType<EntityMoose> MOOSE;
    public static EntityType<EntityMimicube> MIMICUBE;
    public static EntityType<EntityRaccoon> RACCOON;
    public static EntityType<EntityBlobfish> BLOBFISH;
    public static EntityType<EntitySeal> SEAL;
    public static EntityType<EntityCockroach> COCKROACH;
    public static EntityType<EntityCockroachEgg> COCKROACH_EGG;
    public static EntityType<EntityShoebill> SHOEBILL;
    public static EntityType<EntityElephant> ELEPHANT;
    public static EntityType<EntitySoulVulture> SOUL_VULTURE;
    public static EntityType<EntitySnowLeopard> SNOW_LEOPARD;
    public static EntityType<EntitySpectre> SPECTRE;
    public static EntityType<EntityCrow> CROW;
    public static EntityType<EntityAlligatorSnappingTurtle> ALLIGATOR_SNAPPING_TURTLE;
    public static EntityType<EntityMungus> MUNGUS;
    public static EntityType<EntityMantisShrimp> MANTIS_SHRIMP;
    public static EntityType<EntityGuster> GUSTER;
    public static EntityType<EntitySandShot> SAND_SHOT;
    public static EntityType<EntityGust> GUST;
    public static EntityType<EntityWarpedMosco> WARPED_MOSCO;
    public static EntityType<EntityHemolymph> HEMOLYMPH;
    public static EntityType<EntityStraddler> STRADDLER;
    public static EntityType<EntityStradpole> STRADPOLE;
    public static EntityType<EntityStraddleboard> STRADDLEBOARD;
    public static EntityType<EntityEmu> EMU;
    public static EntityType<EntityEmuEgg> EMU_EGG;
    public static EntityType<EntityPlatypus> PLATYPUS;
    public static EntityType<EntityDropBear> DROPBEAR;
    public static EntityType<EntityTasmanianDevil> TASMANIAN_DEVIL;
    public static EntityType<EntityKangaroo> KANGAROO;
    public static EntityType<EntityCachalotWhale> CACHALOT_WHALE;
    public static EntityType<EntityCachalotEcho> CACHALOT_ECHO;
    public static EntityType<EntityLeafcutterAnt> LEAFCUTTER_ANT;
    public static EntityType<EntityEnderiophage> ENDERIOPHAGE;
    public static EntityType<EntityEnderiophageRocket> ENDERIOPHAGE_ROCKET;
    public static EntityType<EntityBaldEagle> BALD_EAGLE;
    public static EntityType<EntityTiger> TIGER;
    public static EntityType<EntityTarantulaHawk> TARANTULA_HAWK;
    public static EntityType<EntityVoidWorm> VOID_WORM;
    public static EntityType<EntityVoidWormPart> VOID_WORM_PART;
    public static EntityType<EntityVoidWormShot> VOID_WORM_SHOT;
    public static EntityType<EntityVoidPortal> VOID_PORTAL;
    public static EntityType<EntityFrilledShark> FRILLED_SHARK;
    public static EntityType<EntityMimicOctopus> MIMIC_OCTOPUS;
    public static EntityType<EntitySeagull> SEAGULL;
    public static EntityType<EntityFroststalker> FROSTSTALKER;
    public static EntityType<EntityIceShard> ICE_SHARD;
    public static EntityType<EntityTusklin> TUSKLIN;
    public static EntityType<EntityLaviathan> LAVIATHAN;
    public static EntityType<EntityCosmaw> COSMAW;
    public static EntityType<EntityToucan> TOUCAN;
    public static EntityType<EntityManedWolf> MANED_WOLF;
    public static EntityType<EntityAnaconda> ANACONDA;
    public static EntityType<EntityAnacondaPart> ANACONDA_PART;
    public static EntityType<EntityVineLasso> VINE_LASSO;
    public static EntityType<EntityAnteater> ANTEATER;
    public static EntityType<EntityRockyRoller> ROCKY_ROLLER;
    public static EntityType<EntityFlutter> FLUTTER;
    public static EntityType<EntityPollenBall> POLLEN_BALL;
    public static EntityType<EntityGeladaMonkey> GELADA_MONKEY;
    public static EntityType<EntityJerboa> JERBOA;
    public static EntityType<EntityTerrapin> TERRAPIN;
    public static EntityType<EntityCombJelly> COMB_JELLY;
    public static EntityType<EntityCosmicCod> COSMIC_COD;
    public static EntityType<EntityBunfungus> BUNFUNGUS;
    public static EntityType<EntityBison> BISON;
    public static EntityType<EntityGiantSquid> GIANT_SQUID;
    public static EntityType<EntitySquidGrapple> SQUID_GRAPPLE;
    public static EntityType<EntitySeaBear> SEA_BEAR;
    public static EntityType<EntityDevilsHolePupfish> DEVILS_HOLE_PUPFISH;
    public static EntityType<EntityCatfish> CATFISH;
    public static EntityType<EntityFlyingFish> FLYING_FISH;
    public static EntityType<EntitySkelewag> SKELEWAG;
    public static EntityType<EntityRainFrog> RAIN_FROG;
    public static EntityType<EntityPotoo> POTOO;
    public static EntityType<EntityMudskipper> MUDSKIPPER;
    public static EntityType<EntityMudBall> MUD_BALL;
    public static EntityType<EntityRhinoceros> RHINOCEROS;
    public static EntityType<EntitySugarGlider> SUGAR_GLIDER;
    public static EntityType<EntityFarseer> FARSEER;
    public static EntityType<EntitySkreecher> SKREECHER;
    public static EntityType<EntityUnderminer> UNDERMINER;
    public static EntityType<EntityMurmur> MURMUR;
    public static EntityType<EntityMurmurHead> MURMUR_HEAD;
    public static EntityType<EntityTendonSegment> TENDON_SEGMENT;
    public static EntityType<EntitySkunk> SKUNK;
    public static EntityType<EntityFart> FART;
    public static EntityType<EntityBananaSlug> BANANA_SLUG;
    public static EntityType<EntityBlueJay> BLUE_JAY;
    public static EntityType<EntityCaiman> CAIMAN;
    public static EntityType<EntityTriops> TRIOPS;

    public static void init() {
        GRIZZLY_BEAR = register("grizzly_bear", EntityType.Builder.of(EntityGrizzlyBear::new, MobCategory.CREATURE).sized(1.6F, 1.8F).clientTrackingRange(10));
        ROADRUNNER = register("roadrunner", EntityType.Builder.of(EntityRoadrunner::new, MobCategory.CREATURE).sized(0.45F, 0.75F).clientTrackingRange(10));
        BONE_SERPENT = register("bone_serpent", EntityType.Builder.of(EntityBoneSerpent::new, MobCategory.MONSTER).sized(1.2F, 1.15F).fireImmune().clientTrackingRange(10));
        BONE_SERPENT_PART = register("bone_serpent_part", EntityType.Builder.<EntityBoneSerpentPart>of(EntityBoneSerpentPart::new, MobCategory.MONSTER).sized(1F, 1F).fireImmune().clientTrackingRange(10));
        GAZELLE = register("gazelle", EntityType.Builder.of(EntityGazelle::new, MobCategory.CREATURE).sized(0.85F, 1.25F).clientTrackingRange(10));
        CROCODILE = register("crocodile", EntityType.Builder.of(EntityCrocodile::new, MobCategory.CREATURE).sized(2.15F, 0.75F).clientTrackingRange(10));
        FLY = register("fly", EntityType.Builder.of(EntityFly::new, MobCategory.AMBIENT).sized(0.35F, 0.35F).clientTrackingRange(4));
        HUMMINGBIRD = register("hummingbird", EntityType.Builder.of(EntityHummingbird::new, MobCategory.CREATURE).sized(0.45F, 0.45F).clientTrackingRange(5));
        ORCA = register("orca", EntityType.Builder.of(EntityOrca::new, MobCategory.WATER_CREATURE).sized(3.75F, 1.75F).clientTrackingRange(10));
        SUNBIRD = register("sunbird", EntityType.Builder.of(EntitySunbird::new, MobCategory.CREATURE).sized(2.75F, 1.5F).fireImmune().clientTrackingRange(12).updateInterval(1));
        GORILLA = register("gorilla", EntityType.Builder.of(EntityGorilla::new, MobCategory.CREATURE).sized(1.25F, 1.5F).clientTrackingRange(10));
        CRIMSON_MOSQUITO = register("crimson_mosquito", EntityType.Builder.of(EntityCrimsonMosquito::new, MobCategory.MONSTER).sized(1.25F, 1.15F).fireImmune().clientTrackingRange(8));
        MOSQUITO_SPIT = register("mosquito_spit", EntityType.Builder.<EntityMosquitoSpit>of(EntityMosquitoSpit::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune());
        RATTLESNAKE = register("rattlesnake", EntityType.Builder.of(EntityRattlesnake::new, MobCategory.CREATURE).sized(0.95F, 0.35F).clientTrackingRange(10));
        ENDERGRADE = register("endergrade", EntityType.Builder.of(EntityEndergrade::new, MobCategory.CREATURE).sized(0.95F, 0.85F).clientTrackingRange(10));
        HAMMERHEAD_SHARK = register("hammerhead_shark", EntityType.Builder.of(EntityHammerheadShark::new, MobCategory.WATER_CREATURE).sized(2.4F, 1.25F).clientTrackingRange(10));
        SHARK_TOOTH_ARROW = register("shark_tooth_arrow", EntityType.Builder.<EntitySharkToothArrow>of(EntitySharkToothArrow::new, MobCategory.MISC).sized(0.5F, 0.5F));
        LOBSTER = register("lobster", EntityType.Builder.of(EntityLobster::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.4F).clientTrackingRange(5));
        KOMODO_DRAGON = register("komodo_dragon", EntityType.Builder.of(EntityKomodoDragon::new, MobCategory.CREATURE).sized(1.9F, 0.9F).clientTrackingRange(10));
        CAPUCHIN_MONKEY = register("capuchin_monkey", EntityType.Builder.of(EntityCapuchinMonkey::new, MobCategory.CREATURE).sized(0.65F, 0.75F).clientTrackingRange(10));
        TOSSED_ITEM = register("tossed_item", EntityType.Builder.<EntityTossedItem>of(EntityTossedItem::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune());
        CENTIPEDE_HEAD = register("centipede_head", EntityType.Builder.of(EntityCentipedeHead::new, MobCategory.MONSTER).sized(0.9F, 0.9F).clientTrackingRange(8));
        CENTIPEDE_BODY = register("centipede_body", EntityType.Builder.<EntityCentipedeBody>of(EntityCentipedeBody::new, MobCategory.MISC).sized(0.9F, 0.9F).fireImmune().updateInterval(1).clientTrackingRange(8));
        CENTIPEDE_TAIL = register("centipede_tail", EntityType.Builder.<EntityCentipedeTail>of(EntityCentipedeTail::new, MobCategory.MISC).sized(0.9F, 0.9F).fireImmune().updateInterval(1).clientTrackingRange(8));
        WARPED_TOAD = register("warped_toad", EntityType.Builder.of(EntityWarpedToad::new, MobCategory.CREATURE).sized(0.9F, 1.4F).fireImmune().updateInterval(1).clientTrackingRange(10));
        MOOSE = register("moose", EntityType.Builder.of(EntityMoose::new, MobCategory.CREATURE).sized(1.7F, 2.4F).clientTrackingRange(10));
        MIMICUBE = register("mimicube", EntityType.Builder.of(EntityMimicube::new, MobCategory.MONSTER).sized(0.9F, 0.9F).clientTrackingRange(8));
        RACCOON = register("raccoon", EntityType.Builder.of(EntityRaccoon::new, MobCategory.CREATURE).sized(0.8F, 0.9F).clientTrackingRange(10));
        BLOBFISH = register("blobfish", EntityType.Builder.of(EntityBlobfish::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.45F).clientTrackingRange(5));
        SEAL = register("seal", EntityType.Builder.of(EntitySeal::new, MobCategory.CREATURE).sized(1.45F, 0.9F).clientTrackingRange(10));
        COCKROACH = register("cockroach", EntityType.Builder.of(EntityCockroach::new, MobCategory.AMBIENT).sized(0.7F, 0.3F).clientTrackingRange(5));
        COCKROACH_EGG = register("cockroach_egg", EntityType.Builder.<EntityCockroachEgg>of(EntityCockroachEgg::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune());
        SHOEBILL = register("shoebill", EntityType.Builder.of(EntityShoebill::new, MobCategory.CREATURE).sized(0.8F, 1.5F).updateInterval(1).clientTrackingRange(10));
        ELEPHANT = register("elephant", EntityType.Builder.of(EntityElephant::new, MobCategory.CREATURE).sized(3.1F, 3.5F).updateInterval(1).clientTrackingRange(10));
        SOUL_VULTURE = register("soul_vulture", EntityType.Builder.of(EntitySoulVulture::new, MobCategory.MONSTER).sized(0.9F, 1.3F).updateInterval(1).fireImmune().clientTrackingRange(8));
        SNOW_LEOPARD = register("snow_leopard", EntityType.Builder.of(EntitySnowLeopard::new, MobCategory.CREATURE).sized(1.2F, 1.3F).immuneTo(Blocks.POWDER_SNOW).clientTrackingRange(10));
        SPECTRE = register("spectre", EntityType.Builder.of(EntitySpectre::new, MobCategory.CREATURE).sized(3.15F, 0.8F).fireImmune().clientTrackingRange(10).updateInterval(1));
        CROW = register("crow", EntityType.Builder.of(EntityCrow::new, MobCategory.CREATURE).sized(0.45F, 0.45F).clientTrackingRange(10));
        ALLIGATOR_SNAPPING_TURTLE = register("alligator_snapping_turtle", EntityType.Builder.of(EntityAlligatorSnappingTurtle::new, MobCategory.CREATURE).sized(1.25F, 0.65F).clientTrackingRange(10));
        MUNGUS = register("mungus", EntityType.Builder.of(EntityMungus::new, MobCategory.CREATURE).sized(0.75F, 1.45F).clientTrackingRange(10));
        MANTIS_SHRIMP = register("mantis_shrimp", EntityType.Builder.of(EntityMantisShrimp::new, MobCategory.WATER_CREATURE).sized(1.25F, 1.2F).clientTrackingRange(10));
        GUSTER = register("guster", EntityType.Builder.of(EntityGuster::new, MobCategory.MONSTER).sized(1.42F, 2.35F).fireImmune().clientTrackingRange(8));
        SAND_SHOT = register("sand_shot", EntityType.Builder.<EntitySandShot>of(EntitySandShot::new, MobCategory.MISC).sized(0.95F, 0.65F).fireImmune());
        GUST = register("gust", EntityType.Builder.<EntityGust>of(EntityGust::new, MobCategory.MISC).sized(0.8F, 0.8F).fireImmune());
        WARPED_MOSCO = register("warped_mosco", EntityType.Builder.of(EntityWarpedMosco::new, MobCategory.MONSTER).sized(1.99F, 3.25F).fireImmune().clientTrackingRange(10));
        HEMOLYMPH = register("hemolymph", EntityType.Builder.<EntityHemolymph>of(EntityHemolymph::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune());
        STRADDLER = register("straddler", EntityType.Builder.of(EntityStraddler::new, MobCategory.MONSTER).sized(1.65F, 3F).fireImmune().clientTrackingRange(8));
        STRADPOLE = register("stradpole", EntityType.Builder.of(EntityStradpole::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.5F).fireImmune().clientTrackingRange(4));
        STRADDLEBOARD = register("straddleboard", EntityType.Builder.<EntityStraddleboard>of(EntityStraddleboard::new, MobCategory.MISC).sized(1.5F, 0.35F).fireImmune().updateInterval(1).clientTrackingRange(10));
        EMU = register("emu", EntityType.Builder.of(EntityEmu::new, MobCategory.CREATURE).sized(1.1F, 1.8F).clientTrackingRange(10));
        EMU_EGG = register("emu_egg", EntityType.Builder.<EntityEmuEgg>of(EntityEmuEgg::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune());
        PLATYPUS = register("platypus", EntityType.Builder.of(EntityPlatypus::new, MobCategory.CREATURE).sized(0.8F, 0.5F).clientTrackingRange(10));
        DROPBEAR = register("dropbear", EntityType.Builder.of(EntityDropBear::new, MobCategory.MONSTER).sized(1.65F, 1.5F).fireImmune().clientTrackingRange(8));
        TASMANIAN_DEVIL = register("tasmanian_devil", EntityType.Builder.of(EntityTasmanianDevil::new, MobCategory.CREATURE).sized(0.7F, 0.8F).clientTrackingRange(10));
        KANGAROO = register("kangaroo", EntityType.Builder.of(EntityKangaroo::new, MobCategory.CREATURE).sized(1.65F, 1.5F).clientTrackingRange(10));
        CACHALOT_WHALE = register("cachalot_whale", EntityType.Builder.of(EntityCachalotWhale::new, MobCategory.WATER_CREATURE).sized(9F, 4.0F).clientTrackingRange(10));
        CACHALOT_ECHO = register("cachalot_echo", EntityType.Builder.<EntityCachalotEcho>of(EntityCachalotEcho::new, MobCategory.MISC).sized(2F, 2F).fireImmune());
        LEAFCUTTER_ANT = register("leafcutter_ant", EntityType.Builder.of(EntityLeafcutterAnt::new, MobCategory.CREATURE).sized(0.8F, 0.5F).clientTrackingRange(5));
        ENDERIOPHAGE = register("enderiophage", EntityType.Builder.of(EntityEnderiophage::new, MobCategory.CREATURE).sized(0.85F, 1.95F).updateInterval(1).clientTrackingRange(8));
        ENDERIOPHAGE_ROCKET = register("enderiophage_rocket", EntityType.Builder.<EntityEnderiophageRocket>of(EntityEnderiophageRocket::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune());
        BALD_EAGLE = register("bald_eagle", EntityType.Builder.of(EntityBaldEagle::new, MobCategory.CREATURE).sized(0.5F, 0.95F).updateInterval(1).clientTrackingRange(14));
        TIGER = register("tiger", EntityType.Builder.of(EntityTiger::new, MobCategory.CREATURE).sized(1.45F, 1.2F).clientTrackingRange(10));
        TARANTULA_HAWK = register("tarantula_hawk", EntityType.Builder.of(EntityTarantulaHawk::new, MobCategory.CREATURE).sized(1.2F, 0.9F).clientTrackingRange(10));
        VOID_WORM = register("void_worm", EntityType.Builder.of(EntityVoidWorm::new, MobCategory.MONSTER).sized(3.4F, 3F).fireImmune().clientTrackingRange(20).updateInterval(1));
        VOID_WORM_PART = register("void_worm_part", EntityType.Builder.<EntityVoidWormPart>of(EntityVoidWormPart::new, MobCategory.MONSTER).sized(1.2F, 1.35F).fireImmune().clientTrackingRange(20).updateInterval(1));
        VOID_WORM_SHOT = register("void_worm_shot", EntityType.Builder.<EntityVoidWormShot>of(EntityVoidWormShot::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune());
        VOID_PORTAL = register("void_portal", EntityType.Builder.<EntityVoidPortal>of(EntityVoidPortal::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune());
        FRILLED_SHARK = register("frilled_shark", EntityType.Builder.of(EntityFrilledShark::new, MobCategory.WATER_CREATURE).sized(1.3F, 0.4F).clientTrackingRange(8));
        MIMIC_OCTOPUS = register("mimic_octopus", EntityType.Builder.of(EntityMimicOctopus::new, MobCategory.WATER_CREATURE).sized(0.9F, 0.6F).clientTrackingRange(8));
        SEAGULL = register("seagull", EntityType.Builder.of(EntitySeagull::new, MobCategory.CREATURE).sized(0.45F, 0.45F).clientTrackingRange(10));
        FROSTSTALKER = register("froststalker", EntityType.Builder.of(EntityFroststalker::new, MobCategory.CREATURE).sized(0.95F, 1.15F).immuneTo(Blocks.POWDER_SNOW));
        ICE_SHARD = register("ice_shard", EntityType.Builder.<EntityIceShard>of(EntityIceShard::new, MobCategory.MISC).sized(0.45F, 0.45F).fireImmune());
        TUSKLIN = register("tusklin", EntityType.Builder.of(EntityTusklin::new, MobCategory.CREATURE).sized(2.2F, 1.9F).immuneTo(Blocks.POWDER_SNOW).clientTrackingRange(10));
        LAVIATHAN = register("laviathan", EntityType.Builder.of(EntityLaviathan::new, MobCategory.CREATURE).sized(3.3F, 2.4F).fireImmune().updateInterval(1).clientTrackingRange(10));
        COSMAW = register("cosmaw", EntityType.Builder.of(EntityCosmaw::new, MobCategory.CREATURE).sized(1.95F, 1.8F).clientTrackingRange(10));
        TOUCAN = register("toucan", EntityType.Builder.of(EntityToucan::new, MobCategory.CREATURE).sized(0.45F, 0.45F).clientTrackingRange(10));
        MANED_WOLF = register("maned_wolf", EntityType.Builder.of(EntityManedWolf::new, MobCategory.CREATURE).sized(0.9F, 1.26F).clientTrackingRange(10));
        ANACONDA = register("anaconda", EntityType.Builder.of(EntityAnaconda::new, MobCategory.CREATURE).sized(0.8F, 0.8F).clientTrackingRange(10));
        ANACONDA_PART = register("anaconda_part", EntityType.Builder.<EntityAnacondaPart>of(EntityAnacondaPart::new, MobCategory.MISC).sized(0.8F, 0.8F).updateInterval(1).clientTrackingRange(10));
        VINE_LASSO = register("vine_lasso", EntityType.Builder.<EntityVineLasso>of(EntityVineLasso::new, MobCategory.MISC).sized(0.85F, 0.2F).fireImmune());
        ANTEATER = register("anteater", EntityType.Builder.of(EntityAnteater::new, MobCategory.CREATURE).sized(1.3F, 1.1F).clientTrackingRange(10));
        ROCKY_ROLLER = register("rocky_roller", EntityType.Builder.of(EntityRockyRoller::new, MobCategory.MONSTER).sized(1.2F, 1.45F).clientTrackingRange(8));
        FLUTTER = register("flutter", EntityType.Builder.of(EntityFlutter::new, MobCategory.AMBIENT).sized(0.5F, 0.7F).clientTrackingRange(6));
        POLLEN_BALL = register("pollen_ball", EntityType.Builder.<EntityPollenBall>of(EntityPollenBall::new, MobCategory.MISC).sized(0.35F, 0.35F).fireImmune());
        GELADA_MONKEY = register("gelada_monkey", EntityType.Builder.of(EntityGeladaMonkey::new, MobCategory.CREATURE).sized(1.2F, 1.2F).clientTrackingRange(10));
        JERBOA = register("jerboa", EntityType.Builder.of(EntityJerboa::new, MobCategory.AMBIENT).sized(0.5F, 0.5F).clientTrackingRange(5));
        TERRAPIN = register("terrapin", EntityType.Builder.of(EntityTerrapin::new, MobCategory.WATER_AMBIENT).sized(0.75F, 0.45F).clientTrackingRange(5));
        COMB_JELLY = register("comb_jelly", EntityType.Builder.of(EntityCombJelly::new, MobCategory.WATER_AMBIENT).sized(0.65F, 0.8F).clientTrackingRange(5));
        COSMIC_COD = register("cosmic_cod", EntityType.Builder.of(EntityCosmicCod::new, MobCategory.AMBIENT).sized(0.85F, 0.4F).clientTrackingRange(5));
        BUNFUNGUS = register("bunfungus", EntityType.Builder.of(EntityBunfungus::new, MobCategory.CREATURE).sized(1.85F, 2.1F).clientTrackingRange(10));
        BISON = register("bison", EntityType.Builder.of(EntityBison::new, MobCategory.CREATURE).sized(2.4F, 2.1F).clientTrackingRange(10));
        GIANT_SQUID = register("giant_squid", EntityType.Builder.of(EntityGiantSquid::new, MobCategory.WATER_CREATURE).sized(0.9F, 1.2F).clientTrackingRange(10));
        SQUID_GRAPPLE = register("squid_grapple", EntityType.Builder.<EntitySquidGrapple>of(EntitySquidGrapple::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune());
        SEA_BEAR = register("sea_bear", EntityType.Builder.of(EntitySeaBear::new, MobCategory.WATER_CREATURE).sized(2.4F, 1.99F).clientTrackingRange(10));
        DEVILS_HOLE_PUPFISH = register("devils_hole_pupfish", EntityType.Builder.of(EntityDevilsHolePupfish::new, MobCategory.WATER_AMBIENT).sized(0.6F, 0.4F).clientTrackingRange(4));
        CATFISH = register("catfish", EntityType.Builder.of(EntityCatfish::new, MobCategory.WATER_AMBIENT).sized(0.9F, 0.6F).clientTrackingRange(5));
        FLYING_FISH = register("flying_fish", EntityType.Builder.of(EntityFlyingFish::new, MobCategory.WATER_AMBIENT).sized(0.6F, 0.4F).clientTrackingRange(5));
        SKELEWAG = register("skelewag", EntityType.Builder.of(EntitySkelewag::new, MobCategory.MONSTER).sized(2F, 1.2F).updateInterval(1).clientTrackingRange(8));
        RAIN_FROG = register("rain_frog", EntityType.Builder.of(EntityRainFrog::new, MobCategory.AMBIENT).sized(0.55F, 0.5F).clientTrackingRange(5));
        POTOO = register("potoo", EntityType.Builder.of(EntityPotoo::new, MobCategory.CREATURE).sized(0.6F, 0.8F).clientTrackingRange(10));
        MUDSKIPPER = register("mudskipper", EntityType.Builder.of(EntityMudskipper::new, MobCategory.CREATURE).sized(0.7F, 0.44F).clientTrackingRange(10));
        MUD_BALL = register("mud_ball", EntityType.Builder.<EntityMudBall>of(EntityMudBall::new, MobCategory.MISC).sized(0.35F, 0.35F).fireImmune());
        RHINOCEROS = register("rhinoceros", EntityType.Builder.of(EntityRhinoceros::new, MobCategory.CREATURE).sized(2.3F, 2.4F).clientTrackingRange(10));
        SUGAR_GLIDER = register("sugar_glider", EntityType.Builder.of(EntitySugarGlider::new, MobCategory.CREATURE).sized(0.8F, 0.45F).clientTrackingRange(10));
        FARSEER = register("farseer", EntityType.Builder.of(EntityFarseer::new, MobCategory.MONSTER).sized(0.99F, 1.5F).updateInterval(1).fireImmune().clientTrackingRange(8));
        SKREECHER = register("skreecher", EntityType.Builder.of(EntitySkreecher::new, MobCategory.CREATURE).sized(0.99F, 0.95F).updateInterval(1).clientTrackingRange(8));
        UNDERMINER = register("underminer", EntityType.Builder.of(EntityUnderminer::new, MobCategory.AMBIENT).sized(0.8F, 1.8F).clientTrackingRange(8));
        MURMUR = register("murmur", EntityType.Builder.of(EntityMurmur::new, MobCategory.MONSTER).sized(0.7F, 1.45F).clientTrackingRange(8));
        MURMUR_HEAD = register("murmur_head", EntityType.Builder.<EntityMurmurHead>of(EntityMurmurHead::new, MobCategory.MONSTER).sized(0.55F, 0.55F).clientTrackingRange(8));
        TENDON_SEGMENT = register("tendon_segment", EntityType.Builder.<EntityTendonSegment>of(EntityTendonSegment::new, MobCategory.MISC).sized(0.1F, 0.1F).fireImmune());
        SKUNK = register("skunk", EntityType.Builder.of(EntitySkunk::new, MobCategory.CREATURE).sized(0.85F, 0.65F).clientTrackingRange(10));
        FART = register("fart", EntityType.Builder.<EntityFart>of(EntityFart::new, MobCategory.MISC).sized(0.7F, 0.3F).fireImmune());
        BANANA_SLUG = register("banana_slug", EntityType.Builder.of(EntityBananaSlug::new, MobCategory.CREATURE).sized(0.8F, 0.4F).clientTrackingRange(10));
        BLUE_JAY = register("blue_jay", EntityType.Builder.of(EntityBlueJay::new, MobCategory.CREATURE).sized(0.5F, 0.6F).clientTrackingRange(10));
        CAIMAN = register("caiman", EntityType.Builder.of(EntityCaiman::new, MobCategory.CREATURE).sized(1.3F, 0.6F).clientTrackingRange(10));
        TRIOPS = register("triops", EntityType.Builder.of(EntityTriops::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.25F).clientTrackingRange(5));
        registerAttributes();
        registerSpawnPlacements();
    }

    private static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(GRIZZLY_BEAR, EntityGrizzlyBear.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(ROADRUNNER, EntityRoadrunner.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(BONE_SERPENT, EntityBoneSerpent.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(BONE_SERPENT_PART, EntityBoneSerpentPart.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(GAZELLE, EntityGazelle.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(CROCODILE, EntityCrocodile.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(FLY, EntityFly.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(HUMMINGBIRD, EntityHummingbird.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(ORCA, EntityOrca.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(SUNBIRD, EntitySunbird.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(GORILLA, EntityGorilla.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(CRIMSON_MOSQUITO, EntityCrimsonMosquito.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(RATTLESNAKE, EntityRattlesnake.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(ENDERGRADE, EntityEndergrade.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(HAMMERHEAD_SHARK, EntityHammerheadShark.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(LOBSTER, EntityLobster.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(KOMODO_DRAGON, EntityKomodoDragon.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(CAPUCHIN_MONKEY, EntityCapuchinMonkey.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(CENTIPEDE_HEAD, EntityCentipedeHead.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(CENTIPEDE_BODY, EntityCentipedeBody.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(CENTIPEDE_TAIL, EntityCentipedeTail.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(WARPED_TOAD, EntityWarpedToad.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(MOOSE, EntityMoose.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(MIMICUBE, EntityMimicube.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(RACCOON, EntityRaccoon.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(BLOBFISH, EntityBlobfish.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(SEAL, EntitySeal.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(COCKROACH, EntityCockroach.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(SHOEBILL, EntityShoebill.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(ELEPHANT, EntityElephant.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(SOUL_VULTURE, EntitySoulVulture.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(SNOW_LEOPARD, EntitySnowLeopard.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(SPECTRE, EntitySpectre.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(CROW, EntityCrow.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(ALLIGATOR_SNAPPING_TURTLE, EntityAlligatorSnappingTurtle.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(MUNGUS, EntityMungus.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(MANTIS_SHRIMP, EntityMantisShrimp.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(GUSTER, EntityGuster.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(WARPED_MOSCO, EntityWarpedMosco.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(STRADDLER, EntityStraddler.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(STRADPOLE, EntityStradpole.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(EMU, EntityEmu.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(PLATYPUS, EntityPlatypus.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(DROPBEAR, EntityDropBear.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(TASMANIAN_DEVIL, EntityTasmanianDevil.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(KANGAROO, EntityKangaroo.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(CACHALOT_WHALE, EntityCachalotWhale.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(LEAFCUTTER_ANT, EntityLeafcutterAnt.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(ENDERIOPHAGE, EntityEnderiophage.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(BALD_EAGLE, EntityBaldEagle.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(TIGER, EntityTiger.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(TARANTULA_HAWK, EntityTarantulaHawk.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(VOID_WORM, EntityVoidWorm.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(VOID_WORM_PART, EntityVoidWormPart.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(FRILLED_SHARK, EntityFrilledShark.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(MIMIC_OCTOPUS, EntityMimicOctopus.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(SEAGULL, EntitySeagull.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(FROSTSTALKER, EntityFroststalker.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(TUSKLIN, EntityTusklin.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(LAVIATHAN, EntityLaviathan.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(COSMAW, EntityCosmaw.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(TOUCAN, EntityToucan.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(MANED_WOLF, EntityManedWolf.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(ANACONDA, EntityAnaconda.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(ANACONDA_PART, EntityAnacondaPart.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(ANTEATER, EntityAnteater.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(ROCKY_ROLLER, EntityRockyRoller.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(FLUTTER, EntityFlutter.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(GELADA_MONKEY, EntityGeladaMonkey.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(JERBOA, EntityJerboa.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(TERRAPIN, EntityTerrapin.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(COMB_JELLY, EntityCombJelly.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(COSMIC_COD, EntityCosmicCod.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(BUNFUNGUS, EntityBunfungus.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(BISON, EntityBison.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(GIANT_SQUID, EntityGiantSquid.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(SEA_BEAR, EntitySeaBear.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(DEVILS_HOLE_PUPFISH, EntityDevilsHolePupfish.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(CATFISH, EntityCatfish.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(FLYING_FISH, EntityFlyingFish.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(SKELEWAG, EntitySkelewag.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(RAIN_FROG, EntityRainFrog.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(POTOO, EntityPotoo.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(MUDSKIPPER, EntityMudskipper.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(RHINOCEROS, EntityRhinoceros.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(SUGAR_GLIDER, EntitySugarGlider.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(FARSEER, EntityFarseer.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(SKREECHER, EntitySkreecher.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(UNDERMINER, EntityUnderminer.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(MURMUR, EntityMurmur.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(MURMUR_HEAD, EntityMurmurHead.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(SKUNK, EntitySkunk.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(BANANA_SLUG, EntityBananaSlug.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(BLUE_JAY, EntityBlueJay.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(CAIMAN, EntityCaiman.bakeAttributes().build());
        FabricDefaultAttributeRegistry.register(TRIOPS, EntityTriops.bakeAttributes().build());
    }

    private static void registerSpawnPlacements() {
        // Fabric: no custom SpawnPlacements.Type; use ON_GROUND and combine leaves placement check in predicate
        SpawnPlacements.register(GRIZZLY_BEAR, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ROADRUNNER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityRoadrunner::canRoadrunnerSpawn);
        SpawnPlacements.register(BONE_SERPENT, SpawnPlacementTypes.IN_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityBoneSerpent::canBoneSerpentSpawn);
        SpawnPlacements.register(GAZELLE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(CROCODILE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCrocodile::canCrocodileSpawn);
        SpawnPlacements.register(FLY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityFly::canFlySpawn);
        SpawnPlacements.register(HUMMINGBIRD, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityHummingbird::canHummingbirdSpawn);
        SpawnPlacements.register(ORCA, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityOrca::canOrcaSpawn);
        SpawnPlacements.register(SUNBIRD, SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySunbird::canSunbirdSpawn);
        SpawnPlacements.register(GORILLA, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityGorilla::canGorillaSpawn);
        SpawnPlacements.register(CRIMSON_MOSQUITO, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCrimsonMosquito::canMosquitoSpawn);
        SpawnPlacements.register(RATTLESNAKE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityRattlesnake::canRattlesnakeSpawn);
        SpawnPlacements.register(ENDERGRADE, SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityEndergrade::canEndergradeSpawn);
        SpawnPlacements.register(HAMMERHEAD_SHARK, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityHammerheadShark::canHammerheadSharkSpawn);
        SpawnPlacements.register(LOBSTER, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityLobster::canLobsterSpawn);
        SpawnPlacements.register(KOMODO_DRAGON, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityKomodoDragon::canKomodoDragonSpawn);
        SpawnPlacements.register(CAPUCHIN_MONKEY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, AMEntityRegistry::createCapuchinLeavesSpawn);
        SpawnPlacements.register(CENTIPEDE_HEAD, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCentipedeHead::canCentipedeSpawn);
        SpawnPlacements.register(WARPED_TOAD, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityWarpedToad::canWarpedToadSpawn);
        SpawnPlacements.register(MOOSE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMoose::canMooseSpawn);
        SpawnPlacements.register(MIMICUBE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(RACCOON, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(BLOBFISH, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityBlobfish::canBlobfishSpawn);
        SpawnPlacements.register(SEAL, SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySeal::canSealSpawn);
        SpawnPlacements.register(COCKROACH, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCockroach::canCockroachSpawn);
        SpawnPlacements.register(SHOEBILL, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ELEPHANT, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(SOUL_VULTURE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySoulVulture::canVultureSpawn);
        SpawnPlacements.register(SPECTRE, SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySpectre::canSpectreSpawn);
        SpawnPlacements.register(SNOW_LEOPARD, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySnowLeopard::canSnowLeopardSpawn);
        SpawnPlacements.register(CROW, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityCrow::canCrowSpawn);
        SpawnPlacements.register(ALLIGATOR_SNAPPING_TURTLE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityAlligatorSnappingTurtle::canTurtleSpawn);
        SpawnPlacements.register(MUNGUS, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMungus::canMungusSpawn);
        SpawnPlacements.register(MANTIS_SHRIMP, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMantisShrimp::canMantisShrimpSpawn);
        SpawnPlacements.register(GUSTER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityGuster::canGusterSpawn);
        SpawnPlacements.register(WARPED_MOSCO, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
        SpawnPlacements.register(STRADDLER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityStraddler::canStraddlerSpawn);
        SpawnPlacements.register(STRADPOLE, SpawnPlacementTypes.IN_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityStradpole::canStradpoleSpawn);
        SpawnPlacements.register(EMU, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityEmu::canEmuSpawn);
        SpawnPlacements.register(PLATYPUS, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityPlatypus::canPlatypusSpawn);
        SpawnPlacements.register(DROPBEAR, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
        SpawnPlacements.register(TASMANIAN_DEVIL, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(KANGAROO, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityKangaroo::canKangarooSpawn);
        SpawnPlacements.register(CACHALOT_WHALE, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCachalotWhale::canCachalotWhaleSpawn);
        SpawnPlacements.register(LEAFCUTTER_ANT, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ENDERIOPHAGE, SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityEnderiophage::canEnderiophageSpawn);
        SpawnPlacements.register(BALD_EAGLE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityBaldEagle::canEagleSpawn);
        SpawnPlacements.register(TIGER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityTiger::canTigerSpawn);
        SpawnPlacements.register(TARANTULA_HAWK, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityTarantulaHawk::canTarantulaHawkSpawn);
        SpawnPlacements.register(VOID_WORM, SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityVoidWorm::canVoidWormSpawn);
        SpawnPlacements.register(FRILLED_SHARK, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityFrilledShark::canFrilledSharkSpawn);
        SpawnPlacements.register(MIMIC_OCTOPUS, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMimicOctopus::canMimicOctopusSpawn);
        SpawnPlacements.register(SEAGULL, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySeagull::canSeagullSpawn);
        SpawnPlacements.register(FROSTSTALKER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityFroststalker::canFroststalkerSpawn);
        SpawnPlacements.register(TUSKLIN, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityTusklin::canTusklinSpawn);
        SpawnPlacements.register(LAVIATHAN, SpawnPlacementTypes.IN_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityLaviathan::canLaviathanSpawn);
        SpawnPlacements.register(COSMAW, SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCosmaw::canCosmawSpawn);
        SpawnPlacements.register(TOUCAN, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, AMEntityRegistry::createToucanLeavesSpawn);
        SpawnPlacements.register(MANED_WOLF, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityManedWolf::checkAnimalSpawnRules);
        SpawnPlacements.register(ANACONDA, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityAnaconda::canAnacondaSpawn);
        SpawnPlacements.register(ANTEATER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityAnteater::canAnteaterSpawn);
        SpawnPlacements.register(ROCKY_ROLLER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityRockyRoller::checkRockyRollerSpawnRules);
        SpawnPlacements.register(FLUTTER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityFlutter::canFlutterSpawn);
        SpawnPlacements.register(GELADA_MONKEY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityGeladaMonkey::checkAnimalSpawnRules);
        SpawnPlacements.register(JERBOA, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityJerboa::canJerboaSpawn);
        SpawnPlacements.register(TERRAPIN, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityTerrapin::canTerrapinSpawn);
        SpawnPlacements.register(COMB_JELLY, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCombJelly::canCombJellySpawn);
        SpawnPlacements.register(COSMIC_COD, SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> true);
        SpawnPlacements.register(BUNFUNGUS, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityBunfungus::canBunfungusSpawn);
        SpawnPlacements.register(BISON, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityBison::checkAnimalSpawnRules);
        SpawnPlacements.register(GIANT_SQUID, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityGiantSquid::canGiantSquidSpawn);
        SpawnPlacements.register(DEVILS_HOLE_PUPFISH, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityDevilsHolePupfish::canPupfishSpawn);
        SpawnPlacements.register(CATFISH, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCatfish::canCatfishSpawn);
        SpawnPlacements.register(FLYING_FISH, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        SpawnPlacements.register(SKELEWAG, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySkelewag::canSkelewagSpawn);
        SpawnPlacements.register(RAIN_FROG, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityRainFrog::canRainFrogSpawn);
        SpawnPlacements.register(POTOO, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, AMEntityRegistry::createPotooLeavesSpawn);
        SpawnPlacements.register(MUDSKIPPER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMudskipper::canMudskipperSpawn);
        SpawnPlacements.register(RHINOCEROS, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityRhinoceros::checkAnimalSpawnRules);
        SpawnPlacements.register(SUGAR_GLIDER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, AMEntityRegistry::createSugarGliderLeavesSpawn);
        SpawnPlacements.register(FARSEER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityFarseer::checkFarseerSpawnRules);
        SpawnPlacements.register(SKREECHER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySkreecher::checkSkreecherSpawnRules);
        SpawnPlacements.register(UNDERMINER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityUnderminer::checkUnderminerSpawnRules);
        SpawnPlacements.register(MURMUR, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMurmur::checkMurmurSpawnRules);
        SpawnPlacements.register(SKUNK, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySkunk::checkAnimalSpawnRules);
        SpawnPlacements.register(BANANA_SLUG, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityBananaSlug::checkBananaSlugSpawnRules);
        SpawnPlacements.register(BLUE_JAY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, AMEntityRegistry::createBlueJayLeavesSpawn);
        SpawnPlacements.register(CAIMAN, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCaiman::canCaimanSpawn);
        SpawnPlacements.register(TRIOPS, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
    }

    public static Predicate<LivingEntity> buildPredicateFromTag(TagKey<EntityType<?>> entityTag){
        if(entityTag == null){
            return Predicates.alwaysFalse();
        }else{
            return (com.google.common.base.Predicate<LivingEntity>) e -> e.isAlive() && e.getType().is(entityTag);
        }
    }

    public static Predicate<LivingEntity> buildPredicateFromTagTameable(TagKey<EntityType<?>> entityTag, LivingEntity owner){
        if(entityTag == null){
            return Predicates.alwaysFalse();
        }else{
            return (com.google.common.base.Predicate<LivingEntity>) e -> e.isAlive() && e.getType().is(entityTag) && !owner.isAlliedTo(e);
        }
    }

    public static boolean rollSpawn(int rolls, RandomSource random, MobSpawnType reason){
        if(reason == MobSpawnType.SPAWNER){
            return true;
        }else{
            return rolls <= 0 || random.nextInt(rolls) == 0;
        }
    }

    /** 5-param form for SpawnPlacements.SpawnPredicate (EntityType, ServerLevelAccessor, MobSpawnType, BlockPos, RandomSource). */
    public static boolean createCapuchinLeavesSpawn(EntityType<?> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return createLeavesSpawnPlacement(level, pos, type) && EntityCapuchinMonkey.canCapuchinSpawn(level, pos, type);
    }
    public static boolean createToucanLeavesSpawn(EntityType<?> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return createLeavesSpawnPlacement(level, pos, type) && EntityToucan.canToucanSpawn(level, pos, type);
    }
    public static boolean createPotooLeavesSpawn(EntityType<?> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return createLeavesSpawnPlacement(level, pos, type) && EntityPotoo.canPotooSpawn(level, pos, type);
    }
    public static boolean createSugarGliderLeavesSpawn(EntityType<?> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return createLeavesSpawnPlacement(level, pos, type) && EntitySugarGlider.canSugarGliderSpawn(level, pos, type);
    }
    public static boolean createBlueJayLeavesSpawn(EntityType<?> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return createLeavesSpawnPlacement(level, pos, type) && EntityBlueJay.checkBlueJaySpawnRules(level, pos, type);
    }

    public static boolean createLeavesSpawnPlacement(LevelReader level, BlockPos pos, EntityType<?> type){
        BlockPos blockpos = pos.above();
        BlockPos blockpos1 = pos.below();
        FluidState fluidstate = level.getFluidState(pos);
        BlockState blockstate = level.getBlockState(pos);
        BlockState blockstate1 = level.getBlockState(blockpos1);
        if (!blockstate1.isValidSpawn(level, blockpos1, type) && !blockstate1.is(BlockTags.LEAVES)) {
            return false;
        } else {
            return NaturalSpawner.isValidEmptySpawnBlock(level, pos, blockstate, fluidstate, type) && NaturalSpawner.isValidEmptySpawnBlock(level, blockpos, level.getBlockState(blockpos), level.getFluidState(blockpos), type);
        }
    }

    /** Fabric 1.21.1: LivingEntity.jumping is not reliably accessible; use upward velocity when jumping. */
    public static boolean getLivingJumping(LivingEntity living) {
        return living.getDeltaMovement().y > 0.0D && !living.onGround();
    }

}
