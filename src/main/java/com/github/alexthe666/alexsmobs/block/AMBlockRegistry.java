package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.item.AMBlockItem;
import com.github.alexthe666.alexsmobs.item.BlockItemAMRender;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.core.registries.Registries;

import java.util.function.Function;

public class AMBlockRegistry {

    private static BlockBehaviour.Properties withBlockId(Identifier id, BlockBehaviour.Properties props) {
        return props.setId(ResourceKey.create(Registries.BLOCK, id));
    }

    public static final Block BANANA_PEEL = registerBlockAndItem("banana_peel", key -> new BlockBananaPeel(withBlockId(key, BlockBananaPeel.defaultProperties())));
    public static final Block HUMMINGBIRD_FEEDER = registerBlockAndItem("hummingbird_feeder", key -> new BlockHummingbirdFeeder(withBlockId(key, BlockHummingbirdFeeder.defaultProperties())));
    public static final Block CROCODILE_EGG = registerBlockAndItem("crocodile_egg", key -> new BlockReptileEgg(AMEntityRegistry.CROCODILE, withBlockId(key, BlockReptileEgg.defaultProperties())));
    public static final Block GUSTMAKER = registerBlockAndItem("gustmaker", key -> new BlockGustmaker(withBlockId(key, BlockGustmaker.defaultProperties())));
    public static final Block STRADDLITE_BLOCK = registerBlockAndItem("straddlite_block", key -> new Block(withBlockId(key, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(1.0F, 1200.0F).sound(SoundType.ANCIENT_DEBRIS))), new Item.Properties().fireResistant(), false);
    public static final Block PLATYPUS_EGG = registerBlockAndItem("platypus_egg", key -> new BlockReptileEgg(AMEntityRegistry.PLATYPUS, withBlockId(key, BlockReptileEgg.defaultProperties())));
    public static final Block LEAFCUTTER_ANTHILL = registerBlockAndItem("leafcutter_anthill", key -> new BlockLeafcutterAnthill(withBlockId(key, BlockLeafcutterAnthill.defaultProperties())));
    public static final Block LEAFCUTTER_ANT_CHAMBER = registerBlockAndItem("leafcutter_ant_chamber", key -> new BlockLeafcutterAntChamber(withBlockId(key, BlockLeafcutterAntChamber.defaultProperties())));
    public static final Block CAPSID = registerBlockAndItem("capsid", key -> new BlockCapsid(withBlockId(key, BlockCapsid.defaultProperties())));
    public static final Block VOID_WORM_BEAK = registerBlockAndItem("void_worm_beak", key -> new BlockVoidWormBeak(withBlockId(key, BlockVoidWormBeak.defaultProperties())));
    public static final Block VOID_WORM_EFFIGY = registerBlockAndItem("void_worm_effigy", key -> new BlockVoidWormEffigy(withBlockId(key, BlockVoidWormEffigy.defaultProperties())));
    public static final Block TERRAPIN_EGG = registerBlockAndItem("terrapin_egg", key -> new BlockTerrapinEgg(withBlockId(key, BlockTerrapinEgg.defaultProperties())));
    public static final Block RAINBOW_GLASS = registerBlockAndItem("rainbow_glass", key -> new BlockRainbowGlass(withBlockId(key, BlockRainbowGlass.defaultProperties())));
    public static final Block BISON_FUR_BLOCK = registerBlockAndItem("bison_fur_block", key -> new Block(withBlockId(key, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.6F, 1.0F).sound(SoundType.WOOL))));
    public static final Block BISON_CARPET = registerBlockAndItem("bison_carpet", key -> new BlockBisonCarpet(withBlockId(key, BlockBisonCarpet.defaultProperties())));
    public static final Block SAND_CIRCLE = registerBlockAndItem("sand_circle", key -> new BlockSandCircle(withBlockId(key, BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.SAND))), new Item.Properties(), false);
    public static final Block RED_SAND_CIRCLE = registerBlockAndItem("red_sand_circle", key -> new BlockRedSandCircle(withBlockId(key, BlockBehaviour.Properties.ofFullCopy(Blocks.RED_SAND).mapColor(MapColor.COLOR_ORANGE))), new Item.Properties(), false);
    public static final Block ENDER_RESIDUE = registerBlockAndItem("ender_residue", key -> new BlockEnderResidue(withBlockId(key, BlockEnderResidue.defaultProperties())));
    public static final Block TRANSMUTATION_TABLE = registerBlockAndItem("transmutation_table", key -> new BlockTransmutationTable(withBlockId(key, BlockTransmutationTable.defaultProperties())), new Item.Properties().rarity(Rarity.EPIC).fireResistant(), true);
    public static final Block SCULK_BOOMER = registerBlockAndItem("sculk_boomer", key -> new BlockSculkBoomer(withBlockId(key, BlockSculkBoomer.defaultProperties())));
    public static final Block SKUNK_SPRAY = registerBlock("skunk_spray", key -> new BlockSkunkSpray(withBlockId(key, BlockSkunkSpray.defaultProperties())));
    public static final Block BANANA_SLUG_SLIME_BLOCK = registerBlockAndItem("banana_slug_slime_block", key -> new BlockBananaSlugSlime(withBlockId(key, BlockBananaSlugSlime.defaultProperties())));
    public static final Block CRYSTALIZED_BANANA_SLUG_MUCUS = registerBlockAndItem("crystalized_banana_slug_mucus", key -> new BlockCrystalizedMucus(withBlockId(key, BlockCrystalizedMucus.defaultProperties())));
    public static final Block CAIMAN_EGG = registerBlockAndItem("caiman_egg", key -> new BlockReptileEgg(AMEntityRegistry.CAIMAN, withBlockId(key, BlockReptileEgg.defaultProperties())));
    public static final Block TRIOPS_EGGS = registerBlockAndItem("triops_eggs", key -> new BlockTriopsEggs(withBlockId(key, BlockTriopsEggs.defaultProperties())));
    /*
    public static final DeferredHolder<Block, Block> PURPUR_PLANKS = registerBlockAndItem("purpur_planks", key -> new Block(withBlockId(key, purpurPlanksProperties())));
    public static final DeferredHolder<Block, Block> PURPUR_PLANKS_STAIRS = registerBlockAndItem("purpur_planks_stairs", key -> new StairBlock(PURPUR_PLANKS.get().defaultBlockState(), withBlockId(key, purpurPlanksProperties())));
    public static final DeferredHolder<Block, Block> PURPUR_PLANKS_SLAB = registerBlockAndItem("purpur_planks_slab", key -> new SlabBlock(withBlockId(key, purpurPlanksProperties())));
    public static final DeferredHolder<Block, Block> PURPUR_PLANKS_WALL = registerBlockAndItem("purpur_planks_wall", key -> new WallBlock(withBlockId(key, purpurPlanksProperties())));
    public static final DeferredHolder<Block, Block> END_PIRATE_DOOR = registerBlockAndItem("end_pirate_door", key -> new BlockEndPirateDoor(withBlockId(key, BlockEndPirateDoor.defaultProperties())));
    public static final DeferredHolder<Block, Block> END_PIRATE_TRAPDOOR = registerBlockAndItem("end_pirate_trapdoor", key -> new TrapDoorBlock(BlockSetType.OAK, withBlockId(key, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).lightLevel(state -> 3).strength(3.0F).sound(SoundType.GLASS).noOcclusion())));
    public static final DeferredHolder<Block, Block> END_PIRATE_ANCHOR = registerBlockAndItem("end_pirate_anchor", key -> new BlockEndPirateAnchor(withBlockId(key, BlockEndPirateAnchor.defaultProperties())));
    public static final DeferredHolder<Block, Block> END_PIRATE_ANCHOR_WINCH = registerBlockAndItem("end_pirate_anchor_winch", key -> new BlockEndPirateAnchorWinch(withBlockId(key, BlockEndPirateAnchorWinch.defaultProperties())));
    public static final DeferredHolder<Block, Block> END_PIRATE_SHIP_WHEEL = registerBlockAndItem("end_pirate_ship_wheel", key -> new BlockEndPirateShipWheel(withBlockId(key, BlockEndPirateShipWheel.defaultProperties())));
    public static final DeferredHolder<Block, Block> END_PIRATE_FLAG = registerBlockAndItem("end_pirate_flag", key -> new BlockEndPirateFlag(withBlockId(key, BlockEndPirateFlag.defaultProperties())));
    public static final DeferredHolder<Block, Block> PHANTOM_SAIL = registerBlockAndItem("phantom_sail", key -> new BlockEndPirateSail(false, withBlockId(key, BlockEndPirateSail.defaultProperties())));
    public static final DeferredHolder<Block, Block> SPECTRE_SAIL = registerBlockAndItem("spectre_sail", key -> new BlockEndPirateSail(true, withBlockId(key, BlockEndPirateSail.defaultProperties())));

     */

    public static Block registerBlockAndItem(String name, Function<Identifier, Block> block) {
        return registerBlockAndItem(name, block, new Item.Properties(), false);
    }

    public static Block registerBlockAndItem(String name, Function<Identifier, Block> blockFactory, Item.Properties blockItemProps, boolean specialRender) {
        Block block = registerBlock(name, blockFactory);
        Identifier itemId = id(name);
        Item.Properties props = blockItemProps.useBlockDescriptionPrefix().setId(ResourceKey.create(Registries.ITEM, itemId));
        Item item = specialRender ? new BlockItemAMRender(block, props) : new AMBlockItem(block, props);
        Registry.register(BuiltInRegistries.ITEM, itemId, item);
        return block;
    }

    public static Block registerBlock(String name, Function<Identifier, Block> blockFactory) {
        return Registry.register(BuiltInRegistries.BLOCK, id(name), blockFactory.apply(id(name)));
    }

    public static void bootstrap() {
    }

    private static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(AlexsMobs.MODID, name);
    }
}
