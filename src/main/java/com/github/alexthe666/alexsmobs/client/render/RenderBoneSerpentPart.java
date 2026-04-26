package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelBoneSerpentBody;
import com.github.alexthe666.alexsmobs.client.model.ModelBoneSerpentTail;
import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpentPart;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

import java.util.Collections;
import java.util.Map;

public class RenderBoneSerpentPart extends LivingEntityRenderer<EntityBoneSerpentPart, LivingEntityRenderState, RenderBoneSerpentPart.BoneSerpentSwitchingModel> {

    private static final Identifier TEXTURE_BODY = Identifier.parse("alexsmobs:textures/entity/bone_serpent_mid.png");
    private static final Identifier TEXTURE_TAIL = Identifier.parse("alexsmobs:textures/entity/bone_serpent_tail.png");
    private static final ModelBoneSerpentBody BODY_MODEL = new ModelBoneSerpentBody();
    private static final ModelBoneSerpentTail TAIL_MODEL = new ModelBoneSerpentTail();

    public static final class BoneSerpentSwitchingModel extends EntityModel<LivingEntityRenderState> {
        private final ModelBoneSerpentBody bodyModel;
        private final ModelBoneSerpentTail tailModel;

        public BoneSerpentSwitchingModel(ModelBoneSerpentBody body, ModelBoneSerpentTail tail) {
            super(new ModelPart(Collections.emptyList(), Map.of()), RenderTypes::entityCutout);
            this.bodyModel = body;
            this.tailModel = tail;
        }

        public AlexAdvancedEntityModel<EntityBoneSerpentPart> modelFor(EntityBoneSerpentPart e) {
            return e.isTail() ? tailModel : bodyModel;
        }

        @Override
        public void setupAnim(LivingEntityRenderState state) {
            LivingEntity le = AlexsMobsClientKeys.getLiving(state);
            if (!(le instanceof EntityBoneSerpentPart e)) {
                return;
            }
            AlexAdvancedEntityModel<EntityBoneSerpentPart> m = modelFor(e);
            float limbSwing = state.walkAnimationPos;
            float limbSwingAmount = Math.min(1.0F, state.walkAnimationSpeed);
            float ageInTicks = state.ageInTicks;
            float netHeadYaw = state.yRot;
            float headPitch = state.xRot;
            m.prepareMobModel(e, limbSwing, limbSwingAmount, ageInTicks);
            m.setupAnim(e, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }

    public RenderBoneSerpentPart(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new BoneSerpentSwitchingModel(BODY_MODEL, TAIL_MODEL), 0.3F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    private AlexAdvancedEntityModel<EntityBoneSerpentPart> activeModel(LivingEntityRenderState state) {
        LivingEntity le = AlexsMobsClientKeys.getLiving(state);
        return le instanceof EntityBoneSerpentPart e ? this.getModel().modelFor(e) : BODY_MODEL;
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        LivingEntity le = AlexsMobsClientKeys.getLiving(state);
        if (!(le instanceof EntityBoneSerpentPart entity)) {
            return TEXTURE_BODY;
        }
        return entity.isTail() ? TEXTURE_TAIL : TEXTURE_BODY;
    }

    @Override
    protected boolean shouldShowName(EntityBoneSerpentPart entity, double distance) {
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
            AlexAdvancedEntityModel<EntityBoneSerpentPart> mesh = this.activeModel(state);
            PoseStack citadelPoseStack = new PoseStack();
            collector.submitCustomGeometry(poseStack, renderType, (pose, consumer) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> mesh.renderToBuffer(scratch, consumer, state.lightCoords, finalOverlay, finalTint))
            );
        }
        if (this.shouldRenderLayers(state) && !this.layers.isEmpty()) {
            this.getModel().setupAnim(state);
            for (RenderLayer<LivingEntityRenderState, BoneSerpentSwitchingModel> layer : this.layers) {
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
