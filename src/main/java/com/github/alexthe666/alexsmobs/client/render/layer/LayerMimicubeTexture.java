package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderMimicube;
import com.github.alexthe666.alexsmobs.entity.EntityMimicube;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class LayerMimicubeTexture extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityMimicube>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/mimicube_outer.png");

    public LayerMimicubeTexture(RenderMimicube render) {
        super(render);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        if (AlexsMobsClientKeys.getLiving(state) == null) {
            return;
        }
        int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
        collector.submitCustomGeometry(matrixStackIn, RenderTypes.entityTranslucent(TEXTURE), (pose, vc) -> {
            PoseStack stack = new PoseStack();
            stack.pushPose();
            stack.last().set(pose);
            getParentModel().renderCitadelToBuffer(stack, vc, packedLightIn, overlay, -1);
            stack.popPose();
        });
    }
}
