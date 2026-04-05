package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class AMPaintingRegistry {

    public static PaintingVariant NFT;
    public static PaintingVariant DOG_POKER;

    private static boolean initialized;

    /** Call after game bootstrap. In 1.21.1 these registries may not be in root until world load; no-op if missing. */
    @SuppressWarnings("unchecked")
    public static void init() {
        if (initialized) return;
        try {
            Registry<PaintingVariant> registry = (Registry<PaintingVariant>) BuiltInRegistries.REGISTRY.getOrThrow((ResourceKey) Registries.PAINTING_VARIANT);
            NFT = Registry.register(registry, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "nft"), new PaintingVariant(32, 32, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "nft")));
            DOG_POKER = Registry.register(registry, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "dog_poker"), new PaintingVariant(32, 16, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "dog_poker")));
        } catch (IllegalStateException e) {
            AlexsMobs.LOGGER.warn("Painting variant registry not available yet (1.21.1): {}", e.getMessage());
        }
        initialized = true;
    }
}
