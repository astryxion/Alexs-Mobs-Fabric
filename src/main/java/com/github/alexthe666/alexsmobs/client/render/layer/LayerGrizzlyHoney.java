package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderGrizzlyBear;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class LayerGrizzlyHoney extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityGrizzlyBear>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/grizzly_bear_honey.png");

    public LayerGrizzlyHoney(RenderGrizzlyBear renderGrizzlyBear) {
        super(renderGrizzlyBear);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityGrizzlyBear entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityGrizzlyBear b ? b : null;
        if (entitylivingbaseIn == null || !entitylivingbaseIn.isHoneyed()) {
            return;
        }
        int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
        this.getParentModel().setupAnim(state);
        PoseStack scratch = new PoseStack();
        collector.submitCustomGeometry(matrixStackIn, RenderTypes.entityTranslucent(TEXTURE), (pose, consumer) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, scratch,
                        stack -> this.getParentModel().renderCitadelToBuffer(stack, consumer, packedLightIn, overlay, -1)));
    }
}
