package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelGiantSquid;
import com.github.alexthe666.alexsmobs.entity.EntityGiantSquid;
import com.github.alexthe666.alexsmobs.entity.EntityGiantSquidPart;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Pose;

public class RenderGiantSquid extends MobRenderer<EntityGiantSquid, LivingEntityRenderState, CitadelEntityModelBridge<EntityGiantSquid>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/giant_squid.png");
    private static final Identifier TEXTURE_BLUE = Identifier.parse("alexsmobs:textures/entity/giant_squid_blue.png");
    private static final Identifier TEXTURE_DEPRESSURIZED = Identifier.parse("alexsmobs:textures/entity/giant_squid_depressurized.png");

    private final PoseStack citadelPoseScratch = new PoseStack();

    public RenderGiantSquid(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelGiantSquid()), 1F);
        this.addLayer(new LayerDepressurization());
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
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
            this.model.submitAnimatedCitadel(poseStack, collector, renderType, state, state.lightCoords, overlay, tint, this.citadelPoseScratch);
        }
        if (this.shouldRenderLayers(state) && !this.layers.isEmpty()) {
            this.model.setupAnim(state);
            for (RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityGiantSquid>> layer : this.layers) {
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
    protected float getFlipDegrees() {
        return 0.0F;
    }

    @Override
    public boolean shouldRender(EntityGiantSquid livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        if (livingEntityIn.isCaptured() && livingEntityIn.isAlive()) {
            return false;
        }
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        }
        for (EntityGiantSquidPart part : livingEntityIn.allParts) {
            if (camera.isVisible(part.getBoundingBox())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityGiantSquid entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityGiantSquid s ? s : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isBlue() ? TEXTURE_BLUE : TEXTURE;
    }

    private class LayerDepressurization extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityGiantSquid>> {

        LayerDepressurization() {
            super(RenderGiantSquid.this);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityGiantSquid squid = AlexsMobsClientKeys.getLiving(state) instanceof EntityGiantSquid s ? s : null;
            if (squid == null) {
                return;
            }
            float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
            float alpha = squid.prevDepressurization + (squid.getDepressurization() - squid.prevDepressurization) * partialTicks;
            if (alpha < 0.01F) {
                return;
            }
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            int tint = AMColorUtil.packColor(1.0F, 1.0F, 1.0F, alpha);
            this.getParentModel().submitAnimatedCitadel(
                    matrixStackIn,
                    collector,
                    RenderTypes.entityTranslucent(TEXTURE_DEPRESSURIZED),
                    state,
                    packedLightIn,
                    overlay,
                    tint,
                    RenderGiantSquid.this.citadelPoseScratch);
        }
    }
}
