package com.github.alexthe666.citadel.client;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;

public class CitadelItemRenderProperties {

    private static final BlockEntityWithoutLevelRenderer RENDERER = new CitadelItemstackRenderer();

    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return RENDERER;
    }
}
