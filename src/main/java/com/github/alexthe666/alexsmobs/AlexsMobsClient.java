package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.client.network.AMClientNetworking;
import com.github.alexthe666.alexsmobs.client.render.RenderMultipartCollisionPart;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public class AlexsMobsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AlexsMobs.PROXY.clientInit();
        AMClientNetworking.registerClientReceivers();
        EntityRendererRegistry.register(AMEntityRegistry.CACHALOT_PART, RenderMultipartCollisionPart::new);
        EntityRendererRegistry.register(AMEntityRegistry.GIANT_SQUID_PART, RenderMultipartCollisionPart::new);
        EntityRendererRegistry.register(AMEntityRegistry.LAVIATHAN_PART, RenderMultipartCollisionPart::new);
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> ClientProxy.preloadRenderTextures());
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "preload_render_textures");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                ClientProxy.preloadRenderTextures();
            }
        });
    }
}
