package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelMurmurBody;
import com.github.alexthe666.alexsmobs.client.model.ModelMurmurHead;
import com.github.alexthe666.alexsmobs.client.model.ModelMurmurNeck;
import com.github.alexthe666.alexsmobs.entity.EntityMurmur;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Pose;

public class RenderMurmurBody extends MobRenderer<EntityMurmur, LivingEntityRenderState, CitadelEntityModelBridge<EntityMurmur>> {
    public static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/murmur.png");
    public static final Identifier TEXTURE_ANGRY = Identifier.parse("alexsmobs:textures/entity/murmur_angry.png");
    public static boolean renderWithHead = false;
    private static final ModelMurmurNeck NECK_MODEL = new ModelMurmurNeck();
    private static final ModelMurmurHead HEAD_MODEL = new ModelMurmurHead();

    /**
     * Maps from vanilla post-feet model space ({@code translate(0, -1.501F, 0)} after first child scale) to the
     * 1.21 fake-head chain ({@code translate(0, -2.9F, 0)} then second {@code 0.85F} scale), for uniform child scale.
     */
    private static final float FAKE_HEAD_CHAIN_Y = 1.501F - 2.9F / 0.85F;

    private final ModelMurmurBody bodyModel;

    public RenderMurmurBody(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelMurmurBody()), 0.5F);
        this.bodyModel = (ModelMurmurBody) this.model.citadel();
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.scale(0.85F, 0.85F, 0.85F);
    }

    @Override
    public void submit(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        poseStack.pushPose();
        if (state.hasPose(Pose.SLEEPING) && state.bedOrientation != null) {
            float headOffset = state.eyeHeight - 0.1F;
            net.minecraft.core.Direction direction = state.bedOrientation;
            poseStack.translate(-direction.getStepX() * headOffset, 0.0F, -direction.getStepZ() * headOffset);
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
            int finalOverlay = overlay;
            int finalTint = tint;
            PoseStack citadelPoseStack = new PoseStack();
            collector.submitCustomGeometry(poseStack, renderType, (pose, consumer) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> this.bodyModel.renderToBuffer(scratch, consumer, state.lightCoords, finalOverlay, finalTint))
            );
        }

        EntityMurmur body = AlexsMobsClientKeys.getLiving(state) instanceof EntityMurmur m ? m : null;
        if (body != null && (renderWithHead || body.shouldRenderFakeHead())) {
            float hairBob = state.ageInTicks;
            poseStack.pushPose();
            poseStack.translate(0.0F, FAKE_HEAD_CHAIN_Y, 0.0F);
            poseStack.scale(0.85F, 0.85F, 0.85F);
            Identifier loc = this.getTextureLocation(state);
            int overlayCoords = LivingEntityRenderer.getOverlayCoords(state, this.getWhiteOverlayProgress(state));
            RenderType cutout = AMRenderTypes.entityCutoutNoCull(loc);
            HEAD_MODEL.resetToDefaultPose();
            HEAD_MODEL.animateHair(hairBob);
            int headOverlay = overlayCoords;
            PoseStack citadelPoseStack = new PoseStack();
            collector.submitCustomGeometry(poseStack, cutout, (pose, headConsumer) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> HEAD_MODEL.renderToBuffer(scratch, headConsumer, state.lightCoords, headOverlay, -1))
            );
            collector.submitCustomGeometry(poseStack, cutout, (pose, neckConsumer) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> NECK_MODEL.renderToBuffer(scratch, neckConsumer, state.lightCoords, headOverlay, -1))
            );
            poseStack.popPose();
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
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityMurmur entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityMurmur m ? m : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isAngry() ? TEXTURE_ANGRY : TEXTURE;
    }
}
