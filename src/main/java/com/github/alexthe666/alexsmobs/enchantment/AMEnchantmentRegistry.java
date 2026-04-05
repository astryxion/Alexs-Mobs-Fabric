package com.github.alexthe666.alexsmobs.enchantment;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class AMEnchantmentRegistry {

    public static final ResourceKey<Enchantment> STRADDLE_JUMP_KEY = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "straddle_jump"));
    public static final ResourceKey<Enchantment> STRADDLE_LAVAWAX_KEY = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "lavawax"));
    public static final ResourceKey<Enchantment> STRADDLE_SERPENTFRIEND_KEY = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "serpentfriend"));
    public static final ResourceKey<Enchantment> STRADDLE_BOARDRETURN_KEY = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "board_return"));

    /** Enchantments are data-driven in 1.21.1 (data/alexsmobs/enchantment/*.json). */
    public static void init() {
    }
}
