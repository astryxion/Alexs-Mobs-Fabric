package com.github.alexthe666.alexsmobs.inventory;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class AMMenuRegistry {

    public static MenuType<MenuTransmutationTable> TRANSMUTATION_TABLE;

    public static void init() {
        TRANSMUTATION_TABLE = Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "transmutation_table"),
                new MenuType<>(MenuTransmutationTable::new, FeatureFlags.DEFAULT_FLAGS));
    }
}
