package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class AMPaintingRegistry {

    public static PaintingVariant NFT;
    public static PaintingVariant DOG_POKER;

    public static void init() {
        NFT = Registry.register(BuiltInRegistries.PAINTING_VARIANT, new ResourceLocation(AlexsMobs.MODID, "nft"), new PaintingVariant(32, 32));
        DOG_POKER = Registry.register(BuiltInRegistries.PAINTING_VARIANT, new ResourceLocation(AlexsMobs.MODID, "dog_poker"), new PaintingVariant(32, 16));
    }
}
