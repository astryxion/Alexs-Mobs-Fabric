package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeHead;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class LayerCentipedeHeadEyes extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityCentipedeHead>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/cave_centipede_eyes.png");

    public LayerCentipedeHeadEyes(RenderLayerParent<LivingEntityRenderState, CitadelEntityModelBridge<EntityCentipedeHead>> render) {
        super(render);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int packedLightIn, LivingEntityRenderState renderState, float netHeadYaw, float headPitch) {
        EntityCentipedeHead entity = AlexsMobsClientKeys.getLiving(renderState) instanceof EntityCentipedeHead h ? h : null;
        if (entity == null) {
            return;
        }
        int overlay = LivingEntityRenderer.getOverlayCoords(renderState, 0.0F);
        this.getParentModel().setupAnim(renderState);
        bufferIn.submitCustomGeometry(matrixStackIn, RenderTypes.eyes(TEXTURE), (pose, consumer) -> {
            PoseStack stack = new PoseStack();
            stack.pushPose();
            stack.last().set(pose);
            this.getParentModel().renderCitadelToBuffer(stack, consumer, packedLightIn, overlay, -1);
            stack.popPose();
        });
    }
}
