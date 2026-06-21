package com.github.alexthe666.alexsmobs.inventory;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.Registry;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class AMMenuRegistry {

    public static final MenuType<MenuTransmutationTable> TRANSMUTATION_TABLE =
            Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "transmutation_table"),
                    new MenuType<>(MenuTransmutationTable::new, FeatureFlags.DEFAULT_FLAGS));

    public static void init() {
    }

}
