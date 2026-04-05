package com.github.alexthe666.alexsmobs.client;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerRainbow;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;

/**
 * Fabric 1.20.1: AddLayers equivalent; called from LivingEntityRendererMixin to add LayerRainbow to every LivingEntityRenderer (1:1 with Forge EntityRenderersEvent.AddLayers).
 */
public class ClientLayerRegistry {

    /** Called from mixin when a LivingEntityRenderer is constructed; adds rainbow layer. Fabric: addLayer is protected, use reflection (1:1). */
    public static void addLayerToRenderer(LivingEntityRenderer<?, ?> renderer) {
        try {
            java.lang.reflect.Method m = LivingEntityRenderer.class.getDeclaredMethod("addLayer", net.minecraft.client.renderer.entity.layers.RenderLayer.class);
            m.setAccessible(true);
            m.invoke(renderer, new LayerRainbow(renderer));
        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("Could not add LayerRainbow via reflection", e);
        }
    }
}
