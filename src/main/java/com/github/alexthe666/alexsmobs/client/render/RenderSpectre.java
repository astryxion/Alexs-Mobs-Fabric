package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelSpectre;
import com.github.alexthe666.alexsmobs.entity.EntitySpectre;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

public class RenderSpectre extends MobRenderer<EntitySpectre, LivingEntityRenderState, CitadelEntityModelBridge<EntitySpectre>> {
    private static final Identifier TEXTURE_BONE = Identifier.parse("alexsmobs:textures/entity/spectre_bone.png");
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/spectre.png");
    private static final Identifier TEXTURE_EYES = Identifier.parse("alexsmobs:textures/entity/spectre_glow.png");
    private static final Identifier TEXTURE_LEAD = Identifier.parse("alexsmobs:textures/entity/spectre_lead.png");

    public RenderSpectre(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelSpectre()), 0.5F);
        this.addLayer(new SpectreEyesLayer(this));
        this.addLayer(new SpectreMembraneLayer(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.scale(1.3F, 1.3F, 1.3F);
    }

    @Override
    protected int getBlockLightLevel(EntitySpectre entityIn, BlockPos partialTicks) {
        return 15;
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE_BONE;
    }

    public float getAlphaForRender(EntitySpectre entityIn, float partialTicks) {
        return ((float) Math.sin((entityIn.tickCount + partialTicks) * 0.1F) + 1.5F) * 0.1F + 0.5F;
    }

    static class SpectreEyesLayer extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntitySpectre>> {

        public SpectreEyesLayer(RenderSpectre p_i50928_1_) {
            super(p_i50928_1_);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntitySpectre entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntitySpectre s ? s : null;
            if (entitylivingbaseIn == null) {
                return;
            }
            this.getParentModel().setupAnim(state);
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            PoseStack citadelPoseStack = new PoseStack();
            collector.submitCustomGeometry(matrixStackIn, RenderTypes.eyes(TEXTURE_EYES), (pose, ivertexbuilder) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch ->
                    this.getParentModel().renderCitadelToBuffer(scratch, ivertexbuilder, 15728640, overlay, -1))
            );
        }
    }

    static class SpectreMembraneLayer extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntitySpectre>> {
        private final RenderSpectre parentRenderer;

        public SpectreMembraneLayer(RenderSpectre p_i50928_1_) {
            super(p_i50928_1_);
            this.parentRenderer = p_i50928_1_;
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntitySpectre entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntitySpectre s ? s : null;
            if (entitylivingbaseIn == null) {
                return;
            }
            float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
            this.getParentModel().setupAnim(state);
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0);
            int alphaColor = AMColorUtil.packColor(1.0F, 1.0F, 1.0F, parentRenderer.getAlphaForRender(entitylivingbaseIn, partialTicks));
            PoseStack citadelPoseStack = new PoseStack();
            collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.getSpectreBones(TEXTURE), (pose, lvt_11_1_) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch ->
                    this.getParentModel().renderCitadelToBuffer(scratch, lvt_11_1_, 15728640, overlay, alphaColor))
            );
            if (entitylivingbaseIn.isLeashed()) {
                collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(TEXTURE_LEAD), (pose, lead) ->
                    AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch ->
                        this.getParentModel().renderCitadelToBuffer(scratch, lead, 15728640, overlay, -1))
                );
            }
        }
    }
}
