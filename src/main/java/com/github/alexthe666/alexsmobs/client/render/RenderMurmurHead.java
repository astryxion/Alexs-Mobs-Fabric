package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelMurmurHead;
import com.github.alexthe666.alexsmobs.client.model.ModelMurmurNeck;
import com.github.alexthe666.alexsmobs.entity.EntityMurmurHead;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RenderMurmurHead extends MobRenderer<EntityMurmurHead, LivingEntityRenderState, CitadelEntityModelBridge<EntityMurmurHead>> {

    private static final ModelMurmurNeck NECK_MODEL = new ModelMurmurNeck();
    public static final int MAX_NECK_SEGMENTS = 128;

    private final ModelMurmurHead headModel;

    public RenderMurmurHead(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelMurmurHead()), 0.3F);
        this.headModel = (ModelMurmurHead) this.model.citadel();
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
    protected float getFlipDegrees() {
        return 0.0F;
    }

    @Override
    public boolean shouldRender(EntityMurmurHead livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else if (livingEntityIn.hasNeckBottom()) {
            Vec3 vector3d = livingEntityIn.getNeckBottom(1.0F);
            Vec3 vector3d1 = livingEntityIn.getNeckTop(1.0F);
            return camera.isVisible(new AABB(vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y, vector3d.z));
        } else {
            return false;
        }
    }

    @Override
    public void submit(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        EntityMurmurHead head = AlexsMobsClientKeys.getLiving(state) instanceof EntityMurmurHead h ? h : null;
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);

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
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> this.headModel.renderToBuffer(scratch, consumer, state.lightCoords, finalOverlay, finalTint))
            );
        }
        poseStack.popPose();

        if (head != null && head.hasNeckBottom()) {
            poseStack.pushPose();
            float headYaw = Mth.rotLerp(partialTicks, head.yBodyRotO, head.yBodyRot);
            Vec3 renderingAt = new Vec3(Mth.lerp(partialTicks, head.xo, head.getX()), Mth.lerp(partialTicks, head.yo, head.getY()), Mth.lerp(partialTicks, head.zo, head.getZ()));
            Vec3 bottom = head.getNeckBottom(partialTicks).subtract(renderingAt);
            Vec3 top = head.getNeckTop(partialTicks).subtract(renderingAt);
            Vec3 moveDownFrom = bottom.subtract(top);
            Vec3 moveUpTowards = top.subtract(bottom);
            Identifier textureId = this.getTextureLocation(state);
            RenderType neckRenderType = AMRenderTypes.entityCutoutNoCull(textureId);
            int overlayCoords = LivingEntityRenderer.getOverlayCoords(state, this.getWhiteOverlayProgress(state));
            poseStack.translate(moveDownFrom.x, moveDownFrom.y - 0.5F, moveDownFrom.z);
            Vec3 currentNeckButt = Vec3.ZERO;
            int segmentCount = 0;
            while (segmentCount < MAX_NECK_SEGMENTS && currentNeckButt.distanceTo(moveUpTowards) > 0.2) {
                double remainingDistance = Math.min(currentNeckButt.distanceTo(moveUpTowards), 1F);
                Vec3 linearVec = moveUpTowards.subtract(currentNeckButt);
                Vec3 powVec = new Vec3(modifyVecAngle(linearVec.x), modifyVecAngle(linearVec.y), modifyVecAngle(linearVec.z));
                Vec3 smoothedVec = remainingDistance < 1F ? linearVec : powVec;
                Vec3 next = smoothedVec.normalize().scale(remainingDistance).add(currentNeckButt);
                int neckLight = getLightColor(head, bottom.add(currentNeckButt).add(renderingAt));
                Vec3 fromSeg = currentNeckButt;
                Vec3 toSeg = next;
                int nl = neckLight;
                int ol = overlayCoords;
                float hy = headYaw;
                collector.submitCustomGeometry(poseStack, neckRenderType, (pose, neckConsumer) ->
                    RenderMurmurHead.renderNeckCube(fromSeg, toSeg, pose, neckConsumer, nl, ol, hy)
                );
                currentNeckButt = next;
                segmentCount++;
            }
            poseStack.popPose();
        }

        if (state.leashStates != null) {
            for (EntityRenderState.LeashState leashState : state.leashStates) {
                collector.submitLeash(poseStack, leashState);
            }
        }
        this.submitNameDisplay(state, poseStack, collector, cameraState);
    }

    private double modifyVecAngle(double dimension) {
        float abs = (float) Math.abs(dimension);
        return Math.signum(dimension) * Mth.clamp(Math.pow(abs, 0.1), 0.01 * abs, abs);
    }

    public static void renderNeckCube(Vec3 from, Vec3 to, PoseStack.Pose basePose, VertexConsumer buffer, int packedLightIn, int overlayCoords, float additionalYaw) {
        Vec3 sub = from.subtract(to);
        double d = sub.horizontalDistance();
        float rotY = (float) (Mth.atan2(sub.x, sub.z) * (double) Mth.RAD_TO_DEG);
        float rotX = (float) (-(Mth.atan2(sub.y, d) * (double) Mth.RAD_TO_DEG)) - 90.0F;
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.last().set(basePose);
        poseStack.translate(from.x, from.y, from.z);
        NECK_MODEL.setAttributes((float) sub.length(), rotX, rotY, additionalYaw);
        NECK_MODEL.renderToBuffer(poseStack, buffer, packedLightIn, overlayCoords, -1);
        poseStack.popPose();
    }

    private int getLightColor(EntityMurmurHead head, Vec3 vec3) {
        BlockPos blockpos = AMBlockPos.fromVec3(vec3);
        if (head.level().hasChunkAt(blockpos)) {
            int i = LevelRenderer.getLightCoords(head.level(), blockpos);
            int j = LevelRenderer.getLightCoords(head.level(), blockpos.above());
            int k = i & 255;
            int l = j & 255;
            int i1 = i >> 16 & 255;
            int j1 = j >> 16 & 255;
            return (Math.max(k, l)) | (Math.max(i1, j1)) << 16;
        } else {
            return 0;
        }
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityMurmurHead entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityMurmurHead h ? h : null;
        if (entity == null) {
            return RenderMurmurBody.TEXTURE;
        }
        return entity.isAngry() ? RenderMurmurBody.TEXTURE_ANGRY : RenderMurmurBody.TEXTURE;
    }
}
