package com.github.alexthe666.alexsmobs.client;

import com.github.alexthe666.alexsmobs.client.render.layer.LayerRainbow;
import com.github.alexthe666.alexsmobs.mixin.LivingEntityRendererAccessor;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;

/**
 * Fabric 1.21.1: AddLayers equivalent; called from LivingEntityRendererMixin.
 */
public class ClientLayerRegistry {

    public static void addLayerToRenderer(LivingEntityRenderer<?, ?> renderer) {
        ((LivingEntityRendererAccessor) renderer).alexsmobs$addLayer(new LayerRainbow(renderer));
    }
}