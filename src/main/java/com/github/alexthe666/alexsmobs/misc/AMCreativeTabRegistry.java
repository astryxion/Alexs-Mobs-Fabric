package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.CustomTabBehavior;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

public class AMCreativeTabRegistry {


    public static final CreativeModeTab TAB = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, AlexsMobs.MODID), CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup." + AlexsMobs.MODID))
            .icon(() -> new ItemStack(AMItemRegistry.TAB_ICON))
            .displayItems((enabledFeatures, output) -> {
                BuiltInRegistries.ITEM.stream()
                        .filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(AlexsMobs.MODID))
                        .filter(item -> item instanceof SpawnEggItem)
                        .forEach(item -> {
                            if (item instanceof CustomTabBehavior customTabBehavior) {
                                customTabBehavior.fillItemCategory(output);
                            } else {
                                output.accept(item);
                            }
                        });
                BuiltInRegistries.ITEM.stream()
                        .filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(AlexsMobs.MODID))
                        .filter(item -> !(item instanceof SpawnEggItem))
                        .forEach(item -> {
                            if (item instanceof CustomTabBehavior customTabBehavior) {
                                customTabBehavior.fillItemCategory(output);
                            } else {
                                output.accept(item);
                            }
                        });
            })
            .build());

    public static void init() {
    }
}
