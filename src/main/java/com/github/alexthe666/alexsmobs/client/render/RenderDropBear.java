package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelDropBear;
import com.github.alexthe666.alexsmobs.entity.EntityDropBear;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class RenderDropBear extends MobRenderer<EntityDropBear, LivingEntityRenderState, CitadelEntityModelBridge<EntityDropBear>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/dropbear.png");
    private static final Identifier TEXTURE_EYES = Identifier.parse("alexsmobs:textures/entity/dropbear_eyes.png");

    public RenderDropBear(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelDropBear()), 0.7F);
        this.addLayer(new EyeLayer(this));
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
        return TEXTURE;
    }

    static class EyeLayer extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityDropBear>> {

        public EyeLayer(RenderDropBear render) {
            super(render);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityDropBear entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityDropBear d ? d : null;
            if (entitylivingbaseIn == null) {
                return;
            }
            this.getParentModel().setupAnim(state);
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            collector.submitCustomGeometry(matrixStackIn, RenderTypes.eyes(TEXTURE_EYES), (pose, ivertexbuilder) ->
                this.getParentModel().renderCitadelToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, overlay, -1)
            );
        }
    }
}
