package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Set;

public class AMPointOfInterestRegistry {

    public static final ResourceKey<PoiType> END_PORTAL_FRAME = key("end_portal_frame");
    public static final ResourceKey<PoiType> LEAFCUTTER_ANT_HILL = key("leafcutter_anthill");
    public static final ResourceKey<PoiType> BEACON = key("am_beacon");
    public static final ResourceKey<PoiType> HUMMINGBIRD_FEEDER = key("hummingbird_feeder");

    public static final PoiType END_PORTAL_FRAME_TYPE = new PoiType(getBlockStates(Blocks.END_PORTAL_FRAME), 32, 6);
    public static final PoiType LEAFCUTTER_ANT_HILL_TYPE = new PoiType(getBlockStates(AMBlockRegistry.LEAFCUTTER_ANTHILL), 32, 6);
    public static final PoiType BEACON_TYPE = new PoiType(getBlockStates(Blocks.BEACON), 32, 6);
    public static final PoiType HUMMINGBIRD_FEEDER_TYPE = new PoiType(getBlockStates(AMBlockRegistry.HUMMINGBIRD_FEEDER), 32, 6);

    private static boolean registered = false;

    private static Set<BlockState> getBlockStates(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }

    private static ResourceKey<PoiType> key(String id) {
        return ResourceKey.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE.key(), Identifier.fromNamespaceAndPath(AlexsMobs.MODID, id));
    }

    public static void init() {
        if (registered) {
            return;
        }
        Registry.register(BuiltInRegistries.POINT_OF_INTEREST_TYPE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "end_portal_frame"), END_PORTAL_FRAME_TYPE);
        Registry.register(BuiltInRegistries.POINT_OF_INTEREST_TYPE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "leafcutter_anthill"), LEAFCUTTER_ANT_HILL_TYPE);
        Registry.register(BuiltInRegistries.POINT_OF_INTEREST_TYPE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "am_beacon"), BEACON_TYPE);
        Registry.register(BuiltInRegistries.POINT_OF_INTEREST_TYPE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "hummingbird_feeder"), HUMMINGBIRD_FEEDER_TYPE);
        registered = true;
    }

}
