package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.client.ClientLayerRegistry;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric 1.20.1: Add LayerRainbow to every LivingEntityRenderer (1:1 with Forge EntityRenderersEvent.AddLayers).
 */
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void alexsmobs$addRainbowLayer(CallbackInfo ci) {
        ClientLayerRegistry.addLayerToRenderer((LivingEntityRenderer<?, ?>) (Object) this);
    }
}
