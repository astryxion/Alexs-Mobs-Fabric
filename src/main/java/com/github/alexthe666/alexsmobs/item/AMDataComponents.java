package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.data.CarverPortalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class AMDataComponents {

    public static DataComponentType<CarverPortalPos> CARVER_PORTAL_POS;

    public static void register() {
        CARVER_PORTAL_POS = Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "carver_portal_pos"),
                DataComponentType.<CarverPortalPos>builder()
                        .persistent(CarverPortalPos.CODEC)
                        .networkSynchronized(CarverPortalPos.STREAM_CODEC)
                        .build()
        );
    }
}
