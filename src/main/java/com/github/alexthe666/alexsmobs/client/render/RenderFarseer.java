package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelFarseer;
import com.github.alexthe666.alexsmobs.entity.EntityFarseer;
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
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class RenderFarseer extends MobRenderer<EntityFarseer, LivingEntityRenderState, CitadelEntityModelBridge<EntityFarseer>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/farseer/farseer.png");
    private static final Identifier TEXTURE_ANGRY = Identifier.parse("alexsmobs:textures/entity/farseer/farseer_angry.png");
    private static final Identifier TEXTURE_CLAWS = Identifier.parse("alexsmobs:textures/entity/farseer/farseer_claws.png");
    private static final Identifier TEXTURE_EYE = Identifier.parse("alexsmobs:textures/entity/farseer/farseer_eye.png");
    private static final Identifier TEXTURE_SCARS = Identifier.parse("alexsmobs:textures/entity/farseer/farseer_scars.png");
    private static final Identifier[] PORTAL_TEXTURES = new Identifier[]{
        Identifier.parse("alexsmobs:textures/entity/farseer/portal_0.png"),
        Identifier.parse("alexsmobs:textures/entity/farseer/portal_1.png"),
        Identifier.parse("alexsmobs:textures/entity/farseer/portal_2.png"),
        Identifier.parse("alexsmobs:textures/entity/farseer/portal_3.png")};
    private static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0D) / 2.0D);
    private static final ModelFarseer EYE_MODEL = new ModelFarseer(0.1f);
    private static final ModelFarseer SCARS_MODEL = new ModelFarseer(0.05f);
    private static final ModelFarseer AFTERIMAGE_MODEL = new ModelFarseer(0.05f);

    private final ModelFarseer farseerModel;

    public RenderFarseer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelFarseer(0.0F)), 0.9F);
        this.farseerModel = (ModelFarseer) this.model.citadel();
        this.addLayer(new LayerOverlay());
    }

    @Nullable
    private EntityFarseer resolveFarseer(LivingEntityRenderState state) {
        if (AlexsMobsClientKeys.getLiving(state) instanceof EntityFarseer farseer) {
            return farseer;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return null;
        }
        Integer entityId = extractRenderStateEntityId(state);
        if (entityId != null && minecraft.level.getEntity(entityId) instanceof EntityFarseer farseer) {
            return farseer;
        }
        return null;
    }

    @Nullable
    private static Integer extractRenderStateEntityId(LivingEntityRenderState state) {
        try {
            Field idField = state.getClass().getField("id");
            Object value = idField.get(state);
            return value instanceof Integer i ? i : null;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected float getShadowRadius(LivingEntityRenderState state) {
        EntityFarseer entityIn = resolveFarseer(state);
        if (entityIn == null) {
            return super.getShadowRadius(state);
        }
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = this.isBodyVisible(state);
        boolean flag1 = !flag && !state.isInvisibleToPlayer;
        boolean flag2 = minecraft.shouldEntityAppearGlowing(entityIn);
        RenderType rendertype = this.getRenderType(state, flag, flag1, flag2);
        if (rendertype != null) {
            float portalLevel = entityIn.getFarseerOpacity(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));
            return 0.9F * portalLevel;
        }
        return super.getShadowRadius(state);
    }

    @Override
    public boolean shouldRender(EntityFarseer livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else {
            if (livingEntityIn.hasLaser()) {
                LivingEntity livingentity = livingEntityIn.getLaserTarget();
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
    public void submit(LivingEntityRenderState state, PoseStack matrixStackIn, SubmitNodeCollector bufferIn, CameraRenderState cameraState) {
        EntityFarseer entityIn = resolveFarseer(state);
        if (entityIn == null) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        LivingEntity laserTarget = entityIn.getLaserTarget();
        float faceCameraAmount = entityIn.getFacingCameraAmount(partialTicks);
        Quaternionf camera = cameraState.orientation;

        matrixStackIn.pushPose();

        boolean shouldSit = entityIn.isPassenger() && entityIn.getVehicle() != null;
        this.model.setCitadelYoung(entityIn.isBaby());
        float f = Mth.rotLerp(partialTicks, entityIn.yBodyRotO, entityIn.yBodyRot);
        float f1 = Mth.rotLerp(partialTicks, entityIn.yHeadRotO, entityIn.yHeadRot);
        float f2 = f1 - f;
        if (shouldSit && entityIn.getVehicle() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity) entityIn.getVehicle();
            f = Mth.rotLerp(partialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
            f2 = f1 - f;
            float f3 = Mth.wrapDegrees(f2);
            if (f3 < -85.0F) {
                f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
                f3 = 85.0F;
            }

            f = f1 - f3;
            if (f3 * f3 > 2500.0F) {
                f += f3 * 0.2F;
            }

            f2 = f1 - f;
        }

        float f6 = Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot());
        if (entityIn.getPose() == Pose.SLEEPING) {
            Direction direction = entityIn.getBedOrientation();
            if (direction != null) {
                float f4 = entityIn.getEyeHeight(Pose.STANDING) - 0.1F;
                matrixStackIn.translate((float) (-direction.getStepX()) * f4, 0.0D, (float) (-direction.getStepZ()) * f4);
            }
        }

        float f7 = state.ageInTicks;
        if (faceCameraAmount != 0) {
            matrixStackIn.mulPose(camera);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
        }
        this.farseerSetupRotations(entityIn, state, matrixStackIn, f7, f, partialTicks);
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
        this.scale(state, matrixStackIn);
        matrixStackIn.translate(0.0D, -1.501F, 0.0D);
        float f8 = 0.0F;
        float f5 = 0.0F;
        if (!shouldSit && entityIn.isAlive()) {
            f8 = entityIn.walkAnimation.position(partialTicks);
            f5 = entityIn.walkAnimation.position() - entityIn.walkAnimation.speed() * (1.0F - partialTicks);
            if (entityIn.isBaby()) {
                f5 *= 3.0F;
            }

            if (f8 > 1.0F) {
                f8 = 1.0F;
            }
        }

        this.farseerModel.prepareMobModel(entityIn, f5, f8, partialTicks);
        this.farseerModel.setupAnim(entityIn, f5, f8, f7, f2, f6);
        EYE_MODEL.setupAnim(entityIn, f5, f8, f7, f2, f6);
        SCARS_MODEL.setupAnim(entityIn, f5, f8, f7, f2, f6);
        AFTERIMAGE_MODEL.setupAnim(entityIn, f5, f8, f7, f2, f6);
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = this.isBodyVisible(state);
        boolean flag1 = !flag && !state.isInvisibleToPlayer;
        boolean flag2 = minecraft.shouldEntityAppearGlowing(entityIn);
        RenderType rendertype = this.getRenderType(state, flag, flag1, flag2);
        if (rendertype != null) {
            int i = LivingEntityRenderer.getOverlayCoords(state, this.getWhiteOverlayProgress(state));
            this.renderFarseerModel(matrixStackIn, bufferIn, rendertype, partialTicks, state.lightCoords, i, flag1 ? 0.15F : Mth.clamp(entityIn.getFarseerOpacity(partialTicks), 0, 1), entityIn);
        }
        if (!entityIn.isSpectator()) {
            this.model.setupAnim(state);
            for (RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityFarseer>> layerrenderer : this.layers) {
                layerrenderer.submit(matrixStackIn, bufferIn, state.lightCoords, state, state.yRot, state.xRot);
            }
        }

        matrixStackIn.popPose();

        if (entityIn.getAnimation() == EntityFarseer.ANIMATION_EMERGE) {
            matrixStackIn.pushPose();
            matrixStackIn.scale(3.0F, 3.0F, 3.0F);
            matrixStackIn.mulPose(camera);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
            PoseStack.Pose posestack$pose = matrixStackIn.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            int portalTexture = Mth.clamp(entityIn.getPortalFrame(), 0, PORTAL_TEXTURES.length - 1);
            float portalAlpha = entityIn.getPortalOpacity(partialTicks);
            RenderType portalType = RenderTypes.entityTranslucentEmissive(PORTAL_TEXTURES[portalTexture]);
            int packedLightIn = state.lightCoords;
            bufferIn.submitCustomGeometry(matrixStackIn, portalType, (pose, portalConsumer) -> {
                portalVertex(portalConsumer, matrix4f, matrix3f, packedLightIn, 0.0F, 0, 0, 1, portalAlpha);
                portalVertex(portalConsumer, matrix4f, matrix3f, packedLightIn, 1.0F, 0, 1, 1, portalAlpha);
                portalVertex(portalConsumer, matrix4f, matrix3f, packedLightIn, 1.0F, 1, 1, 0, portalAlpha);
                portalVertex(portalConsumer, matrix4f, matrix3f, packedLightIn, 0.0F, 1, 0, 0, portalAlpha);
            });
            matrixStackIn.popPose();
        }
        if (entityIn.hasLaser() && laserTarget != null && !laserTarget.isRemoved()) {
            float laserProgress = (entityIn.prevLaserLvl + (entityIn.getLaserAttackLvl() - entityIn.prevLaserLvl) * partialTicks) / (float) EntityFarseer.LASER_ATTACK_DURATION;
            float laserHeight = entityIn.getEyeHeight();
            Vec3 angryShake = Vec3.ZERO;
            double d0 = Mth.lerp(partialTicks, laserTarget.xo, laserTarget.getX()) - Mth.lerp(partialTicks, entityIn.xo, entityIn.getX()) - angryShake.x;
            double d1 = Mth.lerp(partialTicks, laserTarget.yo, laserTarget.getY()) + laserTarget.getEyeHeight() - Mth.lerp(partialTicks, entityIn.yo, entityIn.getY()) - angryShake.y - laserHeight;
            double d2 = Mth.lerp(partialTicks, laserTarget.zo, laserTarget.getZ()) - Mth.lerp(partialTicks, entityIn.zo, entityIn.getZ()) - angryShake.z;
            double d4 = Math.sqrt(d0 * d0 + d2 * d2);
            float laserY = (float) (Mth.atan2(d2, d0) * (double) Mth.RAD_TO_DEG) - 90.0F;
            float laserX = (float) (-(Mth.atan2(d1, d4) * (double) Mth.RAD_TO_DEG));
            matrixStackIn.pushPose();
            matrixStackIn.translate(0, laserHeight, 0);
            matrixStackIn.mulPose(Axis.YN.rotationDegrees(laserY));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(laserX));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(90));
            float length = entityIn.getLaserDistance() * laserProgress;
            float width = (1.5F - laserProgress) * 2F;
            float speed = 1F + laserProgress * laserProgress * 5F;
            PoseStack.Pose posestack$pose = matrixStackIn.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            int j = 255;
            long systemTime = Util.getMillis() * 7L;
            float u = (float) (systemTime % 30000L) / 30000.0F;
            float v = (float) Math.floor((systemTime % 3000L) / 3000.0F * 4.0F) * 0.25F + (float) Math.sin(systemTime / 30000F) * 0.05F + ((float) (systemTime % 20000L) / 20000.0F * speed);
            RenderType beamType = AMRenderTypes.getFarseerBeam();
            bufferIn.submitCustomGeometry(matrixStackIn, beamType, (pose, beamStatic) -> {
                laserOriginVertex(beamStatic, matrix4f, matrix3f, j, u, v);
                laserLeftCornerVertex(beamStatic, matrix4f, matrix3f, length, width, u, v);
                laserRightCornerVertex(beamStatic, matrix4f, matrix3f, length, width, u, v);
                laserLeftCornerVertex(beamStatic, matrix4f, matrix3f, length, width, u, v);
            });

            matrixStackIn.popPose();
        }

        if (state.leashStates != null) {
            for (net.minecraft.client.renderer.entity.state.EntityRenderState.LeashState leashState : state.leashStates) {
                bufferIn.submitLeash(matrixStackIn, leashState);
            }
        }
        this.submitNameDisplay(state, matrixStackIn, bufferIn, cameraState);
    }

    private void renderFarseerModel(PoseStack matrixStackIn, SubmitNodeCollector source, RenderType defRenderType, float partialTicks, int packedLightIn, int overlayColors, float alphaIn, EntityFarseer entityIn) {
        PoseStack citadelPoseStack = new PoseStack();
        if (entityIn.hasLaser()) {
            source.submitCustomGeometry(matrixStackIn, RenderTypes.entityTranslucentEmissive(TEXTURE_EYE), (pose, eyeConsumer) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> EYE_MODEL.renderToBuffer(scratch, eyeConsumer, packedLightIn, NO_OVERLAY, -1)));
        }
        float hurt = Math.max(entityIn.hurtTime, entityIn.deathTime);
        float defAlpha = alphaIn * 0.2F;
        float afterimageSpeed = 0.3F;
        if (hurt > 0) {
            afterimageSpeed = Math.min(hurt / 20F, 1F) + 0.3F;
            source.submitCustomGeometry(matrixStackIn, RenderTypes.entityTranslucentEmissive(TEXTURE_SCARS), (pose, scarsConsumer) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> SCARS_MODEL.renderToBuffer(scratch, scarsConsumer, packedLightIn, overlayColors, -1)));
        }
        source.submitCustomGeometry(matrixStackIn, defRenderType, (pose, consumer) ->
            AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> this.farseerModel.renderToBuffer(scratch, consumer, packedLightIn, overlayColors, -1)));

        matrixStackIn.pushPose();
        matrixStackIn.popPose();

        AFTERIMAGE_MODEL.eye.showModel = false;
        RenderType afterimage = RenderTypes.entityTranslucentEmissive(entityIn.isAngry() ? TEXTURE_ANGRY : TEXTURE);
        Vec3 colorOffset = entityIn.getLatencyOffsetVec(10, partialTicks).scale(-0.2F).add(entityIn.angryShakeVec.scale(0.3F));
        Vec3 redOffset = colorOffset.add(entityIn.calculateAfterimagePos(partialTicks, false, afterimageSpeed));
        Vec3 blueOffset = colorOffset.add(entityIn.calculateAfterimagePos(partialTicks, true, afterimageSpeed));
        float scale = (float) Mth.clamp(colorOffset.length() * 0.1F, 0, 1F);
        float angryProgress = entityIn.prevAngryProgress + (entityIn.angryProgress - entityIn.prevAngryProgress) * partialTicks;
        float afterimageAlpha1 = defAlpha * Math.max(((float) Math.sin((entityIn.tickCount + partialTicks) * 0.2F) + 1F) * 0.3F, angryProgress * 0.2F);
        float afterimageAlpha2 = defAlpha * Math.max(((float) Math.cos((entityIn.tickCount + partialTicks) * 0.2F) + 1F) * 0.3F, angryProgress * 0.2F);

        matrixStackIn.pushPose();
        matrixStackIn.scale(scale + 1F, scale + 1F, scale + 1F);
        matrixStackIn.pushPose();
        matrixStackIn.translate(redOffset.x, redOffset.y, redOffset.z);
        int o = overlayColors;
        source.submitCustomGeometry(matrixStackIn, afterimage, (pose, consumer) ->
            AlexAdvancedEntityModel.withCitadelSubmitPose(pose, new PoseStack(), scratch -> AFTERIMAGE_MODEL.renderToBuffer(scratch, consumer, 240, o, -1)));
        matrixStackIn.popPose();
        matrixStackIn.pushPose();
        matrixStackIn.translate(blueOffset.x, blueOffset.y, blueOffset.z);
        source.submitCustomGeometry(matrixStackIn, afterimage, (pose, consumer) ->
            AlexAdvancedEntityModel.withCitadelSubmitPose(pose, new PoseStack(), scratch -> AFTERIMAGE_MODEL.renderToBuffer(scratch, consumer, 240, o, -1)));
        matrixStackIn.popPose();
        matrixStackIn.popPose();
        AFTERIMAGE_MODEL.eye.showModel = true;
    }

    private static void laserOriginVertex(VertexConsumer p_114220_, Matrix4f p_114221_, Matrix3f p_114092_, int p_114222_, float xOffset, float yOffset) {
        p_114220_.addVertex(0.0F, 0.0F, 0.0F).setColor((int) (255 * 255), (int) (255 * 255), (int) (255 * 255), (int) (255 * 255)).setUv(xOffset + 0.5F, yOffset).setOverlay(NO_OVERLAY).setUv2(240 & 0xFFFF, 240 >> 16).setNormal(0.0F, 1.0F, 0.0F);
    }

    private static void laserLeftCornerVertex(VertexConsumer p_114215_, Matrix4f p_114216_, Matrix3f p_114092_, float p_114217_, float p_114218_, float xOffset, float yOffset) {
        p_114215_.addVertex(0, 0.0F, 0.0F).setColor((int) (255 * 255), (int) (255 * 255), (int) (255 * 255), (int) (0 * 255)).setUv(xOffset, yOffset + 1).setOverlay(NO_OVERLAY).setUv2(240 & 0xFFFF, 240 >> 16).setNormal(0.0F, -1.0F, 0.0F);
    }

    private static void laserRightCornerVertex(VertexConsumer p_114224_, Matrix4f p_114225_, Matrix3f p_114092_, float p_114226_, float p_114227_, float xOffset, float yOffset) {
        p_114224_.addVertex(0, 0.0F, 0.0F).setColor((int) (255 * 255), (int) (255 * 255), (int) (255 * 255), (int) (0 * 255)).setUv(xOffset + 1, yOffset + 1).setOverlay(NO_OVERLAY).setUv2(240 & 0xFFFF, 240 >> 16).setNormal(0.0F, -1.0F, 0.0F);
    }

    private static void portalVertex(VertexConsumer p_114090_, Matrix4f p_114091_, Matrix3f p_114092_, int p_114093_, float p_114094_, int p_114095_, int p_114096_, int p_114097_, float alpha) {
        p_114090_.addVertex(0.0F, 0.0F, 0.0F).setColor((int) (1F * 255), (int) (1F * 255), (int) (1F * 255), (int) (alpha * 255)).setUv((float) p_114096_, (float) p_114097_).setOverlay(NO_OVERLAY).setUv2(240 & 0xFFFF, 240 >> 16).setNormal(0.0F, -1.0F, 0.0F);
    }

    private void farseerSetupRotations(EntityFarseer farseer, LivingEntityRenderState state, PoseStack matrixStackIn, float f1, float f2, float f3) {
        float invCameraAmount = 1F - farseer.getFacingCameraAmount(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));

        if (this.isShaking(state)) {
            f2 += (float) (Math.cos((double) farseer.tickCount * 3.25D) * Math.PI * (double) 0.4F);
        }

        if (!farseer.hasPose(Pose.SLEEPING)) {
            matrixStackIn.mulPose(Axis.YP.rotationDegrees((180.0F - f2 * invCameraAmount)));
        }

        if (farseer.deathTime > 0) {
            float f = ((float) farseer.deathTime + f3 - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            matrixStackIn.mulPose(Axis.YP.rotationDegrees(f * 90.0F * invCameraAmount));
        } else if (isEntityUpsideDown(farseer)) {
            matrixStackIn.translate(0.0D, (double) (farseer.getBbHeight() + 0.1F), 0.0D);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
    }

    @Nullable
    @Override
    protected RenderType getRenderType(LivingEntityRenderState state, boolean normal, boolean invis, boolean outline) {
        EntityFarseer farseer = AlexsMobsClientKeys.getLiving(state) instanceof EntityFarseer f ? f : null;
        if (farseer == null) {
            return null;
        }
        Identifier id = this.getTextureLocation(state);
        if (invis || farseer.getAnimation() == EntityFarseer.ANIMATION_EMERGE) {
            return RenderTypes.entityTranslucentCullItemTarget(id);
        } else if (normal) {
            return RenderTypes.entityCutout(id);
        } else {
            return outline ? RenderTypes.outline(id) : null;
        }
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityFarseer entity = resolveFarseer(state);
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isAngry() ? TEXTURE_ANGRY : TEXTURE;
    }

    private final class LayerOverlay extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityFarseer>> {

        LayerOverlay() {
            super(RenderFarseer.this);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityFarseer entitylivingbaseIn = resolveFarseer(state);
            if (entitylivingbaseIn != null && entitylivingbaseIn.getAnimation() == EntityFarseer.ANIMATION_EMERGE) {
                int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
                this.getParentModel().setupAnim(state);
                PoseStack citadelPoseStack = new PoseStack();
                bufferIn.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(TEXTURE_CLAWS), (pose, ivertexbuilder) ->
                    AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> this.getParentModel().renderCitadelToBuffer(scratch, ivertexbuilder, packedLightIn, overlay, -1)));
            }
        }
    }
}