package com.github.alexthe666.alexsmobs.client;

import com.github.alexthe666.alexsmobs.client.render.layer.LayerRainbow;
import com.github.alexthe666.alexsmobs.mixin.LivingEntityRendererAccessor;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;

/**
 * Fabric 1.20.1: AddLayers equivalent; called from LivingEntityRendererMixin to add LayerRainbow to every LivingEntityRenderer (1:1 with Forge EntityRenderersEvent.AddLayers).
 */
public class ClientLayerRegistry {

    public static void addLayerToRenderer(LivingEntityRenderer<?, ?> renderer) {
        ((LivingEntityRendererAccessor) renderer).alexsmobs$addLayer(new LayerRainbow(renderer));
    }
}
