package com.github.alexthe666.alexsmobs.client.render.item;

import com.github.alexthe666.alexsmobs.client.render.AMItemstackRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;

/** Fabric 1.20.1: No IClientItemExtensions; custom item rendering is registered via BuiltinItemRendererRegistry in ClientProxy (1:1 behavior). */
public class AMItemRenderProperties {

    private static final AMItemstackRenderer RENDERER = new AMItemstackRenderer();

    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return RENDERER;
    }

    /** Shared BEWLR for Fabric BuiltinItemRendererRegistry (replaces Forge ISTER wiring). */
    public static AMItemstackRenderer getRenderer() {
        return RENDERER;
    }
}
