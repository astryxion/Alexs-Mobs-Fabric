package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelMurmurNeck;
import com.github.alexthe666.alexsmobs.client.model.ModelTendonClaw;
import com.github.alexthe666.alexsmobs.entity.EntityTendonSegment;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class RenderTendonSegment extends EntityRenderer<EntityTendonSegment, RenderTendonSegment.TendonSegmentRenderState> {

    private static final Identifier CLAW_TEXTURE = Identifier.parse("alexsmobs:textures/entity/tendon_whip_claw.png");
    private static final ModelTendonClaw CLAW_MODEL = new ModelTendonClaw();

    public static final class TendonSegmentRenderState extends EntityRenderState {
        public EntityTendonSegment entity;
    }

    public RenderTendonSegment(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public TendonSegmentRenderState createRenderState() {
        return new TendonSegmentRenderState();
    }

    @Override
    public void extractRenderState(EntityTendonSegment entity, TendonSegmentRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.entity = entity;
    }

    @Override
    public boolean shouldRender(EntityTendonSegment entity, Frustum frustum, double x, double y, double z) {
        Entity next = entity.getFromEntity();
        return next != null && frustum.isVisible(entity.getBoundingBox().minmax(next.getBoundingBox())) || super.shouldRender(entity, frustum, x, y, z);
    }

    @Override
    public void submit(TendonSegmentRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        EntityTendonSegment entity = state.entity;
        if (entity == null) {
            super.submit(state, poseStack, collector, cameraState);
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        poseStack.pushPose();
        Entity fromEntity = entity.getFromEntity();
        float x = (float) Mth.lerp(partialTicks, entity.xOld, entity.getX());
        float y = (float) Mth.lerp(partialTicks, entity.yOld, entity.getY());
        float z = (float) Mth.lerp(partialTicks, entity.zOld, entity.getZ());

        if (fromEntity != null) {
            float progress = (entity.prevProgress + (entity.getProgress() - entity.prevProgress) * partialTicks) / EntityTendonSegment.MAX_EXTEND_TIME;
            Vec3 distVec = getPositionOfPriorMob(entity, fromEntity, partialTicks).subtract(x, y, z);
            Vec3 to = distVec.scale(1F - progress);
            Vec3 from = distVec;
            collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(RenderMurmurBody.TEXTURE), (pose, neckConsumer) -> {
                ModelMurmurNeck.THIN = true;
                int segmentCount = 0;
                Vec3 currentNeckButt = from;
                double remainingDistance = to.distanceTo(from);
                while (segmentCount < RenderMurmurHead.MAX_NECK_SEGMENTS && remainingDistance > 0) {
                    remainingDistance = Math.min(from.distanceTo(to), 0.5F);
                    Vec3 linearVec = to.subtract(currentNeckButt);
                    Vec3 powVec = new Vec3(modifyVecAngle(linearVec.x), modifyVecAngle(linearVec.y), modifyVecAngle(linearVec.z));
                    Vec3 smoothedVec = powVec;
                    Vec3 next = smoothedVec.normalize().scale(remainingDistance).add(currentNeckButt);
                    int neckLight = getLightColor(entity, to.add(currentNeckButt).add(x, y, z));
                    RenderMurmurHead.renderNeckCube(currentNeckButt, next, pose, neckConsumer, neckLight, OverlayTexture.NO_OVERLAY, 0);
                    currentNeckButt = next;
                    segmentCount++;
                }
                ModelMurmurNeck.THIN = false;
            });
            if (entity.hasClaw() || entity.isRetracting()) {
                poseStack.pushPose();
                poseStack.translate(to.x, to.y, to.z);
                float rotY = (float) (Mth.atan2(to.x, to.z) * (double) Mth.RAD_TO_DEG);
                float rotX = (float) (-(Mth.atan2(to.y, to.horizontalDistance()) * (double) Mth.RAD_TO_DEG));
                CLAW_MODEL.setAttributes(rotX, rotY, 1 - progress);
                int clawLight = getLightColor(entity, to.add(x, y, z));
                collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(CLAW_TEXTURE), (pose, clawConsumer) ->
                    AlexAdvancedEntityModel.withCitadelSubmitPose(pose, new PoseStack(), scratch -> CLAW_MODEL.renderToBuffer(scratch, clawConsumer, clawLight, OverlayTexture.NO_OVERLAY, -1))
                );
                poseStack.popPose();
            }
        }
        poseStack.popPose();
        super.submit(state, poseStack, collector, cameraState);
    }

    private Vec3 getPositionOfPriorMob(EntityTendonSegment segment, Entity mob, float partialTicks) {
        double d4 = Mth.lerp(partialTicks, mob.xOld, mob.getX());
        double d5 = Mth.lerp(partialTicks, mob.yOld, mob.getY());
        double d6 = Mth.lerp(partialTicks, mob.zOld, mob.getZ());
        float f3 = 0;
        if (mob instanceof Player player && segment.isCreator(mob)) {
            float f = player.getAttackAnim(partialTicks);
            float f1 = Mth.sin(Mth.sqrt(f) * Mth.PI);
            float f2 = Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot) * Mth.DEG_TO_RAD;
            int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
            double d0 = (double) Mth.sin(f2);
            double d1 = (double) Mth.cos(f2);
            double d2 = (double) i * 0.35D;
            ItemStack itemstack = player.getMainHandItem();
            if (!itemstack.is(AMItemRegistry.TENDON_WHIP)) {
                i = -i;
            }
            if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.getCameraType().isFirstPerson()) && player == Minecraft.getInstance().player) {
                double d7 = 960.0D / (double) this.entityRenderDispatcher.options.fov().get().intValue();
                Vec3 vec3 = this.entityRenderDispatcher.camera.getNearPlane(this.entityRenderDispatcher.options.fov().get().floatValue()).getPointOnPlane((float) i * 0.6F, -1);
                vec3 = vec3.scale(d7);
                vec3 = vec3.yRot(f1 * 0.25F);
                vec3 = vec3.xRot(-f1 * 0.35F);
                d4 = Mth.lerp((double) partialTicks, player.xOld, player.getX()) + vec3.x;
                d5 = Mth.lerp((double) partialTicks, player.yOld, player.getY()) + vec3.y;
                d6 = Mth.lerp((double) partialTicks, player.zOld, player.getZ()) + vec3.z;
                f3 = player.getEyeHeight() * 0.4F;
            } else {
                d4 = Mth.lerp((double) partialTicks, player.xOld, player.getX()) - d1 * d2 - d0 * 0.2D;
                d5 = player.yOld + (double) player.getEyeHeight() + (player.getY() - player.yOld) * (double) partialTicks - 1D;
                d6 = Mth.lerp((double) partialTicks, player.zOld, player.getZ()) - d0 * d2 + d1 * 0.2D;
                f3 = (player.isCrouching() ? -0.1875F : 0.0F) - player.getEyeHeight() * 0.3F;
            }
        }

        return new Vec3(d4, d5 + f3, d6);
    }

    private double modifyVecAngle(double dimension) {
        float abs = (float) Math.abs(dimension);
        return Math.signum(dimension) * Mth.clamp(Math.pow(abs, 0.1), 0.05 * abs, abs);
    }

    private int getLightColor(Entity head, Vec3 vec3) {
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
}
