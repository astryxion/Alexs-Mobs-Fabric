package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.CustomTabBehavior;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AMCreativeTabRegistry {

    public static CreativeModeTab TAB;

    public static void init() {
        TAB = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, new ResourceLocation(AlexsMobs.MODID, AlexsMobs.MODID),
                FabricItemGroup.builder()
                        .title(Component.translatable("itemGroup." + AlexsMobs.MODID))
                        .icon(() -> new ItemStack(AMItemRegistry.ANIMAL_DICTIONARY))
                        .displayItems((enabledFeatures, output) -> {
                            List<ItemStack> spawnEggs = new ArrayList<>();
                            List<ItemStack> rest = new ArrayList<>();
                            for (Item item : BuiltInRegistries.ITEM) {
                                ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
                                if (!key.getNamespace().equals(AlexsMobs.MODID)) continue;
                                String path = key.getPath();
                                if ("shield_of_the_deep".equals(path) || "end_pirate_anchor".equals(path) || "end_pirate_anchor_winch".equals(path)) continue;
                                CreativeModeTab.Output collector = new CreativeModeTab.Output() {
                                    @Override
                                    public void accept(ItemStack stack, TabVisibility visibility) {
                                        (BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().startsWith("spawn_egg_") ? spawnEggs : rest).add(stack);
                                    }
                                };
                                if (item instanceof CustomTabBehavior customTabBehavior) {
                                    customTabBehavior.fillItemCategory(collector);
                                } else {
                                    collector.accept(new ItemStack(item));
                                }
                            }
                            spawnEggs.sort(Comparator.comparing(s -> BuiltInRegistries.ITEM.getKey(s.getItem()).toString()));
                            rest.sort(Comparator.comparing(s -> BuiltInRegistries.ITEM.getKey(s.getItem()).toString()));
                            for (ItemStack s : spawnEggs) output.accept(s);
                            for (ItemStack s : rest) output.accept(s);
                        })
                        .build());
    }
}
