package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderTusklin;
import com.github.alexthe666.alexsmobs.entity.EntityTusklin;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class LayerTusklinGear extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityTusklin>> {
    private static final Identifier TEXTURE_SADDLE = Identifier.parse("alexsmobs:textures/entity/tusklin_saddle.png");
    private static final Identifier TEXTURE_SHOES = Identifier.parse("alexsmobs:textures/entity/tusklin_hooves.png");

    public LayerTusklinGear(RenderTusklin render) {
        super(render);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityTusklin entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityTusklin t ? t : null;
        if (entity == null) {
            return;
        }
        int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
        this.getParentModel().setupAnim(state);
        if (entity.isSaddled()) {
            collector.submitCustomGeometry(matrixStackIn, RenderTypes.entityCutout(TEXTURE_SADDLE), (pose, vc) -> {
                PoseStack stack = new PoseStack();
                stack.pushPose();
                stack.last().set(pose);
                this.getParentModel().renderCitadelToBuffer(stack, vc, packedLightIn, overlay, -1);
                stack.popPose();
            });
        }
        if (!entity.getShoeStack().isEmpty()) {
            boolean foil = entity.getShoeStack().hasFoil();
            collector.submitCustomGeometry(matrixStackIn, RenderTypes.armorCutoutNoCull(TEXTURE_SHOES), (pose, vc) -> {
                PoseStack stack = new PoseStack();
                stack.pushPose();
                stack.last().set(pose);
                this.getParentModel().renderCitadelToBuffer(stack, vc, packedLightIn, overlay, -1);
                stack.popPose();
            });
            if (foil) {
                collector.submitCustomGeometry(matrixStackIn, RenderTypes.armorEntityGlint(), (pose, vc) -> {
                    PoseStack stack = new PoseStack();
                    stack.pushPose();
                    stack.last().set(pose);
                    this.getParentModel().renderCitadelToBuffer(stack, vc, packedLightIn, overlay, -1);
                    stack.popPose();
                });
            }
        }
    }
}
