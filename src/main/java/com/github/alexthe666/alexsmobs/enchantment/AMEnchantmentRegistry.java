package com.github.alexthe666.alexsmobs.enchantment;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class AMEnchantmentRegistry {

    /** Category is only used for anvil/book defaults; table rolls use {@link Enchantment#canEnchant}. */
    public static final EnchantmentCategory STRADDLEBOARD = EnchantmentCategory.BREAKABLE;

    public static Enchantment STRADDLE_JUMP;
    public static Enchantment STRADDLE_LAVAWAX;
    public static Enchantment STRADDLE_SERPENTFRIEND;
    public static Enchantment STRADDLE_BOARDRETURN;

    public static void init() {
        STRADDLE_JUMP = Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation(AlexsMobs.MODID, "straddle_jump"),
                new StraddleJumpEnchantment(Enchantment.Rarity.COMMON, STRADDLEBOARD, EquipmentSlot.MAINHAND));
        STRADDLE_LAVAWAX = Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation(AlexsMobs.MODID, "lavawax"),
                new StraddleEnchantment(Enchantment.Rarity.UNCOMMON, STRADDLEBOARD, EquipmentSlot.MAINHAND));
        STRADDLE_SERPENTFRIEND = Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation(AlexsMobs.MODID, "serpentfriend"),
                new StraddleEnchantment(Enchantment.Rarity.RARE, STRADDLEBOARD, EquipmentSlot.MAINHAND));
        STRADDLE_BOARDRETURN = Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation(AlexsMobs.MODID, "board_return"),
                new StraddleEnchantment(Enchantment.Rarity.UNCOMMON, STRADDLEBOARD, EquipmentSlot.MAINHAND));
    }
}
