package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelAnaconda;
import com.github.alexthe666.alexsmobs.entity.EntityAnacondaPart;
import com.github.alexthe666.alexsmobs.entity.util.AnacondaPartIndex;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

import java.util.Collections;
import java.util.Map;

public class RenderAnacondaPart extends LivingEntityRenderer<EntityAnacondaPart, LivingEntityRenderState, RenderAnacondaPart.AnacondaSwitchingModel> {

    private static final ModelAnaconda<EntityAnacondaPart> NECK_MODEL = new ModelAnaconda<>(AnacondaPartIndex.NECK);
    private static final ModelAnaconda<EntityAnacondaPart> BODY_MODEL = new ModelAnaconda<>(AnacondaPartIndex.BODY);
    private static final ModelAnaconda<EntityAnacondaPart> TAIL_MODEL = new ModelAnaconda<>(AnacondaPartIndex.TAIL);

    public static final class AnacondaSwitchingModel extends EntityModel<LivingEntityRenderState> {
        private final ModelAnaconda<EntityAnacondaPart> neckModel;
        private final ModelAnaconda<EntityAnacondaPart> bodyModel;
        private final ModelAnaconda<EntityAnacondaPart> tailModel;

        public AnacondaSwitchingModel(ModelAnaconda<EntityAnacondaPart> neck, ModelAnaconda<EntityAnacondaPart> body, ModelAnaconda<EntityAnacondaPart> tail) {
            super(new ModelPart(Collections.emptyList(), Map.of()), RenderTypes::entityCutout);
            this.neckModel = neck;
            this.bodyModel = body;
            this.tailModel = tail;
        }

        public ModelAnaconda<EntityAnacondaPart> modelFor(AnacondaPartIndex partType) {
            return switch (partType) {
                case BODY -> bodyModel;
                case NECK -> neckModel;
                case TAIL -> tailModel;
                default -> bodyModel;
            };
        }

        @Override
        public void setupAnim(LivingEntityRenderState state) {
            LivingEntity le = AlexsMobsClientKeys.getLiving(state);
            if (!(le instanceof EntityAnacondaPart e)) {
                return;
            }
            ModelAnaconda<EntityAnacondaPart> m = modelFor(e.getPartType());
            float limbSwing = state.walkAnimationPos;
            float limbSwingAmount = Math.min(1.0F, state.walkAnimationSpeed);
            float ageInTicks = state.ageInTicks;
            float netHeadYaw = state.yRot;
            float headPitch = state.xRot;
            m.prepareMobModel(e, limbSwing, limbSwingAmount, ageInTicks);
            m.setupAnim(e, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }

    public RenderAnacondaPart(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new AnacondaSwitchingModel(NECK_MODEL, BODY_MODEL, TAIL_MODEL), 0.3F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    private ModelAnaconda<EntityAnacondaPart> activeModel(LivingEntityRenderState state) {
        LivingEntity le = AlexsMobsClientKeys.getLiving(state);
        return le instanceof EntityAnacondaPart e ? this.getModel().modelFor(e.getPartType()) : BODY_MODEL;
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        LivingEntity le = AlexsMobsClientKeys.getLiving(state);
        if (!(le instanceof EntityAnacondaPart entity)) {
            return RenderAnaconda.getAnacondaTexture(false, false);
        }
        return RenderAnaconda.getAnacondaTexture(entity.isYellow(), entity.isShedding());
    }

    @Override
    protected void setupRotations(LivingEntityRenderState state, PoseStack matrixStackIn, float rotationYaw, float scaleFactor) {
        LivingEntity le = AlexsMobsClientKeys.getLiving(state);
        if (!(le instanceof EntityAnacondaPart entity)) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        float newYaw = entity.yHeadRot;
        if (this.isShaking(state)) {
            newYaw += (float) (Math.cos((double) entity.tickCount * 3.25D) * Math.PI * (double) 0.4F);
        }

        Pose pose = entity.getPose();
        if (pose != Pose.SLEEPING) {
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F - newYaw));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
        }

        if (state.deathTime > 0) {
            float f = ((float) state.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(f * 90.0F));
        } else if (entity.hasCustomName()) {
            String s = ChatFormatting.stripFormatting(entity.getName().getString());
            if (("Dinnerbone".equals(s) || "Grumm".equals(s))) {
                matrixStackIn.translate(0.0D, (double) (entity.getBbHeight() + 0.1F), 0.0D);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
            }
        }
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        LivingEntity le = AlexsMobsClientKeys.getLiving(state);
        if (le instanceof EntityAnacondaPart entitylivingbaseIn) {
            float sc = entitylivingbaseIn.getAnacondaMobScale();
            matrixStackIn.scale(sc, sc, sc);
        }
    }

    @Override
    protected boolean shouldShowName(EntityAnacondaPart entity, double distance) {
        return super.shouldShowName(entity, distance) && (entity.shouldShowName() || entity.hasCustomName() && entity == this.entityRenderDispatcher.crosshairPickEntity);
    }

    @Override
    public void submit(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
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
            tint = ARGB.multiply(tint, this.getModelTint(state));
            this.getModel().setupAnim(state);
            int finalTint = tint;
            int finalOverlay = overlay;
            ModelAnaconda<EntityAnacondaPart> mesh = this.activeModel(state);
            PoseStack citadelPoseStack = new PoseStack();
            collector.submitCustomGeometry(poseStack, renderType, (pose, consumer) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> mesh.renderToBuffer(scratch, consumer, state.lightCoords, finalOverlay, finalTint))
            );
        }
        if (this.shouldRenderLayers(state) && !this.layers.isEmpty()) {
            this.getModel().setupAnim(state);
            for (RenderLayer<LivingEntityRenderState, AnacondaSwitchingModel> layer : this.layers) {
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
}
