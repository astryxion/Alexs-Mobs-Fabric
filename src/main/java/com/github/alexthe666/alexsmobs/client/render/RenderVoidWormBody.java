package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelVoidWormBody;
import com.github.alexthe666.alexsmobs.client.model.ModelVoidWormTail;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerVoidWormGlow;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWormPart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
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

public class RenderVoidWormBody extends LivingEntityRenderer<EntityVoidWormPart, LivingEntityRenderState, RenderVoidWormBody.VoidWormSwitchingModel> {
    private static final Identifier TEXTURE_BODY = Identifier.parse("alexsmobs:textures/entity/void_worm/void_worm_body.png");
    private static final Identifier TEXTURE_BODY_HURT = Identifier.parse("alexsmobs:textures/entity/void_worm/void_worm_body_hurt.png");
    private static final Identifier TEXTURE_BODY_GLOW = Identifier.parse("alexsmobs:textures/entity/void_worm/void_worm_body_glow.png");
    private static final Identifier TEXTURE_TAIL = Identifier.parse("alexsmobs:textures/entity/void_worm/void_worm_tail.png");
    private static final Identifier TEXTURE_TAIL_HURT = Identifier.parse("alexsmobs:textures/entity/void_worm/void_worm_tail_hurt.png");
    private static final Identifier TEXTURE_TAIL_GLOW = Identifier.parse("alexsmobs:textures/entity/void_worm/void_worm_tail_glow.png");
    private static final ModelVoidWormBody BODY_MODEL = new ModelVoidWormBody(0.0F);
    private static final ModelVoidWormTail TAIL_MODEL = new ModelVoidWormTail(0.0F);

    public static final class VoidWormSwitchingModel extends EntityModel<LivingEntityRenderState> {
        private final ModelVoidWormBody bodyModel;
        private final ModelVoidWormTail tailModel;

        public VoidWormSwitchingModel(ModelVoidWormBody body, ModelVoidWormTail tail) {
            super(new ModelPart(Collections.emptyList(), Map.of()), RenderTypes::entityCutout);
            this.bodyModel = body;
            this.tailModel = tail;
        }

        public AlexAdvancedEntityModel<EntityVoidWormPart> modelFor(EntityVoidWormPart e) {
            return e.isTail() ? tailModel : bodyModel;
        }

        @Override
        public void setupAnim(LivingEntityRenderState state) {
            LivingEntity le = AlexsMobsClientKeys.getLiving(state);
            if (!(le instanceof EntityVoidWormPart e)) {
                return;
            }
            AlexAdvancedEntityModel<EntityVoidWormPart> m = modelFor(e);
            float limbSwing = state.walkAnimationPos;
            float limbSwingAmount = Math.min(1.0F, state.walkAnimationSpeed);
            float ageInTicks = state.ageInTicks;
            float netHeadYaw = state.yRot;
            float headPitch = state.xRot;
            m.prepareMobModel(e, limbSwing, limbSwingAmount, ageInTicks);
            m.setupAnim(e, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }

    /**
     * Used by {@link com.github.alexthe666.alexsmobs.client.render.layer.LayerVoidWormGlow} for the non-emissive glow pass when the parent model is {@link VoidWormSwitchingModel}.
     */
    public static AlexAdvancedEntityModel<EntityVoidWormPart> eyesMeshForLayer(LivingEntity worm, EntityModel<?> model) {
        if (!(worm instanceof EntityVoidWormPart part)) {
            return null;
        }
        if (!(model instanceof VoidWormSwitchingModel switching)) {
            return null;
        }
        return switching.modelFor(part);
    }

    public RenderVoidWormBody(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new VoidWormSwitchingModel(BODY_MODEL, TAIL_MODEL), 1F);
        this.addLayer(new LayerVoidWormGlow(this, renderManagerIn.getResourceManager()) {
            @Override
            public Identifier getGlowTexture(LivingEntity worm) {
                return ((EntityVoidWormPart) worm).isTail() ? TEXTURE_TAIL_GLOW : TEXTURE_BODY_GLOW;
            }

            @Override
            public boolean isGlowing(LivingEntity worm) {
                return !((EntityVoidWormPart) worm).isHurt();
            }

            @Override
            public float getAlpha(LivingEntity livingEntity) {
                EntityVoidWormPart worm = (EntityVoidWormPart) livingEntity;
                return (float) Mth.clamp((worm.getHealth() - worm.getHealthThreshold()) / (worm.getMaxHealth() - worm.getHealthThreshold()), 0, 1F);
            }
        });
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    private AlexAdvancedEntityModel<EntityVoidWormPart> activeModel(LivingEntityRenderState state) {
        LivingEntity le = AlexsMobsClientKeys.getLiving(state);
        return le instanceof EntityVoidWormPart e ? this.getModel().modelFor(e) : BODY_MODEL;
    }

    @Override
    public boolean shouldRender(EntityVoidWormPart worm, Frustum camera, double camX, double camY, double camZ) {
        return worm.getPortalTicks() <= 0 && super.shouldRender(worm, camera, camX, camY, camZ);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        LivingEntity le = AlexsMobsClientKeys.getLiving(state);
        if (!(le instanceof EntityVoidWormPart entity)) {
            return TEXTURE_BODY;
        }
        if (entity.isHurt()) {
            return entity.isTail() ? TEXTURE_TAIL_HURT : TEXTURE_BODY_HURT;
        } else {
            return entity.isTail() ? TEXTURE_TAIL : TEXTURE_BODY;
        }
    }

    @Override
    protected void setupRotations(LivingEntityRenderState state, PoseStack matrixStackIn, float bodyRot, float scaleFactor) {
        LivingEntity le = AlexsMobsClientKeys.getLiving(state);
        if (!(le instanceof EntityVoidWormPart entityLiving)) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        Pose pose = entityLiving.getPose();
        if (pose != Pose.SLEEPING) {
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F - entityLiving.getWormYaw(partialTicks)));
        }
        if (state.deathTime > 0) {
            float f = ((float) state.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(f * 90.0F));
        }
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        LivingEntity le = AlexsMobsClientKeys.getLiving(state);
        if (le instanceof EntityVoidWormPart entitylivingbaseIn) {
            float s = entitylivingbaseIn.getWormScale();
            matrixStackIn.scale(s, s, s);
        }
    }

    @Override
    protected boolean shouldShowName(EntityVoidWormPart entity, double distance) {
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
            AlexAdvancedEntityModel<EntityVoidWormPart> mesh = this.activeModel(state);
            PoseStack citadelPoseStack = new PoseStack();
            collector.submitCustomGeometry(poseStack, renderType, (pose, consumer) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> mesh.renderToBuffer(scratch, consumer, state.lightCoords, finalOverlay, finalTint))
            );
        }
        if (this.shouldRenderLayers(state) && !this.layers.isEmpty()) {
            this.getModel().setupAnim(state);
            for (RenderLayer<LivingEntityRenderState, VoidWormSwitchingModel> layer : this.layers) {
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
