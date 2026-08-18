package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

// @Mod.EventBusSubscriber removed - use direct registration(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMTileEntityRegistry {

    public static final BlockEntityType<TileEntityLeafcutterAnthill> LEAFCUTTER_ANTHILL = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "leafcutter_anthill_te"), FabricBlockEntityTypeBuilder.create(TileEntityLeafcutterAnthill::new, AMBlockRegistry.LEAFCUTTER_ANTHILL).build());
    public static final BlockEntityType<TileEntityCapsid> CAPSID = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "capsid_te"), FabricBlockEntityTypeBuilder.create(TileEntityCapsid::new, AMBlockRegistry.CAPSID).build());
    public static final BlockEntityType<TileEntityVoidWormBeak> VOID_WORM_BEAK = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "void_worm_beak_te"), FabricBlockEntityTypeBuilder.create(TileEntityVoidWormBeak::new, AMBlockRegistry.VOID_WORM_BEAK).build());
    public static final BlockEntityType<TileEntityTerrapinEgg> TERRAPIN_EGG = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "terrapin_egg_te"), FabricBlockEntityTypeBuilder.create(TileEntityTerrapinEgg::new, AMBlockRegistry.TERRAPIN_EGG).build());
    public static final BlockEntityType<TileEntityTransmutationTable> TRANSMUTATION_TABLE = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "transmutation_table_te"), FabricBlockEntityTypeBuilder.create(TileEntityTransmutationTable::new, AMBlockRegistry.TRANSMUTATION_TABLE).build());
    public static final BlockEntityType<TileEntitySculkBoomer> SCULK_BOOMER = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "sculk_boomer_te"), FabricBlockEntityTypeBuilder.create(TileEntitySculkBoomer::new, AMBlockRegistry.SCULK_BOOMER).build());
    // Re-enable when purpur / end pirate blocks are registered again (parity with AlexsMobs-1.21.1-master AMBlockRegistry / AMTileEntityRegistry).
    public static final BlockEntityType<TileEntityEndPirateDoor> END_PIRATE_DOOR = null;//Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "end_pirate_door_te"), new BlockEntityType<>(TileEntityEndPirateDoor::new, AMBlockRegistry.END_PIRATE_DOOR));
    public static final BlockEntityType<TileEntityEndPirateAnchor> END_PIRATE_ANCHOR = null;//Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "end_pirate_anchor_te"), new BlockEntityType<>(TileEntityEndPirateAnchor::new, AMBlockRegistry.END_PIRATE_ANCHOR));
    public static final BlockEntityType<TileEntityEndPirateAnchorWinch> END_PIRATE_ANCHOR_WINCH = null;//Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "end_pirate_anchor_winch_te"), new BlockEntityType<>(TileEntityEndPirateAnchorWinch::new, AMBlockRegistry.END_PIRATE_ANCHOR_WINCH));
    public static final BlockEntityType<TileEntityEndPirateShipWheel> END_PIRATE_SHIP_WHEEL = null;//Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "end_pirate_ship_wheel_te"), new BlockEntityType<>(TileEntityEndPirateShipWheel::new, AMBlockRegistry.END_PIRATE_SHIP_WHEEL));
    public static final BlockEntityType<TileEntityEndPirateFlag> END_PIRATE_FLAG = null;//Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "end_pirate_flag_te"), new BlockEntityType<>(TileEntityEndPirateFlag::new, AMBlockRegistry.END_PIRATE_FLAG));

    public static void init() {
    }

}
