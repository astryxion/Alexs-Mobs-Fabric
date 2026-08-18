package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelKomodoDragon;
import com.github.alexthe666.alexsmobs.entity.EntityKomodoDragon;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class RenderKomodoDragon extends MobRenderer<EntityKomodoDragon, LivingEntityRenderState, CitadelEntityModelBridge<EntityKomodoDragon>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/komodo_dragon.png");
    private static final Identifier TEXTURE_SADDLE = Identifier.parse("alexsmobs:textures/entity/komodo_dragon_saddle.png");
    private static final Identifier TEXTURE_MAID = Identifier.parse("alexsmobs:textures/entity/komodo_dragon_maid.png");

    public RenderKomodoDragon(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelKomodoDragon(0.0F)), 0.6F);
        this.addLayer(new LayerSaddle(this));
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

    static class LayerSaddle extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityKomodoDragon>> {

        private static final ModelKomodoDragon MAID_MODEL = new ModelKomodoDragon(0.3F);
        private static final ModelKomodoDragon SADDLE_MODEL = new ModelKomodoDragon(0.5F);

        public LayerSaddle(RenderKomodoDragon render) {
            super(render);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityKomodoDragon entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityKomodoDragon k ? k : null;
            if (entitylivingbaseIn == null) {
                return;
            }
            float limbSwing = state.walkAnimationPos;
            float limbSwingAmount = Math.min(1.0F, state.walkAnimationSpeed);
            float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
            float ageInTicks = state.ageInTicks;
            float wrappedHead = state.yRot;
            ModelKomodoDragon parentModel = (ModelKomodoDragon) this.getParentModel().citadel();
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            if (entitylivingbaseIn.isMaid()) {
                collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(TEXTURE_MAID), (pose, maid) -> {
                    parentModel.copyPropertiesTo(MAID_MODEL);
                    MAID_MODEL.prepareMobModel(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
                    MAID_MODEL.setupAnim(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, wrappedHead, headPitch);
                    MAID_MODEL.renderToBuffer(DeferredPoseStacks.fromCaptured(pose), maid, packedLightIn, overlay, -1);
                });
            }
            if (entitylivingbaseIn.isSaddled()) {
                collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(TEXTURE_SADDLE), (pose, saddle) -> {
                    parentModel.copyPropertiesTo(SADDLE_MODEL);
                    SADDLE_MODEL.prepareMobModel(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
                    SADDLE_MODEL.setupAnim(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, wrappedHead, headPitch);
                    SADDLE_MODEL.renderToBuffer(DeferredPoseStacks.fromCaptured(pose), saddle, packedLightIn, overlay, -1);
                });
            }
        }
    }
}
