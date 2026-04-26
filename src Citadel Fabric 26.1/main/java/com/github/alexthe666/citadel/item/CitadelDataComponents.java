package com.github.alexthe666.citadel.item;

import com.github.alexthe666.citadel.item.data.FancyItemDisplay;
import com.github.alexthe666.citadel.item.data.IconItemDisplay;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.function.UnaryOperator;

public class CitadelDataComponents {
    public static DataComponentType<FancyItemDisplay> FANCY_ITEM_DISPLAY;
    public static DataComponentType<IconItemDisplay> ICON_ITEM_DISPLAY;

    public static void register() {
        FANCY_ITEM_DISPLAY = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath("citadel", "fancy_item_display"), DataComponentType.<FancyItemDisplay>builder()
                .persistent(FancyItemDisplay.CODEC)
                .networkSynchronized(FancyItemDisplay.STREAM_CODEC)
                .build());
        ICON_ITEM_DISPLAY = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath("citadel", "icon_item_display"), DataComponentType.<IconItemDisplay>builder()
                .persistent(IconItemDisplay.CODEC)
                .networkSynchronized(IconItemDisplay.STREAM_CODEC)
                .build());
    }
}
