package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelPlatypus;
import com.github.alexthe666.alexsmobs.entity.EntityPlatypus;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class RenderPlatypus extends MobRenderer<EntityPlatypus, LivingEntityRenderState, CitadelEntityModelBridge<EntityPlatypus>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/platypus.png");
    private static final Identifier TEXTURE_PERRY = Identifier.parse("alexsmobs:textures/entity/platypus_perry.png");

    public RenderPlatypus(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelPlatypus()), 0.45F);
        this.addLayer(new FedoraLayer(this));
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
        EntityPlatypus entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityPlatypus p ? p : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isPerry() ? TEXTURE_PERRY : TEXTURE;
    }

    static class FedoraLayer extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityPlatypus>> {
        private final Identifier FEDORA_TEXTURE = Identifier.parse("alexsmobs:textures/entity/platypus_fedora.png");

        public FedoraLayer(RenderPlatypus renderGrizzlyBear) {
            super(renderGrizzlyBear);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityPlatypus entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityPlatypus p ? p : null;
            if (entitylivingbaseIn == null || !entitylivingbaseIn.hasFedora()) {
                return;
            }
            this.getParentModel().setupAnim(state);
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            collector.submitCustomGeometry(matrixStackIn, RenderTypes.entityCutout(FEDORA_TEXTURE), (pose, ivertexbuilder) ->
                this.getParentModel().renderCitadelToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, overlay, -1)
            );
        }
    }
}
