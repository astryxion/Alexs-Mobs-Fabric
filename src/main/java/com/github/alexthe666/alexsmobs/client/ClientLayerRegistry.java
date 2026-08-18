package com.github.alexthe666.alexsmobs.client;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerRainbow;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerVineLasso;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;

public final class ClientLayerRegistry {

    private ClientLayerRegistry() {
    }

    public static void register() {
        LivingEntityRenderLayerRegistrationCallback.EVENT.register((entityType, livingRenderer, registrationHelper, context) -> {
            if (entityType == EntityType.ENDER_DRAGON) {
                return;
            }
            if (entityType == EntityType.PLAYER) {
                registrationHelper.register((RenderLayer) new LayerRainbow<>(livingRenderer));
                registrationHelper.register((RenderLayer) new LayerVineLasso<>(livingRenderer));
                return;
            }
            if (DefaultAttributes.hasSupplier(entityType)) {
                try {
                    registrationHelper.register((RenderLayer) new LayerRainbow<>(livingRenderer));
                    registrationHelper.register((RenderLayer) new LayerVineLasso<>(livingRenderer));
                } catch (Exception e) {
                    AlexsMobs.LOGGER.warn("Could not apply rainbow color layer to " + BuiltInRegistries.ENTITY_TYPE.getKey(entityType) + ", has custom renderer that is not LivingEntityRenderer.");
                }
            }
        });
    }
}
