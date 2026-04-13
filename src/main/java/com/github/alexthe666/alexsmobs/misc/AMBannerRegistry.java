package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BannerPattern;

public class AMBannerRegistry {

    public static BannerPattern BEAR;
    public static BannerPattern AUSTRALIA_0;
    public static BannerPattern AUSTRALIA_1;
    public static BannerPattern NEW_MEXICO;
    public static BannerPattern BRAZIL;

    public static void init() {
        BEAR = Registry.register(BuiltInRegistries.BANNER_PATTERN, new ResourceLocation(AlexsMobs.MODID, "bear"), new BannerPattern("bear"));
        AUSTRALIA_0 = Registry.register(BuiltInRegistries.BANNER_PATTERN, new ResourceLocation(AlexsMobs.MODID, "australia_0"), new BannerPattern("australia_0"));
        AUSTRALIA_1 = Registry.register(BuiltInRegistries.BANNER_PATTERN, new ResourceLocation(AlexsMobs.MODID, "australia_1"), new BannerPattern("australia_1"));
        NEW_MEXICO = Registry.register(BuiltInRegistries.BANNER_PATTERN, new ResourceLocation(AlexsMobs.MODID, "new_mexico"), new BannerPattern("new_mexico"));
        BRAZIL = Registry.register(BuiltInRegistries.BANNER_PATTERN, new ResourceLocation(AlexsMobs.MODID, "brazil"), new BannerPattern("brazil"));
    }
}
