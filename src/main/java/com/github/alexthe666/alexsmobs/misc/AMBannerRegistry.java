package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BannerPattern;

public class AMBannerRegistry {

    public static BannerPattern BEAR;
    public static BannerPattern AUSTRALIA_0;
    public static BannerPattern AUSTRALIA_1;
    public static BannerPattern NEW_MEXICO;
    public static BannerPattern BRAZIL;

    private static boolean initialized;

    /** Call after game bootstrap. In 1.21.1 these registries may not be in root until world load; no-op if missing. */
    @SuppressWarnings("unchecked")
    public static void init() {
        if (initialized) return;
        try {
            Registry<BannerPattern> registry = (Registry<BannerPattern>) BuiltInRegistries.REGISTRY.getOrThrow((ResourceKey) Registries.BANNER_PATTERN);
            ResourceLocation bearId = ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "bear");
            BEAR = Registry.register(registry, bearId, new BannerPattern(bearId, bearId.toString()));
            ResourceLocation australia0Id = ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "australia_0");
            AUSTRALIA_0 = Registry.register(registry, australia0Id, new BannerPattern(australia0Id, australia0Id.toString()));
            ResourceLocation australia1Id = ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "australia_1");
            AUSTRALIA_1 = Registry.register(registry, australia1Id, new BannerPattern(australia1Id, australia1Id.toString()));
            ResourceLocation newMexicoId = ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "new_mexico");
            NEW_MEXICO = Registry.register(registry, newMexicoId, new BannerPattern(newMexicoId, newMexicoId.toString()));
            ResourceLocation brazilId = ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "brazil");
            BRAZIL = Registry.register(registry, brazilId, new BannerPattern(brazilId, brazilId.toString()));
        } catch (IllegalStateException e) {
            AlexsMobs.LOGGER.warn("Banner pattern registry not available yet (1.21.1): {}", e.getMessage());
        }
        initialized = true;
    }
}
