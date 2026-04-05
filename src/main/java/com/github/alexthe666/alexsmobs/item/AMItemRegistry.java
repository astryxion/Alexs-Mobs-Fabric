package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.server.block.LecternBooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class AMItemRegistry {
    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, path);
    }

    private static Item register(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, id(name), item);
    }

    /** Fabric: 1:1 replacements for ForgeMod attributes (registered in init()). */
    public static Attribute SWIM_SPEED_ATTRIBUTE;
    public static Attribute BLOCK_REACH_ATTRIBUTE;
    public static Attribute ENTITY_REACH_ATTRIBUTE;

    /** Fabric: 1:1 replacement for ItemStack.canPerformAction(ToolActions.SHIELD_BLOCK). True for vanilla shield and mod shield-like items. */
    public static boolean isShieldBlocking(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        Item i = stack.getItem();
        return i instanceof ShieldItem || i == SHIELD_OF_THE_DEEP || i == SKELEWAG_SWORD;
    }

    public static final AMArmorMaterial ROADRUNNER_ARMOR_MATERIAL = new AMArmorMaterial("roadrunner", 18, new int[]{3, 3, 3, 3}, 20, SoundEvents.ARMOR_EQUIP_TURTLE, 0);
    public static final AMArmorMaterial CROCODILE_ARMOR_MATERIAL = new AMArmorMaterial("crocodile", 22, new int[]{2, 5, 7, 3}, 25, SoundEvents.ARMOR_EQUIP_TURTLE, 1);
    public static final AMArmorMaterial CENTIPEDE_ARMOR_MATERIAL = new AMArmorMaterial("centipede", 20, new int[]{6, 6, 6, 6}, 22, SoundEvents.ARMOR_EQUIP_TURTLE, 0.5F);
    public static final AMArmorMaterial MOOSE_ARMOR_MATERIAL = new AMArmorMaterial("moose", 19, new int[]{3, 3, 3, 3}, 21, SoundEvents.ARMOR_EQUIP_TURTLE, 0.5F);
    public static final AMArmorMaterial RACCOON_ARMOR_MATERIAL = new AMArmorMaterial("raccoon", 17, new int[]{3, 3, 3, 3}, 21, SoundEvents.ARMOR_EQUIP_LEATHER, 2.5F);
    public static final AMArmorMaterial SOMBRERO_ARMOR_MATERIAL = new AMArmorMaterial("sombrero", 14, new int[]{2, 2, 2, 2}, 30, SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F);
    public static final AMArmorMaterial SPIKED_TURTLE_SHELL_ARMOR_MATERIAL = new AMArmorMaterial("spiked_turtle_shell", 35, new int[]{3, 3, 3, 3}, 30, SoundEvents.ARMOR_EQUIP_TURTLE, 1F, 0.2F);
    public static final AMArmorMaterial FEDORA_ARMOR_MATERIAL = new AMArmorMaterial("fedora", 10, new int[]{2, 2, 2, 2}, 30, SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F);
    public static final AMArmorMaterial EMU_ARMOR_MATERIAL = new AMArmorMaterial("emu", 9, new int[]{4, 4, 4, 4}, 20, SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F);
    public static final AMArmorMaterial TARANTULA_HAWK_ELYTRA_MATERIAL = new AMArmorMaterial("tarantula_hawk_elytra", 9, new int[]{3, 3, 3, 3}, 5, SoundEvents.ARMOR_EQUIP_LEATHER, 0);
    public static final AMArmorMaterial FROSTSTALKER_ARMOR_MATERIAL = new AMArmorMaterial("froststalker", 9, new int[]{3, 3, 3, 3}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F);
    public static final AMArmorMaterial ROCKY_ARMOR_MATERIAL = new AMArmorMaterial("rocky_roller", 20, new int[]{2, 5, 7, 3}, 10, SoundEvents.ARMOR_EQUIP_TURTLE, 0.5F);
    public static final AMArmorMaterial FLYING_FISH_MATERIAL = new AMArmorMaterial("flying_fish", 9, new int[]{1, 1, 1, 1}, 8, SoundEvents.ARMOR_EQUIP_LEATHER, 0F);
    public static final AMArmorMaterial NOVELTY_HAT_MATERIAL = new AMArmorMaterial("novelty_hat", 10, new int[]{2, 2, 2, 2}, 30, SoundEvents.ARMOR_EQUIP_LEATHER, 0F);
    public static final AMArmorMaterial KIMONO_MATERIAL = new AMArmorMaterial("kimono", 8, new int[]{3, 3, 3, 3}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0F);

    public static Item TAB_ICON;
    public static Item ANIMAL_DICTIONARY;
    public static Item BEAR_FUR;
    public static Item BEAR_DUST;
    public static Item ROADRUNNER_FEATHER;
    public static Item ROADDRUNNER_BOOTS;
    public static Item LAVA_BOTTLE;
    public static Item BONE_SERPENT_TOOTH;
    public static Item GAZELLE_HORN;
    public static Item CROCODILE_SCUTE;
    public static Item CROCODILE_CHESTPLATE;
    public static Item MAGGOT;
    public static Item BANANA;
    public static Item ANCIENT_DART;
    public static Item HALO;
    public static Item BLOOD_SAC;
    public static Item MOSQUITO_PROBOSCIS;
    public static Item BLOOD_SPRAYER;
    public static Item RATTLESNAKE_RATTLE;
    public static Item CHORUS_ON_A_STICK;
    public static Item SHARK_TOOTH;
    public static Item SHARK_TOOTH_ARROW;
    public static Item LOBSTER_TAIL;
    public static Item COOKED_LOBSTER_TAIL;
    public static Item LOBSTER_BUCKET;
    public static Item KOMODO_SPIT;
    public static Item KOMODO_SPIT_BOTTLE;
    public static Item POISON_BOTTLE;
    public static Item SOPA_DE_MACACO;
    public static Item CENTIPEDE_LEG;
    public static Item CENTIPEDE_LEGGINGS;
    public static Item MOSQUITO_LARVA;
    public static Item MOOSE_ANTLER;
    public static Item MOOSE_HEADGEAR;
    public static Item MOOSE_RIBS;
    public static Item COOKED_MOOSE_RIBS;
    public static Item MIMICREAM;
    public static Item RACCOON_TAIL;
    public static Item FRONTIER_CAP;
    public static Item BLOBFISH;
    public static Item BLOBFISH_BUCKET;
    public static Item FISH_OIL;
    public static Item MARACA;
    public static Item SOMBRERO;
    public static Item COCKROACH_WING_FRAGMENT;
    public static Item COCKROACH_WING;
    public static Item COCKROACH_OOTHECA;
    public static Item ACACIA_BLOSSOM;
    public static Item SOUL_HEART;
    public static Item SPIKED_SCUTE;
    public static Item SPIKED_TURTLE_SHELL;
    public static Item SHRIMP_FRIED_RICE;
    public static Item GUSTER_EYE;
    public static Item POCKET_SAND;
    public static Item WARPED_MUSCLE;
    public static Item HEMOLYMPH_SAC;
    public static Item HEMOLYMPH_BLASTER;
    public static Item WARPED_MIXTURE;
    public static Item STRADDLITE;
    public static Item STRADPOLE_BUCKET;
    public static Item STRADDLEBOARD;
    public static Item EMU_EGG;
    public static Item BOILED_EMU_EGG;
    public static Item EMU_FEATHER;
    public static Item EMU_LEGGINGS;
    public static Item PLATYPUS_BUCKET;
    public static Item FEDORA;
    public static Item DROPBEAR_CLAW;
    public static Item KANGAROO_MEAT;
    public static Item COOKED_KANGAROO_MEAT;
    public static Item KANGAROO_HIDE;
    public static Item KANGAROO_BURGER;
    public static Item AMBERGRIS;
    public static Item CACHALOT_WHALE_TOOTH;
    public static Item ECHOLOCATOR;
    public static Item ENDOLOCATOR;
    public static Item GONGYLIDIA;
    public static Item LEAFCUTTER_ANT_PUPA;
    public static Item ENDERIOPHAGE_ROCKET;
    public static Item FALCONRY_GLOVE_INVENTORY;
    public static Item FALCONRY_GLOVE_HAND;
    public static Item FALCONRY_GLOVE;
    public static Item FALCONRY_HOOD;
    public static Item TARANTULA_HAWK_WING_FRAGMENT;
    public static Item TARANTULA_HAWK_WING;
    public static Item TARANTULA_HAWK_ELYTRA;
    public static Item MYSTERIOUS_WORM;
    public static Item VOID_WORM_MANDIBLE;
    public static Item VOID_WORM_EYE;
    public static Item DIMENSIONAL_CARVER;
    public static Item SHATTERED_DIMENSIONAL_CARVER;
    public static Item SERRATED_SHARK_TOOTH;
    public static Item FRILLED_SHARK_BUCKET;
    public static Item SHIELD_OF_THE_DEEP;
    public static Item MIMIC_OCTOPUS_BUCKET;
    public static Item FROSTSTALKER_HORN;
    public static Item FROSTSTALKER_HELMET;
    public static Item PIGSHOES;
    public static Item STRADDLE_HELMET;
    public static Item STRADDLE_SADDLE;
    public static Item COSMIC_COD;
    public static Item SHED_SNAKE_SKIN;
    public static Item VINE_LASSO_INVENTORY;
    public static Item VINE_LASSO_HAND;
    public static Item VINE_LASSO;
    public static Item ROCKY_SHELL;
    public static Item ROCKY_CHESTPLATE;
    public static Item POTTED_FLUTTER;
    public static Item TERRAPIN_BUCKET;
    public static Item COMB_JELLY_BUCKET;
    public static Item RAINBOW_JELLY;
    public static Item COSMIC_COD_BUCKET;
    public static Item MUNGAL_SPORES;
    public static Item BISON_FUR;
    public static Item LOST_TENTACLE;
    public static Item SQUID_GRAPPLE;
    public static Item DEVILS_HOLE_PUPFISH_BUCKET;
    public static Item PUPFISH_LOCATOR;
    public static Item SMALL_CATFISH_BUCKET;
    public static Item MEDIUM_CATFISH_BUCKET;
    public static Item LARGE_CATFISH_BUCKET;
    public static Item RAW_CATFISH;
    public static Item COOKED_CATFISH;
    public static Item FLYING_FISH;
    public static Item FLYING_FISH_BOOTS;
    public static Item FLYING_FISH_BUCKET;
    public static Item FISH_BONES;
    public static Item SKELEWAG_SWORD_INVENTORY;
    public static Item SKELEWAG_SWORD_HAND;
    public static Item SKELEWAG_SWORD;
    public static Item NOVELTY_HAT;
    public static Item MUDSKIPPER_BUCKET;
    public static Item FARSEER_ARM;
    public static Item SKREECHER_SOUL;
    public static Item GHOSTLY_PICKAXE;
    public static Item ELASTIC_TENDON;
    public static Item TENDON_WHIP;
    public static Item UNSETTLING_KIMONO;
    public static Item STINK_BOTTLE;
    public static Item STINK_RAY_HAND;
    public static Item STINK_RAY_INVENTORY;
    public static Item STINK_RAY_EMPTY_HAND;
    public static Item STINK_RAY_EMPTY_INVENTORY;
    public static Item STINK_RAY;
    public static Item BANANA_SLUG_SLIME;
    public static Item MOSQUITO_REPELLENT_STEW;
    public static Item TRIOPS_BUCKET;
    public static Item MUSIC_DISC_THIME;
    public static Item MUSIC_DISC_DAZE;

    private static void initSpawnEggs() {
        register("spawn_egg_grizzly_bear", new SpawnEggItem(AMEntityRegistry.GRIZZLY_BEAR, 0x693A2C, 0x976144, new Item.Properties()));
        register("spawn_egg_roadrunner", new SpawnEggItem(AMEntityRegistry.ROADRUNNER, 0x3A2E26, 0xFBE9CE, new Item.Properties()));
        register("spawn_egg_bone_serpent", new SpawnEggItem(AMEntityRegistry.BONE_SERPENT, 0xE5D9C4, 0xFF6038, new Item.Properties()));
        register("spawn_egg_gazelle", new SpawnEggItem(AMEntityRegistry.GAZELLE, 0xDDA675, 0x2C2925, new Item.Properties()));
        register("spawn_egg_crocodile", new SpawnEggItem(AMEntityRegistry.CROCODILE, 0x738940, 0xA6A15E, new Item.Properties()));
        register("spawn_egg_fly", new SpawnEggItem(AMEntityRegistry.FLY, 0x464241, 0x892E2E, new Item.Properties()));
        register("spawn_egg_hummingbird", new SpawnEggItem(AMEntityRegistry.HUMMINGBIRD, 0x325E7F, 0x44A75F, new Item.Properties()));
        register("spawn_egg_orca", new SpawnEggItem(AMEntityRegistry.ORCA, 0x2C2C2C, 0xD6D8E4, new Item.Properties()));
        register("spawn_egg_sunbird", new SpawnEggItem(AMEntityRegistry.SUNBIRD, 0xF6694F, 0xFFDDA0, new Item.Properties()));
        register("spawn_egg_gorilla", new SpawnEggItem(AMEntityRegistry.GORILLA, 0x595B5D, 0x1C1C21, new Item.Properties()));
        register("spawn_egg_crimson_mosquito", new SpawnEggItem(AMEntityRegistry.CRIMSON_MOSQUITO, 0x53403F, 0xC11A1A, new Item.Properties()));
        register("spawn_egg_rattlesnake", new SpawnEggItem(AMEntityRegistry.RATTLESNAKE, 0xCEB994, 0x937A5B, new Item.Properties()));
        register("spawn_egg_endergrade", new SpawnEggItem(AMEntityRegistry.ENDERGRADE, 0x7862B3, 0x81BDEB, new Item.Properties()));
        register("spawn_egg_hammerhead_shark", new SpawnEggItem(AMEntityRegistry.HAMMERHEAD_SHARK, 0x8A92B5, 0xB9BED8, new Item.Properties()));
        register("spawn_egg_lobster", new SpawnEggItem(AMEntityRegistry.LOBSTER, 0xC43123, 0xDD5F38, new Item.Properties()));
        register("spawn_egg_komodo_dragon", new SpawnEggItem(AMEntityRegistry.KOMODO_DRAGON, 0x746C4F, 0x564231, new Item.Properties()));
        register("spawn_egg_capuchin_monkey", new SpawnEggItem(AMEntityRegistry.CAPUCHIN_MONKEY, 0x25211F, 0xF1DAB3, new Item.Properties()));
        register("spawn_egg_centipede", new SpawnEggItem(AMEntityRegistry.CENTIPEDE_HEAD, 0x342B2E, 0x733449, new Item.Properties()));
        register("spawn_egg_warped_toad", new SpawnEggItem(AMEntityRegistry.WARPED_TOAD, 0x1F968E, 0xFEAC6D, new Item.Properties()));
        register("spawn_egg_moose", new SpawnEggItem(AMEntityRegistry.MOOSE, 0x36302A, 0xD4B183, new Item.Properties()));
        register("spawn_egg_mimicube", new SpawnEggItem(AMEntityRegistry.MIMICUBE, 0x8A80C1, 0x5E4F6F, new Item.Properties()));
        register("spawn_egg_raccoon", new SpawnEggItem(AMEntityRegistry.RACCOON, 0x85827E, 0x2A2726, new Item.Properties()));
        register("spawn_egg_blobfish", new SpawnEggItem(AMEntityRegistry.BLOBFISH, 0xDBC6BD, 0x9E7A7F, new Item.Properties()));
        register("spawn_egg_seal", new SpawnEggItem(AMEntityRegistry.SEAL, 0x483C32, 0x66594C, new Item.Properties()));
        register("spawn_egg_cockroach", new SpawnEggItem(AMEntityRegistry.COCKROACH, 0x0D0909, 0x42241E, new Item.Properties()));
        register("spawn_egg_shoebill", new SpawnEggItem(AMEntityRegistry.SHOEBILL, 0x828282, 0xD5B48A, new Item.Properties()));
        register("spawn_egg_elephant", new SpawnEggItem(AMEntityRegistry.ELEPHANT, 0x8D8987, 0xEDE5D1, new Item.Properties()));
        register("spawn_egg_soul_vulture", new SpawnEggItem(AMEntityRegistry.SOUL_VULTURE, 0x23262D, 0x57F4FF, new Item.Properties()));
        register("spawn_egg_snow_leopard", new SpawnEggItem(AMEntityRegistry.SNOW_LEOPARD, 0xACA293, 0x26201D, new Item.Properties()));
        register("spawn_egg_spectre", new SpawnEggItem(AMEntityRegistry.SPECTRE, 0xC8D0EF, 0x8791EF, new Item.Properties()));
        register("spawn_egg_crow", new SpawnEggItem(AMEntityRegistry.CROW, 0x0D111C, 0x1C2030, new Item.Properties()));
        register("spawn_egg_alligator_snapping_turtle", new SpawnEggItem(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE, 0x6C5C52, 0x456926, new Item.Properties()));
        register("spawn_egg_mungus", new SpawnEggItem(AMEntityRegistry.MUNGUS, 0x836A8D, 0x45454C, new Item.Properties()));
        register("spawn_egg_mantis_shrimp", new SpawnEggItem(AMEntityRegistry.MANTIS_SHRIMP, 0xDB4858, 0x15991E, new Item.Properties()));
        register("spawn_egg_guster", new SpawnEggItem(AMEntityRegistry.GUSTER, 0xF8D49A, 0xFF720A, new Item.Properties()));
        register("spawn_egg_warped_mosco", new SpawnEggItem(AMEntityRegistry.WARPED_MOSCO, 0x322F58, 0x5B5EF1, new Item.Properties()));
        register("spawn_egg_straddler", new SpawnEggItem(AMEntityRegistry.STRADDLER, 0x5D5F6E, 0xCDA886, new Item.Properties()));
        register("spawn_egg_stradpole", new SpawnEggItem(AMEntityRegistry.STRADPOLE, 0x5D5F6E, 0x576A8B, new Item.Properties()));
        register("spawn_egg_emu", new SpawnEggItem(AMEntityRegistry.EMU, 0x665346, 0x3B3938, new Item.Properties()));
        register("spawn_egg_platypus", new SpawnEggItem(AMEntityRegistry.PLATYPUS, 0x7D503E, 0x363B43, new Item.Properties()));
        register("spawn_egg_dropbear", new SpawnEggItem(AMEntityRegistry.DROPBEAR, 0x8A2D35, 0x60A3A3, new Item.Properties()));
        register("spawn_egg_tasmanian_devil", new SpawnEggItem(AMEntityRegistry.TASMANIAN_DEVIL, 0x252426, 0xA8B4BF, new Item.Properties()));
        register("spawn_egg_kangaroo", new SpawnEggItem(AMEntityRegistry.KANGAROO, 0xCE9D65, 0xDEBDA0, new Item.Properties()));
        register("spawn_egg_cachalot_whale", new SpawnEggItem(AMEntityRegistry.CACHALOT_WHALE, 0x949899, 0x5F666E, new Item.Properties()));
        register("spawn_egg_leafcutter_ant", new SpawnEggItem(AMEntityRegistry.LEAFCUTTER_ANT, 0x964023, 0xA65930, new Item.Properties()));
        register("spawn_egg_enderiophage", new SpawnEggItem(AMEntityRegistry.ENDERIOPHAGE, 0x872D83, 0xF6E2CD, new Item.Properties()));
        register("spawn_egg_bald_eagle", new SpawnEggItem(AMEntityRegistry.BALD_EAGLE, 0x321F18, 0xF4F4F4, new Item.Properties()));
        register("spawn_egg_tiger", new SpawnEggItem(AMEntityRegistry.TIGER, 0xC7612E, 0x2A3233, new Item.Properties()));
        register("spawn_egg_tarantula_hawk", new SpawnEggItem(AMEntityRegistry.TARANTULA_HAWK, 0x234763, 0xE37B38, new Item.Properties()));
        register("spawn_egg_void_worm", new SpawnEggItem(AMEntityRegistry.VOID_WORM, 0x0F1026, 0x1699AB, new Item.Properties()));
        register("spawn_egg_frilled_shark", new SpawnEggItem(AMEntityRegistry.FRILLED_SHARK, 0x726B6B, 0x873D3D, new Item.Properties()));
        register("spawn_egg_mimic_octopus", new SpawnEggItem(AMEntityRegistry.MIMIC_OCTOPUS, 0xFFEBDC, 0x1D1C1F, new Item.Properties()));
        register("spawn_egg_seagull", new SpawnEggItem(AMEntityRegistry.SEAGULL, 0xC9D2DC, 0xFFD850, new Item.Properties()));
        register("spawn_egg_froststalker", new SpawnEggItem(AMEntityRegistry.FROSTSTALKER, 0x788AC1, 0xA1C3FF, new Item.Properties()));
        register("spawn_egg_tusklin", new SpawnEggItem(AMEntityRegistry.TUSKLIN, 0x735841, 0xE8E2D5, new Item.Properties()));
        register("spawn_egg_laviathan", new SpawnEggItem(AMEntityRegistry.LAVIATHAN, 0xD68356, 0x3C3947, new Item.Properties()));
        register("spawn_egg_cosmaw", new SpawnEggItem(AMEntityRegistry.COSMAW, 0x746DBD, 0xD6BFE3, new Item.Properties()));
        register("spawn_egg_toucan", new SpawnEggItem(AMEntityRegistry.TOUCAN, 0xF58F33, 0x1E2133, new Item.Properties()));
        register("spawn_egg_maned_wolf", new SpawnEggItem(AMEntityRegistry.MANED_WOLF, 0xBB7A47, 0x40271A, new Item.Properties()));
        register("spawn_egg_anaconda", new SpawnEggItem(AMEntityRegistry.ANACONDA, 0x565C22, 0xD3763F, new Item.Properties()));
        register("spawn_egg_anteater", new SpawnEggItem(AMEntityRegistry.ANTEATER, 0x4C3F3A, 0xCCBCB4, new Item.Properties()));
        register("spawn_egg_rocky_roller", new SpawnEggItem(AMEntityRegistry.ROCKY_ROLLER, 0xB0856F, 0x999184, new Item.Properties()));
        register("spawn_egg_flutter", new SpawnEggItem(AMEntityRegistry.FLUTTER, 0x70922D, 0xD07BE3, new Item.Properties()));
        register("spawn_egg_gelada_monkey", new SpawnEggItem(AMEntityRegistry.GELADA_MONKEY, 0xB08C64, 0xFF4F53, new Item.Properties()));
        register("spawn_egg_jerboa", new SpawnEggItem(AMEntityRegistry.JERBOA, 0xDEC58A, 0xDE9D90, new Item.Properties()));
        register("spawn_egg_terrapin", new SpawnEggItem(AMEntityRegistry.TERRAPIN, 0x6E6E30, 0x929647, new Item.Properties()));
        register("spawn_egg_comb_jelly", new SpawnEggItem(AMEntityRegistry.COMB_JELLY, 0xCFE9FE, 0x6EFF8B, new Item.Properties()));
        register("spawn_egg_cosmic_cod", new SpawnEggItem(AMEntityRegistry.COSMIC_COD, 0x6985C7, 0xE2D1FF, new Item.Properties()));
        register("spawn_egg_bunfungus", new SpawnEggItem(AMEntityRegistry.BUNFUNGUS, 0x6F6D91, 0xC92B29, new Item.Properties()));
        register("spawn_egg_bison", new SpawnEggItem(AMEntityRegistry.BISON, 0x4C3A2E, 0x7A6546, new Item.Properties()));
        register("spawn_egg_giant_squid", new SpawnEggItem(AMEntityRegistry.GIANT_SQUID, 0xAB4B4D, 0xD67D6B, new Item.Properties()));
        register("spawn_egg_devils_hole_pupfish", new SpawnEggItem(AMEntityRegistry.DEVILS_HOLE_PUPFISH, 0x567BC4, 0x6C4475, new Item.Properties()));
        register("spawn_egg_catfish", new SpawnEggItem(AMEntityRegistry.CATFISH, 0x807757, 0x8A7466, new Item.Properties()));
        register("spawn_egg_flying_fish", new SpawnEggItem(AMEntityRegistry.FLYING_FISH, 0x7BBCED, 0x6881B3, new Item.Properties()));
        register("spawn_egg_skelewag", new SpawnEggItem(AMEntityRegistry.SKELEWAG, 0xD9FCB1, 0x3A4F30, new Item.Properties()));
        register("spawn_egg_rain_frog", new SpawnEggItem(AMEntityRegistry.RAIN_FROG, 0xC0B59B, 0x7B654F, new Item.Properties()));
        register("spawn_egg_potoo", new SpawnEggItem(AMEntityRegistry.POTOO, 0x8C7753, 0xFFC042, new Item.Properties()));
        register("spawn_egg_mudskipper", new SpawnEggItem(AMEntityRegistry.MUDSKIPPER, 0x60704A, 0x49806C, new Item.Properties()));
        register("spawn_egg_rhinoceros", new SpawnEggItem(AMEntityRegistry.RHINOCEROS, 0xA19594, 0x827474, new Item.Properties()));
        register("spawn_egg_sugar_glider", new SpawnEggItem(AMEntityRegistry.SUGAR_GLIDER, 0x868181, 0xEBEBE0, new Item.Properties()));
        register("spawn_egg_farseer", new SpawnEggItem(AMEntityRegistry.FARSEER, 0x33374F, 0x91FF59, new Item.Properties()));
        register("spawn_egg_skreecher", new SpawnEggItem(AMEntityRegistry.SKREECHER, 0x074857, 0x7FF8FF, new Item.Properties()));
        register("spawn_egg_underminer", new SpawnEggItem(AMEntityRegistry.UNDERMINER, 0xD6E2FF, 0x6C84C4, new Item.Properties()));
        register("spawn_egg_murmur", new SpawnEggItem(AMEntityRegistry.MURMUR, 0x804448, 0xB5AF9C, new Item.Properties()));
        register("spawn_egg_skunk", new SpawnEggItem(AMEntityRegistry.SKUNK, 0x222D36, 0xE4E5F2, new Item.Properties()));
        register("spawn_egg_banana_slug", new SpawnEggItem(AMEntityRegistry.BANANA_SLUG, 0xFFD045, 0xFFF173, new Item.Properties()));
        register("spawn_egg_blue_jay", new SpawnEggItem(AMEntityRegistry.BLUE_JAY, 0x5FB7FE, 0x293B42, new Item.Properties()));
        register("spawn_egg_caiman", new SpawnEggItem(AMEntityRegistry.CAIMAN, 0x5C5631, 0xBBC45C, new Item.Properties()));
        register("spawn_egg_triops", new SpawnEggItem(AMEntityRegistry.TRIOPS, 0x967954, 0xCA7150, new Item.Properties()));
        registerPatternItem("bear");
        registerPatternItem("australia_0");
        registerPatternItem("australia_1");
        registerPatternItem("new_mexico");
        registerPatternItem("brazil");
        for (int i = 0; i <= 10; i++) {
            register("dimensional_carver_shard_" + i, new ItemInventoryOnly(new Item.Properties()));
        }
    }

    private static void registerArmorMaterials() {
        AMArmorMaterial[] materials = new AMArmorMaterial[]{
            ROADRUNNER_ARMOR_MATERIAL, CROCODILE_ARMOR_MATERIAL, CENTIPEDE_ARMOR_MATERIAL, MOOSE_ARMOR_MATERIAL,
            RACCOON_ARMOR_MATERIAL, SOMBRERO_ARMOR_MATERIAL, SPIKED_TURTLE_SHELL_ARMOR_MATERIAL, FEDORA_ARMOR_MATERIAL,
            EMU_ARMOR_MATERIAL, TARANTULA_HAWK_ELYTRA_MATERIAL, FROSTSTALKER_ARMOR_MATERIAL, ROCKY_ARMOR_MATERIAL,
            FLYING_FISH_MATERIAL, NOVELTY_HAT_MATERIAL, KIMONO_MATERIAL
        };
        for (AMArmorMaterial mat : materials) {
            ResourceLocation loc = id(mat.getName());
            Registry.register(BuiltInRegistries.ARMOR_MATERIAL, loc, mat.getMaterial());
            mat.setHolder(BuiltInRegistries.ARMOR_MATERIAL.getHolderOrThrow(ResourceKey.create(Registries.ARMOR_MATERIAL, loc)));
        }
    }

    private static void registerPatternItem(String name) {
        TagKey<BannerPattern> bannerPatternTagKey = TagKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "pattern_for_" + name));
        register("banner_pattern_" + name, new BannerPatternItem(bannerPatternTagKey, (new Item.Properties()).stacksTo(1)));
    }

    public static void init() {
        registerArmorMaterials();
        SWIM_SPEED_ATTRIBUTE = Registry.register(BuiltInRegistries.ATTRIBUTE, id("swim_speed"), new RangedAttribute("attribute.name.generic.alexsmobs.swim_speed", 1.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
        BLOCK_REACH_ATTRIBUTE = Registry.register(BuiltInRegistries.ATTRIBUTE, id("block_reach"), new RangedAttribute("attribute.name.generic.alexsmobs.block_reach", 4.5D, 0.0D, 1024.0D).setSyncable(true));
        ENTITY_REACH_ATTRIBUTE = Registry.register(BuiltInRegistries.ATTRIBUTE, id("entity_reach"), new RangedAttribute("attribute.name.generic.alexsmobs.entity_reach", 3.0D, 0.0D, 1024.0D).setSyncable(true));

        TAB_ICON = register("tab_icon", new ItemTabIcon(new Item.Properties()));
        ANIMAL_DICTIONARY = register("animal_dictionary", new ItemAnimalDictionary(new Item.Properties().stacksTo(1)));
        BEAR_FUR = register("bear_fur", new Item(new Item.Properties()));
        BEAR_DUST = register("bear_dust", new ItemBearDust(new Item.Properties().rarity(Rarity.EPIC)));
        ROADRUNNER_FEATHER = register("roadrunner_feather", new Item(new Item.Properties()));
        ROADDRUNNER_BOOTS = register("roadrunner_boots", new ItemModArmor(ROADRUNNER_ARMOR_MATERIAL, ArmorItem.Type.BOOTS));
        LAVA_BOTTLE = register("lava_bottle", new Item(new Item.Properties().stacksTo(1)));
        BONE_SERPENT_TOOTH = register("bone_serpent_tooth", new Item(new Item.Properties().fireResistant()));
        GAZELLE_HORN = register("gazelle_horn", new Item(new Item.Properties().fireResistant()));
        CROCODILE_SCUTE = register("crocodile_scute", new Item(new Item.Properties()));
        CROCODILE_CHESTPLATE = register("crocodile_chestplate", new ItemModArmor(CROCODILE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE));
        MAGGOT = register("maggot", new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.2F).build())));
        BANANA = register("banana", new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build())));
        ANCIENT_DART = register("ancient_dart", new Item(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
        HALO = register("halo", new ItemInventoryOnly(new Item.Properties()));
        BLOOD_SAC = register("blood_sac", new Item(new Item.Properties()));
        MOSQUITO_PROBOSCIS = register("mosquito_proboscis", new Item(new Item.Properties()));
        BLOOD_SPRAYER = register("blood_sprayer", new ItemBloodSprayer(new Item.Properties().durability(100)));
        RATTLESNAKE_RATTLE = register("rattlesnake_rattle", new Item(new Item.Properties()));
        CHORUS_ON_A_STICK = register("chorus_on_a_stick", new Item(new Item.Properties().stacksTo(1)));
        SHARK_TOOTH = register("shark_tooth", new Item(new Item.Properties()));
        SHARK_TOOTH_ARROW = register("shark_tooth_arrow", new ItemModArrow(new Item.Properties()));
        LOBSTER_TAIL = register("lobster_tail", new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.4F).build())));
        COOKED_LOBSTER_TAIL = register("cooked_lobster_tail", new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.65F).build())));
        LOBSTER_BUCKET = register("lobster_bucket", new ItemModFishBucket(() -> AMEntityRegistry.LOBSTER, Fluids.WATER, new Item.Properties()));
        KOMODO_SPIT = register("komodo_spit", new Item(new Item.Properties()));
        KOMODO_SPIT_BOTTLE = register("komodo_spit_bottle", new Item(new Item.Properties()));
        POISON_BOTTLE = register("poison_bottle", new Item(new Item.Properties()));
        SOPA_DE_MACACO = register("sopa_de_macaco", new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.4F).usingConvertsTo(Items.BOWL).build()).stacksTo(1)));
        CENTIPEDE_LEG = register("centipede_leg", new Item(new Item.Properties()));
        CENTIPEDE_LEGGINGS = register("centipede_leggings", new ItemModArmor(CENTIPEDE_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS));
        MOSQUITO_LARVA = register("mosquito_larva", new Item(new Item.Properties()));
        MOOSE_ANTLER = register("moose_antler", new Item(new Item.Properties()));
        MOOSE_HEADGEAR = register("moose_headgear", new ItemModArmor(MOOSE_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
        MOOSE_RIBS = register("moose_ribs", new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.6F).build())));
        COOKED_MOOSE_RIBS = register("cooked_moose_ribs", new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(7).saturationModifier(0.85F).build())));
        MIMICREAM = register("mimicream", new Item(new Item.Properties()));
        RACCOON_TAIL = register("raccoon_tail", new Item(new Item.Properties()));
        FRONTIER_CAP = register("frontier_cap", new ItemModArmor(RACCOON_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
        BLOBFISH = register("blobfish", new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.4F).effect(new MobEffectInstance(MobEffects.POISON, 120, 0), 1.0F).build())));
        BLOBFISH_BUCKET = register("blobfish_bucket", new ItemModFishBucket(() -> AMEntityRegistry.BLOBFISH, Fluids.WATER, new Item.Properties()));
        FISH_OIL = register("fish_oil", new ItemFishOil(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).food(new FoodProperties.Builder().nutrition(0).saturationModifier(0.2F).build())));
        MARACA = register("maraca", new ItemMaraca(new Item.Properties()));
        SOMBRERO = register("sombrero", new ItemModArmor(SOMBRERO_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
        COCKROACH_WING_FRAGMENT = register("cockroach_wing_fragment", new Item(new Item.Properties()));
        COCKROACH_WING = register("cockroach_wing", new Item(new Item.Properties()));
        COCKROACH_OOTHECA = register("cockroach_ootheca", new ItemAnimalEgg(new Item.Properties()));
        ACACIA_BLOSSOM = register("acacia_blossom", new Item(new Item.Properties()));
        SOUL_HEART = register("soul_heart", new Item(new Item.Properties()));
        SPIKED_SCUTE = register("spiked_scute", new Item(new Item.Properties()));
        SPIKED_TURTLE_SHELL = register("spiked_turtle_shell", new ItemModArmor(SPIKED_TURTLE_SHELL_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
        SHRIMP_FRIED_RICE = register("shrimp_fried_rice", new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(12).saturationModifier(1F).build())));
        GUSTER_EYE = register("guster_eye", new Item(new Item.Properties()));
        POCKET_SAND = register("pocket_sand", new ItemPocketSand(new Item.Properties().durability(220)));
        WARPED_MUSCLE = register("warped_muscle", new Item(new Item.Properties()));
        HEMOLYMPH_SAC = register("hemolymph_sac", new Item(new Item.Properties()));
        HEMOLYMPH_BLASTER = register("hemolymph_blaster", new ItemHemolymphBlaster(new Item.Properties().durability(150)));
        WARPED_MIXTURE = register("warped_mixture", new Item(new Item.Properties().rarity(Rarity.RARE).stacksTo(1).craftRemainder(Items.GLASS_BOTTLE)));
        STRADDLITE = register("straddlite", new Item(new Item.Properties().fireResistant()));
        STRADPOLE_BUCKET = register("stradpole_bucket", new ItemModFishBucket(() -> AMEntityRegistry.STRADPOLE, Fluids.LAVA, new Item.Properties()));
        STRADDLEBOARD = register("straddleboard", new ItemStraddleboard(new Item.Properties().fireResistant().durability(220)));
        EMU_EGG = register("emu_egg", new ItemAnimalEgg(new Item.Properties().stacksTo(8)));
        BOILED_EMU_EGG = register("boiled_emu_egg", new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(1F).build())));
        EMU_FEATHER = register("emu_feather", new Item(new Item.Properties().fireResistant()));
        EMU_LEGGINGS = register("emu_leggings", new ItemModArmor(EMU_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS));
        PLATYPUS_BUCKET = register("platypus_bucket", new ItemModFishBucket(() -> AMEntityRegistry.PLATYPUS, Fluids.WATER, new Item.Properties()));
        FEDORA = register("fedora", new ItemModArmor(FEDORA_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
        DROPBEAR_CLAW = register("dropbear_claw", new Item(new Item.Properties()));
        KANGAROO_MEAT = register("kangaroo_meat", new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.6F).build())));
        COOKED_KANGAROO_MEAT = register("cooked_kangaroo_meat", new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.85F).build())));
        KANGAROO_HIDE = register("kangaroo_hide", new Item(new Item.Properties()));
        KANGAROO_BURGER = register("kangaroo_burger", new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(12).saturationModifier(1F).build())));
        AMBERGRIS = register("ambergris", new ItemFuel(new Item.Properties(), 12800));
        CACHALOT_WHALE_TOOTH = register("cachalot_whale_tooth", new Item(new Item.Properties()));
        ECHOLOCATOR = register("echolocator", new ItemEcholocator(new Item.Properties().durability(100), ItemEcholocator.EchoType.ECHOLOCATION));
        ENDOLOCATOR = register("endolocator", new ItemEcholocator(new Item.Properties().durability(25), ItemEcholocator.EchoType.ENDER));
        GONGYLIDIA = register("gongylidia", new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(1.2F).build())));
        LEAFCUTTER_ANT_PUPA = register("leafcutter_ant_pupa", new ItemLeafcutterPupa(new Item.Properties()));
        ENDERIOPHAGE_ROCKET = register("enderiophage_rocket", new ItemEnderiophageRocket(new Item.Properties()));
        FALCONRY_GLOVE_INVENTORY = register("falconry_glove_inventory", new ItemInventoryOnly(new Item.Properties()));
        FALCONRY_GLOVE_HAND = register("falconry_glove_hand", new ItemInventoryOnly(new Item.Properties()));
        FALCONRY_GLOVE = register("falconry_glove", new ItemFalconryGlove(new Item.Properties().stacksTo(1)));
        FALCONRY_HOOD = register("falconry_hood", new Item(new Item.Properties()));
        TARANTULA_HAWK_WING_FRAGMENT = register("tarantula_hawk_wing_fragment", new Item(new Item.Properties()));
        TARANTULA_HAWK_WING = register("tarantula_hawk_wing", new Item(new Item.Properties()));
        TARANTULA_HAWK_ELYTRA = register("tarantula_hawk_elytra", new ItemTarantulaHawkElytra(new Item.Properties().durability(800).rarity(Rarity.UNCOMMON), TARANTULA_HAWK_ELYTRA_MATERIAL));
        MYSTERIOUS_WORM = register("mysterious_worm", new ItemMysteriousWorm(new Item.Properties().rarity(Rarity.RARE)));
        VOID_WORM_MANDIBLE = register("void_worm_mandible", new Item(new Item.Properties()));
        VOID_WORM_EYE = register("void_worm_eye", new Item(new Item.Properties().rarity(Rarity.RARE)));
        DIMENSIONAL_CARVER = register("dimensional_carver", new ItemDimensionalCarver(new Item.Properties().durability(20).rarity(Rarity.EPIC)));
        SHATTERED_DIMENSIONAL_CARVER = register("shattered_dimensional_carver", new ItemShatteredDimensionalCarver(new Item.Properties().durability(4).rarity(Rarity.RARE)));
        SERRATED_SHARK_TOOTH = register("serrated_shark_tooth", new Item(new Item.Properties()));
        FRILLED_SHARK_BUCKET = register("frilled_shark_bucket", new ItemModFishBucket(() -> AMEntityRegistry.FRILLED_SHARK, Fluids.WATER, new Item.Properties()));
        SHIELD_OF_THE_DEEP = register("shield_of_the_deep", new ItemShieldOfTheDeep(new Item.Properties().durability(400).rarity(Rarity.UNCOMMON)));
        MIMIC_OCTOPUS_BUCKET = register("mimic_octopus_bucket", new ItemModFishBucket(() -> AMEntityRegistry.MIMIC_OCTOPUS, Fluids.WATER, new Item.Properties()));
        FROSTSTALKER_HORN = register("froststalker_horn", new Item(new Item.Properties()));
        FROSTSTALKER_HELMET = register("froststalker_helmet", new ItemModArmor(FROSTSTALKER_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
        PIGSHOES = register("pigshoes", new ItemPigshoes(new Item.Properties().stacksTo(1)));
        STRADDLE_HELMET = register("straddle_helmet", new Item(new Item.Properties().fireResistant()));
        STRADDLE_SADDLE = register("straddle_saddle", new Item(new Item.Properties().fireResistant()));
        COSMIC_COD = register("cosmic_cod", new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.3F).effect(new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.ENDER_FLU), 12000), 0.15F).build())));
        SHED_SNAKE_SKIN = register("shed_snake_skin", new Item(new Item.Properties()));
        VINE_LASSO_INVENTORY = register("vine_lasso_inventory", new ItemInventoryOnly(new Item.Properties()));
        VINE_LASSO_HAND = register("vine_lasso_hand", new ItemInventoryOnly(new Item.Properties()));
        VINE_LASSO = register("vine_lasso", new ItemVineLasso(new Item.Properties().stacksTo(1)));
        ROCKY_SHELL = register("rocky_shell", new Item(new Item.Properties()));
        ROCKY_CHESTPLATE = register("rocky_chestplate", new ItemModArmor(ROCKY_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE));
        POTTED_FLUTTER = register("potted_flutter", new ItemFlutterPot(new Item.Properties()));
        TERRAPIN_BUCKET = register("terrapin_bucket", new ItemModFishBucket(() -> AMEntityRegistry.TERRAPIN, Fluids.WATER, new Item.Properties()));
        COMB_JELLY_BUCKET = register("comb_jelly_bucket", new ItemModFishBucket(() -> AMEntityRegistry.COMB_JELLY, Fluids.WATER, new Item.Properties()));
        RAINBOW_JELLY = register("rainbow_jelly", new ItemRainbowJelly(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.2F).build())));
        COSMIC_COD_BUCKET = register("cosmic_cod_bucket", new ItemCosmicCodBucket(new Item.Properties()));
        MUNGAL_SPORES = register("mungal_spores", new Item(new Item.Properties()));
        BISON_FUR = register("bison_fur", new Item(new Item.Properties()));
        LOST_TENTACLE = register("lost_tentacle", new Item(new Item.Properties()));
        SQUID_GRAPPLE = register("squid_grapple", new ItemSquidGrapple(new Item.Properties().durability(450)));
        DEVILS_HOLE_PUPFISH_BUCKET = register("devils_hole_pupfish_bucket", new ItemModFishBucket(() -> AMEntityRegistry.DEVILS_HOLE_PUPFISH, Fluids.WATER, new Item.Properties()));
        PUPFISH_LOCATOR = register("pupfish_locator", new ItemEcholocator(new Item.Properties().durability(200), ItemEcholocator.EchoType.PUPFISH));
        SMALL_CATFISH_BUCKET = register("small_catfish_bucket", new ItemModFishBucket(() -> AMEntityRegistry.CATFISH, Fluids.WATER, new Item.Properties()));
        MEDIUM_CATFISH_BUCKET = register("medium_catfish_bucket", new ItemModFishBucket(() -> AMEntityRegistry.CATFISH, Fluids.WATER, new Item.Properties()));
        LARGE_CATFISH_BUCKET = register("large_catfish_bucket", new ItemModFishBucket(() -> AMEntityRegistry.CATFISH, Fluids.WATER, new Item.Properties()));
        RAW_CATFISH = register("raw_catfish", new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).build())));
        COOKED_CATFISH = register("cooked_catfish", new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.5F).build())));
        FLYING_FISH = register("flying_fish", new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.4F).build())));
        FLYING_FISH_BOOTS = register("flying_fish_boots", new ItemModArmor(FLYING_FISH_MATERIAL, ArmorItem.Type.BOOTS));
        FLYING_FISH_BUCKET = register("flying_fish_bucket", new ItemModFishBucket(() -> AMEntityRegistry.FLYING_FISH, Fluids.WATER, new Item.Properties()));
        FISH_BONES = register("fish_bones", new Item(new Item.Properties()));
        SKELEWAG_SWORD_INVENTORY = register("skelewag_sword_inventory", new ItemInventoryOnly(new Item.Properties()));
        SKELEWAG_SWORD_HAND = register("skelewag_sword_hand", new ItemInventoryOnly(new Item.Properties()));
        SKELEWAG_SWORD = register("skelewag_sword", new ItemSkelewagSword(new Item.Properties().stacksTo(1).durability(430)));
        NOVELTY_HAT = register("novelty_hat", new ItemModArmor(NOVELTY_HAT_MATERIAL, ArmorItem.Type.HELMET));
        MUDSKIPPER_BUCKET = register("mudskipper_bucket", new ItemModFishBucket(() -> AMEntityRegistry.MUDSKIPPER, Fluids.WATER, new Item.Properties()));
        FARSEER_ARM = register("farseer_arm", new Item(new Item.Properties().rarity(Rarity.RARE)));
        SKREECHER_SOUL = register("skreecher_soul", new Item(new Item.Properties()));
        GHOSTLY_PICKAXE = register("ghostly_pickaxe", new ItemGhostlyPickaxe(new Item.Properties()));
        ELASTIC_TENDON = register("elastic_tendon", new Item(new Item.Properties()));
        TENDON_WHIP = register("tendon_whip", new ItemTendonWhip(new Item.Properties()));
        UNSETTLING_KIMONO = register("unsettling_kimono", new ItemModArmor(KIMONO_MATERIAL, ArmorItem.Type.CHESTPLATE));
        STINK_BOTTLE = register("stink_bottle", new ItemStinkBottle(AMBlockRegistry.SKUNK_SPRAY, new Item.Properties().stacksTo(16)));
        STINK_RAY_HAND = register("stink_ray_hand", new ItemInventoryOnly(new Item.Properties()));
        STINK_RAY_INVENTORY = register("stink_ray_inventory", new ItemInventoryOnly(new Item.Properties()));
        STINK_RAY_EMPTY_HAND = register("stink_ray_empty_hand", new ItemInventoryOnly(new Item.Properties()));
        STINK_RAY_EMPTY_INVENTORY = register("stink_ray_empty_inventory", new ItemInventoryOnly(new Item.Properties()));
        STINK_RAY = register("stink_ray", new ItemStinkRay(new Item.Properties().durability(5)));
        BANANA_SLUG_SLIME = register("banana_slug_slime", new Item(new Item.Properties()));
        MOSQUITO_REPELLENT_STEW = register("mosquito_repellent_stew", new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).alwaysEdible().saturationModifier(0.3F).effect(new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.MOSQUITO_REPELLENT), 24000), 1.0F).usingConvertsTo(Items.BOWL).build()).stacksTo(1)));
        TRIOPS_BUCKET = register("triops_bucket", new ItemModFishBucket(() -> AMEntityRegistry.TRIOPS, Fluids.WATER, new Item.Properties()));
        MUSIC_DISC_THIME = register("music_disc_thime", new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
        MUSIC_DISC_DAZE = register("music_disc_daze", new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

        initSpawnEggs();

        CROCODILE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(CROCODILE_SCUTE));
        ROADRUNNER_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(ROADRUNNER_FEATHER));
        CENTIPEDE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(CENTIPEDE_LEG));
        MOOSE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(MOOSE_ANTLER));
        RACCOON_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(RACCOON_TAIL));
        SOMBRERO_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(Items.HAY_BLOCK));
        SPIKED_TURTLE_SHELL_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(SPIKED_SCUTE));
        FEDORA_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(Items.LEATHER));
        EMU_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(EMU_FEATHER));
        ROCKY_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(ROCKY_SHELL));
        FLYING_FISH_MATERIAL.setRepairMaterial(Ingredient.of(FLYING_FISH));
        NOVELTY_HAT_MATERIAL.setRepairMaterial(Ingredient.of(Items.BONE));
        KIMONO_MATERIAL.setRepairMaterial(Ingredient.of(ItemTags.WOOL));
        LecternBooks.BOOKS.put(BuiltInRegistries.ITEM.getKey(ANIMAL_DICTIONARY), new LecternBooks.BookData(0x606B26, 0xFDF8ED));
    }

    public static void initDispenser(){
        DispenserBlock.registerBehavior(SHARK_TOOTH_ARROW, createProjectileDispenseBehavior((level, pos, stack) -> {
            EntitySharkToothArrow entityarrow = new EntitySharkToothArrow(AMEntityRegistry.SHARK_TOOTH_ARROW, pos.x, pos.y, pos.z, level);
            entityarrow.pickup = EntitySharkToothArrow.Pickup.ALLOWED;
            return entityarrow;
        }));
        DispenserBlock.registerBehavior(ANCIENT_DART, createProjectileDispenseBehavior((level, pos, stack) -> {
            EntityTossedItem tossedItem = new EntityTossedItem(level, pos.x, pos.y, pos.z);
            tossedItem.setDart(true);
            return tossedItem;
        }));
        DispenserBlock.registerBehavior(COCKROACH_OOTHECA, createProjectileDispenseBehavior((level, pos, stack) ->
            new EntityCockroachEgg(level, pos.x, pos.y, pos.z)));
        DispenserBlock.registerBehavior(EMU_EGG, createProjectileDispenseBehavior((level, pos, stack) ->
            new EntityEmuEgg(level, pos.x, pos.y, pos.z)));
        DispenserBlock.registerBehavior(ENDERIOPHAGE_ROCKET, createProjectileDispenseBehavior((level, pos, stack) ->
            new EntityEnderiophageRocket(level, pos.x, pos.y, pos.z, stack)));
        DispenseItemBehavior bucketDispenseBehavior = new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            public ItemStack execute(BlockSource blockSource, ItemStack stack) {
                DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem)stack.getItem();
                BlockPos blockpos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                Level level = blockSource.level();
                if (dispensiblecontaineritem.emptyContents((Player)null, level, blockpos, (BlockHitResult)null)) {
                    dispensiblecontaineritem.checkExtraContent((Player)null, level, stack, blockpos);
                    return new ItemStack(Items.BUCKET);
                } else {
                    return this.defaultDispenseItemBehavior.dispense(blockSource, stack);
                }
            }
        };
        DispenserBlock.registerBehavior(LOBSTER_BUCKET, bucketDispenseBehavior);
        DispenserBlock.registerBehavior(BLOBFISH_BUCKET, bucketDispenseBehavior);
        DispenserBlock.registerBehavior(STRADPOLE_BUCKET, bucketDispenseBehavior);
        DispenserBlock.registerBehavior(PLATYPUS_BUCKET, bucketDispenseBehavior);
        DispenserBlock.registerBehavior(FRILLED_SHARK_BUCKET, bucketDispenseBehavior);
        DispenserBlock.registerBehavior(MIMIC_OCTOPUS_BUCKET, bucketDispenseBehavior);
        DispenserBlock.registerBehavior(TERRAPIN_BUCKET, bucketDispenseBehavior);
        DispenserBlock.registerBehavior(COMB_JELLY_BUCKET, bucketDispenseBehavior);
        DispenserBlock.registerBehavior(COSMIC_COD_BUCKET, bucketDispenseBehavior);
        DispenserBlock.registerBehavior(DEVILS_HOLE_PUPFISH_BUCKET, bucketDispenseBehavior);
        DispenserBlock.registerBehavior(SMALL_CATFISH_BUCKET, bucketDispenseBehavior);
        DispenserBlock.registerBehavior(MEDIUM_CATFISH_BUCKET, bucketDispenseBehavior);
        DispenserBlock.registerBehavior(LARGE_CATFISH_BUCKET, bucketDispenseBehavior);
        DispenserBlock.registerBehavior(FLYING_FISH_BUCKET, bucketDispenseBehavior);
        DispenserBlock.registerBehavior(MUDSKIPPER_BUCKET, bucketDispenseBehavior);
        ComposterBlock.COMPOSTABLES.put(BANANA, 0.65F);
        ComposterBlock.COMPOSTABLES.put(AMBlockRegistry.BANANA_PEEL.asItem(), 1F);
        ComposterBlock.COMPOSTABLES.put(ACACIA_BLOSSOM, 0.65F);
        ComposterBlock.COMPOSTABLES.put(GONGYLIDIA, 0.9F);
    }

    /** Creates a dispense behavior that spawns a custom projectile (1.21.1: AbstractProjectileDispenseBehavior removed). */
    private static DispenseItemBehavior createProjectileDispenseBehavior(ProjectileFactory factory) {
        return new DefaultDispenseItemBehavior() {
            @Override
            public ItemStack execute(BlockSource source, ItemStack stack) {
                Level level = source.level();
                Direction facing = source.state().getValue(DispenserBlock.FACING);
                BlockPos front = source.pos().relative(facing);
                Vec3 pos = Vec3.atCenterOf(front);
                Projectile projectile = factory.create(level, pos, stack);
                projectile.setPos(pos.x, pos.y, pos.z);
                Vec3 dir = Vec3.atLowerCornerOf(facing.getNormal());
                projectile.shoot(dir.x, dir.y, dir.z, 1.0F, 6.0F);
                level.addFreshEntity(projectile);
                stack.shrink(1);
                return stack;
            }
        };
    }

    @FunctionalInterface
    private interface ProjectileFactory {
        Projectile create(Level level, Vec3 pos, ItemStack stack);
    }

}
