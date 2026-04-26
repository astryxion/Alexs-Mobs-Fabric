package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderEndergrade;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class LayerEndergradeSaddle extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityEndergrade>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/endergrade_saddle.png");

    public LayerEndergradeSaddle(RenderEndergrade renderGrizzlyBear) {
        super(renderGrizzlyBear);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityEndergrade entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityEndergrade e ? e : null;
        if (entitylivingbaseIn == null || !entitylivingbaseIn.isSaddled()) {
            return;
        }
        int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
        this.getParentModel().setupAnim(state);
        collector.submitCustomGeometry(matrixStackIn, RenderTypes.entityCutout(TEXTURE), (pose, consumer) ->
                this.getParentModel().renderCitadelToBuffer(matrixStackIn, consumer, packedLightIn, overlay, -1));
    }
}
