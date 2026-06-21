package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;

public class AMPaintingRegistry {
    public static final ResourceKey<PaintingVariant> NFT = ResourceKey.create(Registries.PAINTING_VARIANT, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "nft"));
    public static final ResourceKey<PaintingVariant> DOG_POKER = ResourceKey.create(Registries.PAINTING_VARIANT, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "dog_poker"));

    public static void init() {
    }
}
