package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AMTileEntityRegistry {

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, path);
    }

    public static BlockEntityType<TileEntityLeafcutterAnthill> LEAFCUTTER_ANTHILL;
    public static BlockEntityType<TileEntityCapsid> CAPSID;
    public static BlockEntityType<TileEntityVoidWormBeak> VOID_WORM_BEAK;
    public static BlockEntityType<TileEntityTerrapinEgg> TERRAPIN_EGG;
    public static BlockEntityType<TileEntityTransmutationTable> TRANSMUTATION_TABLE;
    public static BlockEntityType<TileEntitySculkBoomer> SCULK_BOOMER;
    public static BlockEntityType<TileEntityEndPirateDoor> END_PIRATE_DOOR;
    public static BlockEntityType<TileEntityEndPirateAnchor> END_PIRATE_ANCHOR;
    public static BlockEntityType<TileEntityEndPirateAnchorWinch> END_PIRATE_ANCHOR_WINCH;
    public static BlockEntityType<TileEntityEndPirateShipWheel> END_PIRATE_SHIP_WHEEL;
    public static BlockEntityType<TileEntityEndPirateFlag> END_PIRATE_FLAG;

    public static void init() {
        LEAFCUTTER_ANTHILL = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("leafcutter_anthill_te"),
                BlockEntityType.Builder.of(TileEntityLeafcutterAnthill::new, AMBlockRegistry.LEAFCUTTER_ANTHILL).build(null));
        CAPSID = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("capsid_te"),
                BlockEntityType.Builder.of(TileEntityCapsid::new, AMBlockRegistry.CAPSID).build(null));
        VOID_WORM_BEAK = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("void_worm_beak_te"),
                BlockEntityType.Builder.of(TileEntityVoidWormBeak::new, AMBlockRegistry.VOID_WORM_BEAK).build(null));
        TERRAPIN_EGG = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("terrapin_egg_te"),
                BlockEntityType.Builder.of(TileEntityTerrapinEgg::new, AMBlockRegistry.TERRAPIN_EGG).build(null));
        TRANSMUTATION_TABLE = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("transmutation_table"),
                BlockEntityType.Builder.of(TileEntityTransmutationTable::new, AMBlockRegistry.TRANSMUTATION_TABLE).build(null));
        SCULK_BOOMER = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("sculk_boomer"),
                BlockEntityType.Builder.of(TileEntitySculkBoomer::new, AMBlockRegistry.SCULK_BOOMER).build(null));
        END_PIRATE_DOOR = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("end_pirate_door_te"),
                BlockEntityType.Builder.of(TileEntityEndPirateDoor::new, AMBlockRegistry.END_PIRATE_DOOR).build(null));
        END_PIRATE_ANCHOR = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("end_pirate_anchor_te"),
                BlockEntityType.Builder.of(TileEntityEndPirateAnchor::new, AMBlockRegistry.END_PIRATE_ANCHOR).build(null));
        END_PIRATE_ANCHOR_WINCH = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("end_pirate_anchor_winch_te"),
                BlockEntityType.Builder.of(TileEntityEndPirateAnchorWinch::new, AMBlockRegistry.END_PIRATE_ANCHOR_WINCH).build(null));
        END_PIRATE_SHIP_WHEEL = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("end_pirate_ship_wheel_te"),
                BlockEntityType.Builder.of(TileEntityEndPirateShipWheel::new, AMBlockRegistry.END_PIRATE_SHIP_WHEEL).build(null));
        END_PIRATE_FLAG = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("end_pirate_flag_te"),
                BlockEntityType.Builder.of(TileEntityEndPirateFlag::new, AMBlockRegistry.END_PIRATE_FLAG).build(null));
    }
}
