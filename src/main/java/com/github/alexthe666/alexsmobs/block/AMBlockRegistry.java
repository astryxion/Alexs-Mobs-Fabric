package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.item.AMBlockItem;
import com.github.alexthe666.alexsmobs.item.BlockItemAMRender;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class AMBlockRegistry {
    public static final BlockBehaviour.Properties PURPUR_PLANKS_PROPERTIES = BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(0.5F, 1.0F).sound(SoundType.WOOD);

    private static ResourceLocation id(String path) {
        return new ResourceLocation(AlexsMobs.MODID, path);
    }

    public static Block BANANA_PEEL;
    public static Block HUMMINGBIRD_FEEDER;
    public static Block CROCODILE_EGG;
    public static Block GUSTMAKER;
    public static Block STRADDLITE_BLOCK;
    public static Block PLATYPUS_EGG;
    public static Block LEAFCUTTER_ANTHILL;
    public static Block LEAFCUTTER_ANT_CHAMBER;
    public static Block CAPSID;
    public static Block VOID_WORM_BEAK;
    public static Block VOID_WORM_EFFIGY;
    public static Block TERRAPIN_EGG;
    public static Block RAINBOW_GLASS;
    public static Block BISON_FUR_BLOCK;
    public static Block BISON_CARPET;
    public static Block SAND_CIRCLE;
    public static Block RED_SAND_CIRCLE;
    public static Block ENDER_RESIDUE;
    public static Block TRANSMUTATION_TABLE;
    public static Block SCULK_BOOMER;
    public static Block SKUNK_SPRAY;
    public static Block BANANA_SLUG_SLIME_BLOCK;
    public static Block CRYSTALIZED_BANANA_SLUG_MUCUS;
    public static Block CAIMAN_EGG;
    public static Block TRIOPS_EGGS;
    /*
        public static final RegistryObject<Block> PURPUR_PLANKS = registerBlockAndItem("purpur_planks", () -> new Block(PURPUR_PLANKS_PROPERTIES));;
    public static final RegistryObject<Block> PURPUR_PLANKS_STAIRS = registerBlockAndItem("purpur_planks_stairs", () -> new StairBlock(PURPUR_PLANKS.get().defaultBlockState(), PURPUR_PLANKS_PROPERTIES));;
    public static final RegistryObject<Block> PURPUR_PLANKS_SLAB = registerBlockAndItem("purpur_planks_slab", () -> new SlabBlock(PURPUR_PLANKS_PROPERTIES));;
    public static final RegistryObject<Block> PURPUR_PLANKS_WALL = registerBlockAndItem("purpur_planks_wall", () -> new WallBlock(PURPUR_PLANKS_PROPERTIES));;
    public static final RegistryObject<Block> END_PIRATE_DOOR = registerBlockAndItem("end_pirate_door", () -> new BlockEndPirateDoor());
    public static final RegistryObject<Block> END_PIRATE_TRAPDOOR = registerBlockAndItem("end_pirate_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.TERRACOTTA_PURPLE).lightLevel((state) -> 3).strength(3.0F).sound(SoundType.GLASS).noOcclusion()));;
    public static final RegistryObject<Block> END_PIRATE_ANCHOR = registerBlockAndItem("end_pirate_anchor", () -> new BlockEndPirateAnchor());
    public static final RegistryObject<Block> END_PIRATE_ANCHOR_WINCH = registerBlockAndItem("end_pirate_anchor_winch", () -> new BlockEndPirateAnchorWinch());
    public static final RegistryObject<Block> END_PIRATE_SHIP_WHEEL = registerBlockAndItem("end_pirate_ship_wheel", () -> new BlockEndPirateShipWheel());
    public static final RegistryObject<Block> END_PIRATE_FLAG = registerBlockAndItem("end_pirate_flag", () -> new BlockEndPirateFlag());
    public static final RegistryObject<Block> PHANTOM_SAIL = registerBlockAndItem("phantom_sail", () -> new BlockEndPirateSail(false));
    public static final RegistryObject<Block> SPECTRE_SAIL = registerBlockAndItem("spectre_sail", () -> new BlockEndPirateSail(true));

     */

    public static void init() {
        BANANA_PEEL = registerBlockAndItem("banana_peel", new BlockBananaPeel());
        HUMMINGBIRD_FEEDER = registerBlockAndItem("hummingbird_feeder", new BlockHummingbirdFeeder());
        CROCODILE_EGG = registerBlockAndItem("crocodile_egg", new BlockReptileEgg(AMEntityRegistry.CROCODILE));
        GUSTMAKER = registerBlockAndItem("gustmaker", new BlockGustmaker());
        STRADDLITE_BLOCK = registerBlockAndItem("straddlite_block", new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(1.0F, 1200.0F).sound(SoundType.ANCIENT_DEBRIS)), new Item.Properties().fireResistant(), false);
        PLATYPUS_EGG = registerBlockAndItem("platypus_egg", new BlockReptileEgg(AMEntityRegistry.PLATYPUS));
        LEAFCUTTER_ANTHILL = registerBlockAndItem("leafcutter_anthill", new BlockLeafcutterAnthill());
        LEAFCUTTER_ANT_CHAMBER = registerBlockAndItem("leafcutter_ant_chamber", new BlockLeafcutterAntChamber());
        CAPSID = registerBlockAndItem("capsid", new BlockCapsid());
        VOID_WORM_BEAK = registerBlockAndItem("void_worm_beak", new BlockVoidWormBeak());
        VOID_WORM_EFFIGY = registerBlockAndItem("void_worm_effigy", new BlockVoidWormEffigy());
        TERRAPIN_EGG = registerBlockAndItem("terrapin_egg", new BlockTerrapinEgg());
        RAINBOW_GLASS = registerBlockAndItem("rainbow_glass", new BlockRainbowGlass());
        BISON_FUR_BLOCK = registerBlockAndItem("bison_fur_block", new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.6F, 1.0F).sound(SoundType.WOOL)));
        BISON_CARPET = registerBlockAndItem("bison_carpet", new BlockBisonCarpet());
        SAND_CIRCLE = registerBlockAndItem("sand_circle", new SandBlock(14406560, BlockBehaviour.Properties.copy(Blocks.SAND)), new Item.Properties(), false);
        RED_SAND_CIRCLE = registerBlockAndItem("red_sand_circle", new SandBlock(11098145, BlockBehaviour.Properties.copy(Blocks.RED_SAND)), new Item.Properties(), false);
        ENDER_RESIDUE = registerBlockAndItem("ender_residue", new BlockEnderResidue());
        TRANSMUTATION_TABLE = registerBlockAndItem("transmutation_table", new BlockTransmutationTable(), new Item.Properties().rarity(Rarity.EPIC).fireResistant(), true);
        SCULK_BOOMER = registerBlockAndItem("sculk_boomer", new BlockSculkBoomer());
        SKUNK_SPRAY = Registry.register(BuiltInRegistries.BLOCK, id("skunk_spray"), new BlockSkunkSpray());
        BANANA_SLUG_SLIME_BLOCK = registerBlockAndItem("banana_slug_slime_block", new BlockBananaSlugSlime());
        CRYSTALIZED_BANANA_SLUG_MUCUS = registerBlockAndItem("crystalized_banana_slug_mucus", new BlockCrystalizedMucus());
        CAIMAN_EGG = registerBlockAndItem("caiman_egg", new BlockReptileEgg(AMEntityRegistry.CAIMAN));
        TRIOPS_EGGS = registerBlockAndItem("triops_eggs", new BlockTriopsEggs());
        //TODO reimplement end pirate blocks
    }

    private static Block registerBlockAndItem(String name, Block block) {
        return registerBlockAndItem(name, block, new Item.Properties(), false);
    }

    private static Block registerBlockAndItem(String name, Block block, Item.Properties blockItemProps, boolean specialRender) {
        Block registered = Registry.register(BuiltInRegistries.BLOCK, id(name), block);
        Item item = specialRender ? new BlockItemAMRender(registered, blockItemProps) : new AMBlockItem(registered, blockItemProps);
        Registry.register(BuiltInRegistries.ITEM, id(name), item);
        return registered;
    }
}
