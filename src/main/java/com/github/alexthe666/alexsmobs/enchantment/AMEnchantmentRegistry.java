package com.github.alexthe666.alexsmobs.enchantment;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class AMEnchantmentRegistry {

    public static final EnchantmentCategory STRADDLEBOARD = EnchantmentCategory.BREAKABLE;

    public static Enchantment STRADDLE_JUMP;
    public static Enchantment STRADDLE_LAVAWAX;
    public static Enchantment STRADDLE_SERPENTFRIEND;
    public static Enchantment STRADDLE_BOARDRETURN;

    /** Kept for call-site compatibility during backport; prefer the Enchantment fields. */
    public static final ResourceLocation STRADDLE_JUMP_KEY = new ResourceLocation(AlexsMobs.MODID, "straddle_jump");
    public static final ResourceLocation STRADDLE_LAVAWAX_KEY = new ResourceLocation(AlexsMobs.MODID, "lavawax");
    public static final ResourceLocation STRADDLE_SERPENTFRIEND_KEY = new ResourceLocation(AlexsMobs.MODID, "serpentfriend");
    public static final ResourceLocation STRADDLE_BOARDRETURN_KEY = new ResourceLocation(AlexsMobs.MODID, "board_return");

    public static void init() {
        STRADDLE_JUMP = Registry.register(BuiltInRegistries.ENCHANTMENT, STRADDLE_JUMP_KEY,
                new StraddleJumpEnchantment(Enchantment.Rarity.RARE, STRADDLEBOARD, EquipmentSlot.MAINHAND));
        STRADDLE_LAVAWAX = Registry.register(BuiltInRegistries.ENCHANTMENT, STRADDLE_LAVAWAX_KEY,
                new StraddleEnchantment(Enchantment.Rarity.COMMON, STRADDLEBOARD, EquipmentSlot.MAINHAND));
        STRADDLE_SERPENTFRIEND = Registry.register(BuiltInRegistries.ENCHANTMENT, STRADDLE_SERPENTFRIEND_KEY,
                new StraddleEnchantment(Enchantment.Rarity.UNCOMMON, STRADDLEBOARD, EquipmentSlot.MAINHAND));
        STRADDLE_BOARDRETURN = Registry.register(BuiltInRegistries.ENCHANTMENT, STRADDLE_BOARDRETURN_KEY,
                new StraddleEnchantment(Enchantment.Rarity.COMMON, STRADDLEBOARD, EquipmentSlot.MAINHAND));
    }
}
