package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelMimicOctopus;
import com.github.alexthe666.alexsmobs.entity.EntityMimicOctopus;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class RenderMimicOctopus extends MobRenderer<EntityMimicOctopus, LivingEntityRenderState, CitadelEntityModelBridge<EntityMimicOctopus>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/mimic_octopus.png");
    private static final Identifier TEXTURE_OVERLAY = Identifier.parse("alexsmobs:textures/entity/mimic_octopus_overlay.png");
    private static final Identifier TEXTURE_CREEPER = Identifier.parse("alexsmobs:textures/entity/mimic_octopus_creeper.png");
    private static final Identifier TEXTURE_GUARDIAN = Identifier.parse("alexsmobs:textures/entity/mimic_octopus_guardian.png");
    private static final Identifier TEXTURE_PUFFERFISH = Identifier.parse("alexsmobs:textures/entity/mimic_octopus_pufferfish.png");
    private static final Identifier TEXTURE_MIMICUBE = Identifier.parse("alexsmobs:textures/entity/mimic_octopus_mimicube.png");
    private static final Identifier TEXTURE_EYES = Identifier.parse("alexsmobs:textures/entity/mimic_octopus_eyes.png");
    private static final Identifier GUARDIAN_BEAM_TEXTURE = Identifier.parse("textures/entity/guardian_beam.png");
    private static final RenderType BEAM_RENDER_TYPE = AMRenderTypes.entityCutoutNoCull(GUARDIAN_BEAM_TEXTURE);

    private final ModelMimicOctopus octoModel;
    private final PoseStack citadelPoseScratch = new PoseStack();

    public RenderMimicOctopus(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelMimicOctopus()), 0.4F);
        this.octoModel = (ModelMimicOctopus) this.model.citadel();
        this.addLayer(new OverlayLayer(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    private static void beamVertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z, int r, int g, int b, float u, float v) {
        consumer.addVertex(matrix, x, y, z).setColor(r, g, b, 255).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT);
    }

    @Override
    public void submit(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        EntityMimicOctopus entityIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityMimicOctopus o ? o : null;
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);

        if (entityIn != null) {
            LivingEntity livingentity = entityIn.getGuardianLaser();
            if (livingentity != null) {
                float f = entityIn.getLaserAttackAnimationScale(partialTicks);
                float f1 = (float) entityIn.level().getGameTime() + partialTicks;
                float f2 = f1 * 0.5F % 1.0F;
                float f3 = entityIn.getEyeHeight();
                poseStack.pushPose();
                poseStack.translate(0.0D, f3, 0.0D);
                Vec3 vector3d = this.getPosition(livingentity, (double) livingentity.getBbHeight() * 0.5D, partialTicks);
                Vec3 vector3d1 = this.getPosition(entityIn, f3, partialTicks);
                Vec3 vector3d2 = vector3d.subtract(vector3d1);
                float f4 = (float) (vector3d2.length() + 1.0D);
                vector3d2 = vector3d2.normalize();
                float f5 = (float) Math.acos(vector3d2.y);
                float f6 = (float) Math.atan2(vector3d2.z, vector3d2.x);
                poseStack.mulPose(Axis.YP.rotationDegrees(((Mth.PI / 2F) - f6) * Mth.RAD_TO_DEG));
                poseStack.mulPose(Axis.XP.rotationDegrees(f5 * Mth.RAD_TO_DEG));
                float f7 = f1 * 0.05F * -1.5F;
                float f8 = f * f;
                int j = 64 + (int) (f8 * 191.0F);
                int k = 32 + (int) (f8 * 191.0F);
                int l = 128 - (int) (f8 * 64.0F);
                float f11 = Mth.cos(f7 + 2.3561945F) * 0.282F;
                float f12 = Mth.sin(f7 + 2.3561945F) * 0.282F;
                float f13 = Mth.cos(f7 + Maths.QUARTER_PI) * 0.282F;
                float f14 = Mth.sin(f7 + Maths.QUARTER_PI) * 0.282F;
                float f15 = Mth.cos(f7 + 3.926991F) * 0.282F;
                float f16 = Mth.sin(f7 + 3.926991F) * 0.282F;
                float f17 = Mth.cos(f7 + 5.4977875F) * 0.282F;
                float f18 = Mth.sin(f7 + 5.4977875F) * 0.282F;
                float f19 = Mth.cos(f7 + Mth.PI) * 0.2F;
                float f20 = Mth.sin(f7 + Mth.PI) * 0.2F;
                float f21 = Mth.cos(f7 + 0.0F) * 0.2F;
                float f22 = Mth.sin(f7 + 0.0F) * 0.2F;
                float f23 = Mth.cos(f7 + (Mth.PI / 2F)) * 0.2F;
                float f24 = Mth.sin(f7 + (Mth.PI / 2F)) * 0.2F;
                float f25 = Mth.cos(f7 + (Mth.PI * 1.5F)) * 0.2F;
                float f26 = Mth.sin(f7 + (Mth.PI * 1.5F)) * 0.2F;
                float f29 = -1.0F + f2;
                float f30 = f4 * 2.5F + f29;
                int fj = j;
                int fk = k;
                int fl = l;
                collector.submitCustomGeometry(poseStack, BEAM_RENDER_TYPE, (pose, ivertexbuilder) -> {
                    Matrix4f matrix4f = pose.pose();
                    beamVertex(ivertexbuilder, matrix4f, f19, f4, f20, fj, fk, fl, 0.4999F, f30);
                    beamVertex(ivertexbuilder, matrix4f, f19, 0.0F, f20, fj, fk, fl, 0.4999F, f29);
                    beamVertex(ivertexbuilder, matrix4f, f21, 0.0F, f22, fj, fk, fl, 0.0F, f29);
                    beamVertex(ivertexbuilder, matrix4f, f21, f4, f22, fj, fk, fl, 0.0F, f30);
                    beamVertex(ivertexbuilder, matrix4f, f23, f4, f24, fj, fk, fl, 0.4999F, f30);
                    beamVertex(ivertexbuilder, matrix4f, f23, 0.0F, f24, fj, fk, fl, 0.4999F, f29);
                    beamVertex(ivertexbuilder, matrix4f, f25, 0.0F, f26, fj, fk, fl, 0.0F, f29);
                    beamVertex(ivertexbuilder, matrix4f, f25, f4, f26, fj, fk, fl, 0.0F, f30);
                    float f31 = 0.0F;
                    if (entityIn.tickCount % 2 == 0) {
                        f31 = 0.5F;
                    }
                    beamVertex(ivertexbuilder, matrix4f, f11, f4, f12, fj, fk, fl, 0.5F, f31 + 0.5F);
                    beamVertex(ivertexbuilder, matrix4f, f13, f4, f14, fj, fk, fl, 1.0F, f31 + 0.5F);
                    beamVertex(ivertexbuilder, matrix4f, f17, f4, f18, fj, fk, fl, 1.0F, f31);
                    beamVertex(ivertexbuilder, matrix4f, f15, f4, f16, fj, fk, fl, 0.5F, f31);
                });
                poseStack.popPose();
            }
        }

        poseStack.pushPose();
        if (state.hasPose(Pose.SLEEPING) && state.bedOrientation != null) {
            float f = state.eyeHeight - 0.1F;
            net.minecraft.core.Direction direction = state.bedOrientation;
            poseStack.translate(-direction.getStepX() * f, 0.0F, -direction.getStepZ() * f);
        }
        float scaleFactor = state.scale;
        poseStack.scale(scaleFactor, scaleFactor, scaleFactor);
        this.setupRotations(state, poseStack, state.bodyRot, scaleFactor);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(state, poseStack);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        boolean bodyVisible = this.isBodyVisible(state);
        boolean translucentLayer = !bodyVisible && !state.isInvisibleToPlayer;
        RenderType renderType = this.getRenderType(state, bodyVisible, translucentLayer, state.appearsGlowing());
        if (renderType != null) {
            int overlay = LivingEntityRenderer.getOverlayCoords(state, this.getWhiteOverlayProgress(state));
            int tint = translucentLayer ? 654311423 : -1;
            tint = net.minecraft.util.ARGB.multiply(tint, this.getModelTint(state));
            this.model.setupAnim(state);
            int finalTint = tint;
            int finalOverlay = overlay;
            collector.submitCustomGeometry(poseStack, renderType, (pose, consumer) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, this.citadelPoseScratch, scratch ->
                    this.octoModel.renderToBuffer(scratch, consumer, state.lightCoords, finalOverlay, finalTint))
            );
        }
        if (this.shouldRenderLayers(state) && !this.layers.isEmpty()) {
            this.model.setupAnim(state);
            for (RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityMimicOctopus>> layer : this.layers) {
                layer.submit(poseStack, collector, state.lightCoords, state, state.yRot, state.xRot);
            }
        }
        poseStack.popPose();

        if (state.leashStates != null) {
            for (EntityRenderState.LeashState leashState : state.leashStates) {
                collector.submitLeash(poseStack, leashState);
            }
        }
        this.submitNameDisplay(state, poseStack, collector, cameraState);
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        EntityMimicOctopus octo = AlexsMobsClientKeys.getLiving(state) instanceof EntityMimicOctopus o ? o : null;
        if (octo == null) {
            return;
        }
        this.octoModel.young = octo.isBaby();
        matrixStackIn.translate(0, -0.02F, 0);
        matrixStackIn.scale(0.9F * octo.getScale(), 0.9F * octo.getScale(), 0.9F * octo.getScale());
    }

    public boolean shouldRender(EntityMimicOctopus livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else {
            if (livingEntityIn.hasGuardianLaser()) {
                LivingEntity livingentity = livingEntityIn.getGuardianLaser();
                if (livingentity != null) {
                    Vec3 vector3d = this.getPosition(livingentity, (double) livingentity.getBbHeight() * 0.5D, 1.0F);
                    Vec3 vector3d1 = this.getPosition(livingEntityIn, livingEntityIn.getEyeHeight(), 1.0F);
                    return camera.isVisible(new AABB(vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y, vector3d.z));
                }
            }

            return false;
        }
    }

    private Vec3 getPosition(LivingEntity entityLivingBaseIn, double p_177110_2_, float p_177110_4_) {
        double d0 = Mth.lerp(p_177110_4_, entityLivingBaseIn.xOld, entityLivingBaseIn.getX());
        double d1 = Mth.lerp(p_177110_4_, entityLivingBaseIn.yOld, entityLivingBaseIn.getY()) + p_177110_2_;
        double d2 = Mth.lerp(p_177110_4_, entityLivingBaseIn.zOld, entityLivingBaseIn.getZ());
        return new Vec3(d0, d1, d2);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }

    private final class OverlayLayer extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityMimicOctopus>> {

        private OverlayLayer(RenderMimicOctopus render) {
            super(render);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityMimicOctopus entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityMimicOctopus o ? o : null;
            if (entitylivingbaseIn == null) {
                return;
            }
            float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
            float transProgress = entitylivingbaseIn.prevTransProgress + (entitylivingbaseIn.transProgress - entitylivingbaseIn.prevTransProgress) * partialTicks;
            float colorProgress = (entitylivingbaseIn.prevColorShiftProgress + (entitylivingbaseIn.colorShiftProgress - entitylivingbaseIn.prevColorShiftProgress) * partialTicks) * 0.2F;
            float r = 1F;
            float g = 1F;
            float b = 1F;
            float a = 1F;
            float startR = 1.0F;
            float startG = 1.0F;
            float startB = 1.0F;
            float startA = 1.0F;
            float finR = 1.0F;
            float finG = 1.0F;
            float finB = 1.0F;
            float finA = 1.0F;
            if (entitylivingbaseIn.getPrevMimicState() == EntityMimicOctopus.MimicState.OVERLAY) {
                if (entitylivingbaseIn.getPrevMimickedBlock() != null) {
                    int j = OctopusColorRegistry.getBlockColor(entitylivingbaseIn.getPrevMimickedBlock());
                    startR = (float) (j >> 16 & 255) / 255.0F;
                    startG = (float) (j >> 8 & 255) / 255.0F;
                    startB = (float) (j & 255) / 255.0F;
                } else {
                    startA = 0.0F;
                }
            }
            if ((entitylivingbaseIn.getMimicState() == EntityMimicOctopus.MimicState.OVERLAY)) {
                if (entitylivingbaseIn.getMimickedBlock() != null) {
                    int i = OctopusColorRegistry.getBlockColor(entitylivingbaseIn.getMimickedBlock());
                    finR = (float) (i >> 16 & 255) / 255.0F;
                    finG = (float) (i >> 8 & 255) / 255.0F;
                    finB = (float) (i & 255) / 255.0F;
                } else {
                    finA = 0.0F;
                }
                r = startR + (finR - startR) * colorProgress;
                g = startG + (finG - startG) * colorProgress;
                b = startB + (finB - startB) * colorProgress;
                a = startA + (finA - startA) * colorProgress;
            }
            if (a == 1.0F) {
                a *= 0.9F + 0.1F * (float) Math.sin(entitylivingbaseIn.tickCount * 0.1F);
            }
            this.getParentModel().setupAnim(state);
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            if (entitylivingbaseIn.getPrevMimicState() != null) {
                float alphaPrev = 1 - transProgress * 0.2F;
                RenderType prevType = AMRenderTypes.entityTranslucent(getFor(entitylivingbaseIn.getPrevMimicState()));
                int tintPrev = net.minecraft.util.ARGB.colorFromFloat(alphaPrev * (entitylivingbaseIn.getPrevMimicState() == entitylivingbaseIn.getMimicState() ? a : 1.0F), 1.0F, 1.0F, 1.0F);
                collector.submitCustomGeometry(matrixStackIn, prevType, (pose, prev) ->
                    AlexAdvancedEntityModel.withCitadelSubmitPose(pose, RenderMimicOctopus.this.citadelPoseScratch, scratch ->
                        RenderMimicOctopus.this.octoModel.renderToBuffer(scratch, prev, packedLightIn, overlay, tintPrev))
                );
            }
            float alphaCurrent = transProgress * 0.2F;
            RenderType currentType = AMRenderTypes.entityTranslucent(getFor(entitylivingbaseIn.getMimicState()));
            int tintCurrent = net.minecraft.util.ARGB.colorFromFloat(alphaCurrent, r, g, b);
            collector.submitCustomGeometry(matrixStackIn, currentType, (pose, current) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, RenderMimicOctopus.this.citadelPoseScratch, scratch ->
                    RenderMimicOctopus.this.octoModel.renderToBuffer(scratch, current, packedLightIn, overlay, tintCurrent))
            );
            RenderType eyesType = AMRenderTypes.entityTranslucent(TEXTURE_EYES);
            int tintEyes = net.minecraft.util.ARGB.colorFromFloat(1.0F, 1.0F, 1.0F, 1.0F);
            collector.submitCustomGeometry(matrixStackIn, eyesType, (pose, eyes) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, RenderMimicOctopus.this.citadelPoseScratch, scratch ->
                    RenderMimicOctopus.this.octoModel.renderToBuffer(scratch, eyes, packedLightIn, overlay, tintEyes))
            );
        }

        public Identifier getFor(EntityMimicOctopus.MimicState st) {
            if (st == EntityMimicOctopus.MimicState.CREEPER) {
                return TEXTURE_CREEPER;
            }
            if (st == EntityMimicOctopus.MimicState.GUARDIAN) {
                return TEXTURE_GUARDIAN;
            }
            if (st == EntityMimicOctopus.MimicState.PUFFERFISH) {
                return TEXTURE_PUFFERFISH;
            }
            if (st == EntityMimicOctopus.MimicState.MIMICUBE) {
                return TEXTURE_MIMICUBE;
            }
            return TEXTURE_OVERLAY;
        }
    }
}
