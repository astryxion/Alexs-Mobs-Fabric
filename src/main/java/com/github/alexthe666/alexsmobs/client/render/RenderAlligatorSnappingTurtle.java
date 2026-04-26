package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelAlligatorSnappingTurtle;
import com.github.alexthe666.alexsmobs.entity.EntityAlligatorSnappingTurtle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class RenderAlligatorSnappingTurtle
        extends MobRenderer<EntityAlligatorSnappingTurtle, LivingEntityRenderState, CitadelEntityModelBridge<EntityAlligatorSnappingTurtle>> {
    private static final Identifier TEXTURE_MOSS = Identifier
            .parse("alexsmobs:textures/entity/alligator_snapping_turtle_moss.png");
    private static final Identifier TEXTURE = Identifier
            .parse("alexsmobs:textures/entity/alligator_snapping_turtle.png");

    public RenderAlligatorSnappingTurtle(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelAlligatorSnappingTurtle()), 0.75F);
        this.addLayer(new AlligatorSnappingTurtleMossLayer(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        EntityAlligatorSnappingTurtle entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityAlligatorSnappingTurtle t ? t : null;
        if (entity == null) {
            return;
        }
        float d = entity.getTurtleScale() < 0.01F ? 1F : entity.getTurtleScale();
        matrixStackIn.scale(d, d, d);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }

    static class AlligatorSnappingTurtleMossLayer
            extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityAlligatorSnappingTurtle>> {

        AlligatorSnappingTurtleMossLayer(RenderAlligatorSnappingTurtle renderer) {
            super(renderer);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int packedLightIn,
                LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityAlligatorSnappingTurtle entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityAlligatorSnappingTurtle t ? t : null;
            if (entity == null || entity.getMoss() <= 0) {
                return;
            }
            float mossAlpha = 0.15F * Mth.clamp(entity.getMoss(), 0, 10);
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            int color = ARGB.colorFromFloat(1.0F, 1.0F, 1.0F, Math.min(1.0F, mossAlpha));
            this.getParentModel().setupAnim(state);
            bufferIn.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityTranslucent(TEXTURE_MOSS), (pose, mossbuffer) ->
                    this.getParentModel().renderCitadelToBuffer(matrixStackIn, mossbuffer, packedLightIn, overlay, color));
        }
    }
}
