package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel.CitadelLivingRenderState;
import com.github.alexthe666.alexsmobs.client.model.ModelLaviathan;
import com.github.alexthe666.alexsmobs.entity.EntityLaviathan;
import com.github.alexthe666.alexsmobs.entity.EntityLaviathanPart;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class RenderLaviathan extends MobRenderer<EntityLaviathan, CitadelLivingRenderState, AlexAdvancedEntityModel.CitadelEntityModelBridge<EntityLaviathan, ModelLaviathan>> {

    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/laviathan.png");
    private static final Identifier TEXTURE_GLOW = Identifier.parse("alexsmobs:textures/entity/laviathan_glow.png");
    private static final Identifier TEXTURE_OBSIDIAN = Identifier.parse("alexsmobs:textures/entity/laviathan_obsidian.png");
    private static final Identifier TEXTURE_GEAR = Identifier.parse("alexsmobs:textures/entity/laviathan_gear.png");
    private static final Identifier TEXTURE_HELMET = Identifier.parse("alexsmobs:textures/entity/laviathan_helmet.png");
    private static final float REINS_COLOR_R = 98F / 255F;
    private static final float REINS_COLOR_G = 77F / 255F;
    private static final float REINS_COLOR_B = 52F / 255F;
    private static final float REINS_COLOR_R2 = 58F / 255F;
    private static final float REINS_COLOR_G2 = 40F / 255F;
    private static final float REINS_COLOR_B2 = 34F / 255F;
    public static boolean renderWithoutShaking = false;

    private final ModelLaviathan laviathanModel;

    public RenderLaviathan(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new AlexAdvancedEntityModel.CitadelEntityModelBridge<>(new ModelLaviathan()), 4.0F);
        this.laviathanModel = this.model.delegate();
        this.addLayer(new LayerOverlays(this));
    }

    @Override
    public CitadelLivingRenderState createRenderState() {
        return new CitadelLivingRenderState();
    }

    @Override
    public void extractRenderState(EntityLaviathan entity, CitadelLivingRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.citadelEntity = entity;
    }

    private static void addVertexPairAlex(VertexConsumer p_174308_, Matrix4f p_174309_, float p_174310_, float p_174311_, float p_174312_, int p_174313_, int p_174314_, int p_174315_, int p_174316_, float p_174317_, float p_174318_, float p_174319_, float p_174320_, int p_174321_, boolean p_174322_) {
        float f = (float) p_174321_ / 24.0F;
        int i = (int) Mth.lerp(f, (float) p_174313_, (float) p_174314_);
        int j = (int) Mth.lerp(f, (float) p_174315_, (float) p_174316_);
        int k = LightCoordsUtil.pack(i, j);
        float f2 = REINS_COLOR_R;
        float f3 = REINS_COLOR_G;
        float f4 = REINS_COLOR_B;
        if (p_174321_ % 2 == (p_174322_ ? 1 : 0)) {
            f2 = REINS_COLOR_R2;
            f3 = REINS_COLOR_G2;
            f4 = REINS_COLOR_B2;
        }
        float f5 = p_174310_ * f;
        float f6 = p_174311_ > 0.0F ? p_174311_ * f * f : p_174311_ - p_174311_ * (1.0F - f) * (1.0F - f);
        float f7 = p_174312_ * f;
        p_174308_.addVertex(p_174309_, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_).setColor((int) (f2 * 255), (int) (f3 * 255), (int) (f4 * 255), 255).setLight(k);
        p_174308_.addVertex(p_174309_, f5 + p_174319_, f6 + p_174317_ - p_174318_, f7 - p_174320_).setColor((int) (f2 * 255), (int) (f3 * 255), (int) (f4 * 255), 255).setLight(k);
    }

    @Override
    public boolean shouldRender(EntityLaviathan livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else {
            for (EntityLaviathanPart part : livingEntityIn.allParts) {
                if (camera.isVisible(part.getBoundingBox())) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    protected void scale(CitadelLivingRenderState state, PoseStack matrixStackIn) {
        EntityLaviathan entitylivingbaseIn = state.citadelEntity instanceof EntityLaviathan l ? l : null;
        if (entitylivingbaseIn == null) {
            return;
        }
        this.laviathanModel.young = entitylivingbaseIn.isBaby();
    }

    @Override
    protected boolean isShaking(CitadelLivingRenderState state) {
        EntityLaviathan entity = state.citadelEntity instanceof EntityLaviathan l ? l : null;
        return entity != null && entity.isInWaterOrRain() && !entity.isObsidian() && !renderWithoutShaking;
    }

    @Override
    public Identifier getTextureLocation(CitadelLivingRenderState state) {
        EntityLaviathan entity = state.citadelEntity instanceof EntityLaviathan l ? l : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isObsidian() ? TEXTURE_OBSIDIAN : TEXTURE;
    }

    private float getHeadShakeForReins(EntityLaviathan mob, float partialTick) {
        float hh1 = mob.prevHeadHeight;
        float hh2 = mob.getHeadHeight();
        float rawHeadHeight = (hh1 + (hh2 - hh1) * partialTick) / 3F;
        float clampedNeckRot = Mth.clamp(-rawHeadHeight, -1, 1);
        float headStillProgress = 1F - Math.abs(clampedNeckRot);
        float swim = Mth.lerp(partialTick, mob.prevSwimProgress, mob.swimProgress);
        float limbSwingAmount = mob.walkAnimation.speed(partialTick);
        float swing = mob.walkAnimation.position() + partialTick;
        float swingAmount = limbSwingAmount * swim * 0.2F * headStillProgress;
        float swimSpeed = mob.swimProgress >= 5F ? 0.3F : 0.9F;
        float swimDegree = 0.5F + swim * 0.05F;
        float boxOffset = (float) (-21 * 3.141592653589793D / (double) (2 * 3));
        float moveScale = 1;
        return 1.3F * Mth.cos(swing * swimSpeed * moveScale + boxOffset * (float) 2) * swingAmount * swimDegree * moveScale;
    }

    private float getHeadBobForReins(EntityLaviathan mob, float partialTick) {
        float swing = mob.tickCount + partialTick;
        float swingAmount = 1.0F;
        float idleSpeed = 0.04f;
        float idleDegree = 0.3f;
        float boxOffset = (float) (9 * 3.141592653589793D / (double) (2 * 3));
        float moveScale = 1;
        return 0.8F * Mth.cos(swing * idleSpeed * moveScale + boxOffset * (float) 2) * swingAmount * idleDegree * moveScale;
    }

    private <E extends Entity> void renderRein(EntityLaviathan mob, float partialTick, PoseStack p_115464_, SubmitNodeCollector collector, E rider, boolean left) {
        p_115464_.pushPose();
        Entity head = mob.headPart;
        if (head == null) {
            return;
        }
        float limbSwingAmount = mob.walkAnimation.speed(partialTick);
        float shake = getHeadShakeForReins(mob, partialTick);
        float headYaw = Math.abs(mob.getHeadYaw(partialTick)) / 50F;
        float headPitch = 1F - Math.abs((mob.prevHeadHeight + (mob.getHeadHeight() - mob.prevHeadHeight) * partialTick) / 3F);
        float yawAdd = (1F - headYaw) * 0.4F * (1F - limbSwingAmount * 0.7F) - headPitch * 0.2F;
        Vec3 vec3 = rider instanceof LivingEntity ? getReinPosition((LivingEntity) rider, partialTick, left, shake) : rider.getRopeHoldPosition(partialTick);
        double d0 = (double) (Mth.lerp(partialTick, mob.yBodyRot, mob.yBodyRotO) * Mth.DEG_TO_RAD) + (Math.PI / 2D);
        Vec3 vec31 = new Vec3((left ? -0.05F - yawAdd : 0.05F + yawAdd) + shake, 0.45F - headYaw * 0.2F + getHeadBobForReins(mob, partialTick), 0.1F);
        double d1 = Math.cos(d0) * vec31.z + Math.sin(d0) * vec31.x;
        double d2 = Math.sin(d0) * vec31.z - Math.cos(d0) * vec31.x;
        double d3 = Mth.lerp(partialTick, head.xOld, head.getX()) + d1;
        double d4 = Mth.lerp(partialTick, head.yOld, head.getY()) + vec31.y;
        double d5 = Mth.lerp(partialTick, head.zOld, head.getZ()) + d2;
        p_115464_.translate(d3, d4, d5);
        float f = (float) (vec3.x - d3);
        float f1 = (float) (vec3.y - d4);
        float f2 = (float) (vec3.z - d5);
        Matrix4f matrix4f = p_115464_.last().pose();
        float f4 = (float) (Mth.fastInvSqrt(f * f + f2 * f2) * 0.025F / 2.0F);
        float f5 = f2 * f4;
        float f6 = f * f4;
        BlockPos blockpos = AMBlockPos.fromVec3(mob.getEyePosition(partialTick));
        BlockPos blockpos1 = AMBlockPos.fromVec3(rider.getEyePosition(partialTick));
        int i = this.getBlockLightLevel(mob, blockpos);
        int j = mob.level().getBrightness(LightLayer.BLOCK, blockpos1);
        int k = mob.level().getBrightness(LightLayer.SKY, blockpos);
        int l = mob.level().getBrightness(LightLayer.SKY, blockpos1);
        float width = 0.05F;
        collector.submitCustomGeometry(p_115464_, RenderTypes.leash(), (pose, vertexconsumer) -> {
            Matrix4f mat = pose.pose();
            for (int i1 = 0; i1 <= 24; ++i1) {
                addVertexPairAlex(vertexconsumer, mat, f, f1, f2, i, j, k, l, width, width, f5, f6, i1, false);
            }
            for (int j1 = 24; j1 >= 0; --j1) {
                addVertexPairAlex(vertexconsumer, mat, f, f1, f2, i, j, k, l, width, width, f5, f6, j1, true);
            }
        });
        p_115464_.popPose();
    }

    private Vec3 getReinPosition(LivingEntity entity, float p_36374_, boolean left, float shake) {
        double d0 = 0.4D * (left ? -1.0D : 1.0D) - 0;
        float f = Mth.lerp(p_36374_ * 0.5F, entity.getXRot(), entity.xRotO) * Mth.DEG_TO_RAD;
        float f1 = Mth.lerp(p_36374_, entity.yBodyRotO, entity.yBodyRot) * Mth.DEG_TO_RAD;
        if (!entity.isFallFlying() && !entity.isAutoSpinAttack()) {
            if (entity.isVisuallySwimming()) {
                return entity.getPosition(p_36374_).add((new Vec3(d0, 0.3D, -0.34D)).xRot(-f).yRot(-f1));
            } else {
                double d5 = entity.getBoundingBox().getYsize() - 1.0D;
                double d6 = entity.isCrouching() ? -0.2D : 0.07D;
                return entity.getPosition(p_36374_).add((new Vec3(d0, d5, d6)).yRot(-f1));
            }
        } else {
            Vec3 vec3 = entity.getViewVector(p_36374_);
            Vec3 vec31 = entity.getDeltaMovement();
            double d1 = vec31.horizontalDistanceSqr();
            double d2 = vec3.horizontalDistanceSqr();
            float f2;
            if (d1 > 0.0D && d2 > 0.0D) {
                double d3 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d1 * d2);
                double d4 = vec31.x * vec3.z - vec31.z * vec3.x;
                f2 = (float) (Math.signum(d4) * Math.acos(d3));
            } else {
                f2 = 0.0F;
            }

            return entity.getPosition(p_36374_).add((new Vec3(d0, -0.11D, 0.85D)).zRot(-f2).xRot(-f).yRot(-f1));
        }
    }

    @Override
    public void submit(CitadelLivingRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        EntityLaviathan mob = state.citadelEntity instanceof EntityLaviathan l ? l : null;
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);

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
            PoseStack citadelPoseStack = new PoseStack();
            collector.submitCustomGeometry(poseStack, renderType, (pose, consumer) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> this.laviathanModel.renderToBuffer(scratch, consumer, state.lightCoords, finalOverlay, finalTint))
            );
        }
        if (this.shouldRenderLayers(state) && !this.layers.isEmpty()) {
            this.model.setupAnim(state);
            for (RenderLayer<CitadelLivingRenderState, AlexAdvancedEntityModel.CitadelEntityModelBridge<EntityLaviathan, ModelLaviathan>> layer : this.layers) {
                layer.submit(poseStack, collector, state.lightCoords, state, state.yRot, state.xRot);
            }
        }
        poseStack.popPose();

        if (mob != null) {
            Entity entity = mob.getControllingPassenger();
            if (entity != null) {
                double d0 = Mth.lerp(partialTicks, mob.xOld, mob.getX());
                double d1 = Mth.lerp(partialTicks, mob.yOld, mob.getY());
                double d2 = Mth.lerp(partialTicks, mob.zOld, mob.getZ());
                poseStack.pushPose();
                poseStack.translate(-d0, -d1, -d2);
                this.renderRein(mob, partialTicks, poseStack, collector, entity, true);
                this.renderRein(mob, partialTicks, poseStack, collector, entity, false);
                poseStack.popPose();
            }
        }

        if (state.leashStates != null) {
            for (EntityRenderState.LeashState leashState : state.leashStates) {
                collector.submitLeash(poseStack, leashState);
            }
        }
        this.submitNameDisplay(state, poseStack, collector, cameraState);
    }

    private final class LayerOverlays extends RenderLayer<CitadelLivingRenderState, AlexAdvancedEntityModel.CitadelEntityModelBridge<EntityLaviathan, ModelLaviathan>> {

        LayerOverlays(RenderLaviathan render) {
            super(render);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, CitadelLivingRenderState state, float netHeadYaw, float headPitch) {
            EntityLaviathan laviathan = state.citadelEntity instanceof EntityLaviathan l ? l : null;
            if (laviathan == null) {
                return;
            }
            this.getParentModel().setupAnim(state);
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            PoseStack citadelPoseStack = new PoseStack();
            if (!laviathan.isObsidian()) {
                collector.submitCustomGeometry(matrixStackIn, RenderTypes.eyes(TEXTURE_GLOW), (pose, ivertexbuilder) ->
                    AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> RenderLaviathan.this.laviathanModel.renderToBuffer(scratch, ivertexbuilder, packedLightIn, overlay, -1))
                );
            }
            if (laviathan.hasHeadGear()) {
                collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(TEXTURE_HELMET), (pose, ivertexbuilder) ->
                    AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> RenderLaviathan.this.laviathanModel.renderToBuffer(scratch, ivertexbuilder, packedLightIn, overlay, -1))
                );
            }
        }
    }
}
