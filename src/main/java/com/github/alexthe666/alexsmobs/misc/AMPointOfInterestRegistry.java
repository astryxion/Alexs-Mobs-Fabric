package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class AMPointOfInterestRegistry {

    public static PoiType END_PORTAL_FRAME;
    public static PoiType LEAFCUTTER_ANT_HILL;
    public static PoiType BEACON;
    public static PoiType HUMMINGBIRD_FEEDER;

    public static void init() {
        END_PORTAL_FRAME = Registry.register(BuiltInRegistries.POINT_OF_INTEREST_TYPE, new ResourceLocation(AlexsMobs.MODID, "end_portal_frame"),
                new PoiType(getBlockStates(Blocks.END_PORTAL_FRAME), 32, 6));
        LEAFCUTTER_ANT_HILL = Registry.register(BuiltInRegistries.POINT_OF_INTEREST_TYPE, new ResourceLocation(AlexsMobs.MODID, "leafcutter_anthill"),
                new PoiType(getBlockStates(AMBlockRegistry.LEAFCUTTER_ANTHILL), 32, 6));
        BEACON = Registry.register(BuiltInRegistries.POINT_OF_INTEREST_TYPE, new ResourceLocation(AlexsMobs.MODID, "am_beacon"),
                new PoiType(getBlockStates(Blocks.BEACON), 32, 6));
        HUMMINGBIRD_FEEDER = Registry.register(BuiltInRegistries.POINT_OF_INTEREST_TYPE, new ResourceLocation(AlexsMobs.MODID, "hummingbird_feeder"),
                new PoiType(getBlockStates(AMBlockRegistry.HUMMINGBIRD_FEEDER), 32, 6));
    }

    private static Set<BlockState> getBlockStates(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }

    public static ResourceKey<PoiType> getKey(PoiType type) {
        return BuiltInRegistries.POINT_OF_INTEREST_TYPE.getResourceKey(type).orElseThrow();
    }
}
