package com.github.alexthe666.alexsmobs.mixin.client;

import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Model.class)
public abstract class ModelCitadelBridgeRenderMixin {

    @Inject(method = "renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V", at = @At("HEAD"), cancellable = true)
    private void alexsmobs$renderCitadelBridge(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color, CallbackInfo ci) {
        Object self = this;
        if (self instanceof CitadelEntityModelBridge<?> bridge) {
            bridge.renderCitadelToBuffer(poseStack, buffer, packedLight, packedOverlay, color);
            ci.cancel();
        } else if (self instanceof AlexAdvancedEntityModel.CitadelEntityModelBridge<?, ?> bridge) {
            bridge.delegate().renderToBuffer(poseStack, buffer, packedLight, packedOverlay, color);
            ci.cancel();
        }
    }
}
