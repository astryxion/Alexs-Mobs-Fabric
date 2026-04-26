package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelWarpedToad;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderWarpedToad;
import com.github.alexthe666.alexsmobs.entity.EntityWarpedToad;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class LayerWarpedToadGlow extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityWarpedToad>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/warped_toad_glow.png");
    private static final Identifier TEXTURE_BLINKING = Identifier.parse("alexsmobs:textures/entity/warped_toad_glow_blink.png");

    public LayerWarpedToadGlow(RenderWarpedToad renderWarpedToad) {
        super(renderWarpedToad);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityWarpedToad entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityWarpedToad t ? t : null;
        if (entitylivingbaseIn == null || entitylivingbaseIn.isBased()) {
            return;
        }
        this.getParentModel().setupAnim(state);
        ModelWarpedToad model = (ModelWarpedToad) this.getParentModel().citadel();
        final float alpha = 0.75F + (Mth.cos(state.ageInTicks * 0.2F) + 1F) * 0.125F;
        int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
        collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.getEyesFlickering(entitylivingbaseIn.isBlinking() ? TEXTURE_BLINKING : TEXTURE, 0), (pose, ivertexbuilder) ->
            model.renderToBuffer(matrixStackIn, ivertexbuilder, 240, overlay, -1)
        );
    }
}
