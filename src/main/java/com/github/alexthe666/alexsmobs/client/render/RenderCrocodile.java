package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelCrocodile;
import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class RenderCrocodile extends MobRenderer<EntityCrocodile, LivingEntityRenderState, CitadelEntityModelBridge<EntityCrocodile>> {
    private static final Identifier TEXTURE_0 = Identifier.parse("alexsmobs:textures/entity/crocodile_0.png");
    private static final Identifier TEXTURE_1 = Identifier.parse("alexsmobs:textures/entity/crocodile_1.png");
    private static final Identifier TEXTURE_CROWN = Identifier.parse("alexsmobs:textures/entity/crocodile_crown.png");

    public RenderCrocodile(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelCrocodile()), 0.8F);
        this.addLayer(new CrownLayer(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.scale(0.9F, 0.9F, 0.9F);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityCrocodile entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityCrocodile c ? c : null;
        if (entity == null) {
            return TEXTURE_0;
        }
        return entity.isDesert() ? TEXTURE_1 : TEXTURE_0;
    }

    static class CrownLayer extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityCrocodile>> {

        public CrownLayer(RenderCrocodile p_i50928_1_) {
            super(p_i50928_1_);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityCrocodile entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityCrocodile c ? c : null;
            if (entitylivingbaseIn == null || !entitylivingbaseIn.isCrowned()) {
                return;
            }
            matrixStackIn.pushPose();
            this.getParentModel().setupAnim(state);
            collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(TEXTURE_CROWN), (pose, shoeBuffer) ->
                this.getParentModel().renderCitadelToBuffer(matrixStackIn, shoeBuffer, packedLightIn, OverlayTexture.NO_OVERLAY, -1)
            );
            matrixStackIn.popPose();
        }
    }
}
