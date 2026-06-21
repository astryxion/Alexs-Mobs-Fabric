package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.data.CarverPortalPos;
import com.github.alexthe666.alexsmobs.item.data.TabIconDisplay;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class AMDataComponents {

    public static final DataComponentType<TabIconDisplay> TAB_ICON_DISPLAY = DataComponentType.<TabIconDisplay>builder()
            .persistent(TabIconDisplay.CODEC)
            .networkSynchronized(TabIconDisplay.STREAM_CODEC)
            .build();

    public static final DataComponentType<CarverPortalPos> CARVER_PORTAL_POS = DataComponentType.<CarverPortalPos>builder()
            .persistent(CarverPortalPos.CODEC)
            .networkSynchronized(CarverPortalPos.STREAM_CODEC)
            .build();

    private static boolean registered = false;

    public static void init() {
        if (registered) {
            return;
        }
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "tab_icon_display"), TAB_ICON_DISPLAY);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "carver_portal_pos"), CARVER_PORTAL_POS);
        registered = true;
    }
}
