package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelMoose;
import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderMoose extends MobRenderer<EntityMoose, LivingEntityRenderState, CitadelEntityModelBridge<EntityMoose>> {
    private static final Identifier TEXTURE_ANTLERED = Identifier.parse("alexsmobs:textures/entity/moose_antlered.png");
    private static final Identifier TEXTURE_SNOWY_ANTLERED = Identifier.parse("alexsmobs:textures/entity/moose_snowy_antlered.png");
    private static final Identifier TEXTURE_SNOWY = Identifier.parse("alexsmobs:textures/entity/moose_snowy.png");
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/moose.png");

    public RenderMoose(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelMoose()), 0.8F);
        this.addLayer(new LayerSnow());
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityMoose entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityMoose m ? m : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isAntlered() && !entity.isBaby() ? TEXTURE_ANTLERED : TEXTURE;
    }

    class LayerSnow extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityMoose>> {

        public LayerSnow() {
            super(RenderMoose.this);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityMoose entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityMoose m ? m : null;
            if (entitylivingbaseIn == null || !entitylivingbaseIn.isSnowy()) {
                return;
            }
            Identifier tex = entitylivingbaseIn.isAntlered() && !entitylivingbaseIn.isBaby() ? TEXTURE_SNOWY_ANTLERED : TEXTURE_SNOWY;
            this.getParentModel().setupAnim(state);
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(tex), (pose, ivertexbuilder) ->
                this.getParentModel().renderCitadelToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, overlay, -1)
            );
        }
    }
}
