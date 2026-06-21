package com.github.alexthe666.citadel.client;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.client.render.CitadelLecternRenderer;
import com.github.alexthe666.citadel.client.shader.CitadelShaderRenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.resources.Identifier;

public class ClientEvents {
    public static void registerRenderers() {
        BlockEntityRenderers.register(Citadel.LECTERN_BE, CitadelLecternRenderer::new);
    }

    public static void registerPipelines() {
        // Render pipeline registration is triggered by Citadel shader bootstrap.
        CitadelShaderRenderTypes.RAINBOW_AURA_PIPELINE.toString();
    }

    public static void registerItemModels() {
        Identifier.fromNamespaceAndPath("citadel", "custom_item_model");
    }
}
