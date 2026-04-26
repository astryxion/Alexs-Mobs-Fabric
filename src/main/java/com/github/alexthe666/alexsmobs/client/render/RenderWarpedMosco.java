package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelWarpedMosco;
import com.github.alexthe666.alexsmobs.entity.EntityWarpedMosco;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class RenderWarpedMosco extends MobRenderer<EntityWarpedMosco, LivingEntityRenderState, CitadelEntityModelBridge<EntityWarpedMosco>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/warped_mosco.png");
    private static final Identifier TEXTURE_EYES = Identifier.parse("alexsmobs:textures/entity/warped_mosco_glow.png");

    public RenderWarpedMosco(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelWarpedMosco()), 1F);
        this.addLayer(new WarpedMoscoGlowLayer(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }

    static class WarpedMoscoGlowLayer extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityWarpedMosco>> {

        public WarpedMoscoGlowLayer(RenderWarpedMosco p_i50928_1_) {
            super(p_i50928_1_);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityWarpedMosco entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityWarpedMosco w ? w : null;
            if (entitylivingbaseIn == null) {
                return;
            }
            float ageInTicks = state.ageInTicks;
            float alpha = 0.5F + (Mth.cos(ageInTicks * 0.2F) + 1F) * 0.2F;
            this.getParentModel().setupAnim(state);
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            int packedColor = AMColorUtil.packColor(0.5F, 1.0F, 1.0F, alpha);
            collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.getEyesFlickering(TEXTURE_EYES, 0), (pose, ivertexbuilder) ->
                this.getParentModel().renderCitadelToBuffer(matrixStackIn, ivertexbuilder, 240, overlay, packedColor)
            );
        }
    }
}
