package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class LayerCrimsonMosquitoBlood extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityCrimsonMosquito>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/crimson_mosquito_blood.png");
    private static final Identifier TEXTURE_SICK = Identifier.parse("alexsmobs:textures/entity/crimson_mosquito_blood_blue.png");

    public LayerCrimsonMosquitoBlood(RenderCrimsonMosquito renderCrimsonMosquito) {
        super(renderCrimsonMosquito);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int packedLightIn, LivingEntityRenderState renderState, float netHeadYaw, float headPitch) {
        EntityCrimsonMosquito entitylivingbaseIn = AlexsMobsClientKeys.getLiving(renderState) instanceof EntityCrimsonMosquito m ? m : null;
        if (entitylivingbaseIn == null || entitylivingbaseIn.getBloodLevel() <= 0) {
            return;
        }
        matrixStackIn.pushPose();
        bufferIn.submitModel(
                this.getParentModel(),
                renderState,
                matrixStackIn,
                RenderTypes.eyes(entitylivingbaseIn.isSick() ? TEXTURE_SICK : TEXTURE),
                packedLightIn,
                LivingEntityRenderer.getOverlayCoords(renderState, 0.0F),
                -1,
                null);
        matrixStackIn.popPose();
    }
}
