package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel.CitadelLivingRenderState;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelSunbird;
import com.github.alexthe666.alexsmobs.entity.EntitySunbird;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Pose;
import org.joml.Matrix4f;

public class RenderSunbird extends MobRenderer<EntitySunbird, CitadelLivingRenderState, AlexAdvancedEntityModel.CitadelEntityModelBridge<EntitySunbird, ModelSunbird>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/sunbird.png");
    private static final Identifier TEXTURE_GLOW = Identifier.parse("alexsmobs:textures/entity/sunbird_glow.png");

    private final ModelSunbird sunbirdModel;

    public RenderSunbird(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new AlexAdvancedEntityModel.CitadelEntityModelBridge<>(new ModelSunbird()), 0.5F);
        this.sunbirdModel = this.model.delegate();
        this.addLayer(new LayerScorch(this.sunbirdModel));
    }

    @Override
    public CitadelLivingRenderState createRenderState() {
        return new CitadelLivingRenderState();
    }

    @Override
    public void extractRenderState(EntitySunbird entity, CitadelLivingRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.citadelEntity = entity;
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, int packedLight, float x, float y, int u, int v) {
        consumer.addVertex(pose, x, y, 0.0F)
            .setColor((int) (255 * 255), (int) (255 * 255), (int) (255 * 255), (int) (100 * 255))
            .setUv((float) u, (float) v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setUv2(packedLight & 0xFFFF, packedLight >> 16)
            .setNormal(0.0F, 1.0F, 0.0F);
    }

    @Override
    protected void scale(CitadelLivingRenderState state, PoseStack matrixStackIn) {
    }

    @Override
    protected int getBlockLightLevel(EntitySunbird entityIn, BlockPos pos) {
        return 15;
    }

    @Override
    public Identifier getTextureLocation(CitadelLivingRenderState state) {
        return TEXTURE;
    }

    @Override
    public void submit(CitadelLivingRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
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
            this.model.setupAnim(state);
            int finalTint = tint;
            int finalOverlay = overlay;
            PoseStack citadelPoseStack = new PoseStack();
            collector.submitCustomGeometry(poseStack, renderType, (pose, consumer) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> this.sunbirdModel.renderToBuffer(scratch, consumer, state.lightCoords, finalOverlay, finalTint))
            );
        }
        EntitySunbird entity = state.citadelEntity instanceof EntitySunbird e ? e : null;
        if (entity != null) {
            float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
            float ageInTicks = entity.tickCount + partialTicks;
            float scorchScale = (12.0F + (float) Math.sin(ageInTicks * 0.3F)) * entity.getScorchProgress(partialTicks);
            if (scorchScale > 0.0F) {
                poseStack.pushPose();
                poseStack.translate(0.0F, entity.getBbHeight() * 0.5F, 0.0F);
                poseStack.mulPose(cameraState.orientation);
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                poseStack.pushPose();
                poseStack.mulPose(Axis.ZP.rotationDegrees(ageInTicks * 8.0F));
                poseStack.translate(-scorchScale * 0.5F, -scorchScale * 0.5F, 0.0F);
                Matrix4f mat = poseStack.last().pose();
                int light = state.lightCoords;
                RenderType shineType = AMRenderTypes.getSunbirdShine();
                collector.submitCustomGeometry(poseStack, shineType, (pose, consumer) -> {
                    vertex(consumer, mat, light, 0.0F, 0.0F, 0, 1);
                    vertex(consumer, mat, light, scorchScale, 0.0F, 1, 1);
                    vertex(consumer, mat, light, scorchScale, scorchScale, 1, 0);
                    vertex(consumer, mat, light, 0.0F, scorchScale, 0, 0);
                });
                poseStack.popPose();
                poseStack.popPose();
            }
        }
        if (this.shouldRenderLayers(state) && !this.layers.isEmpty()) {
            this.model.setupAnim(state);
            for (RenderLayer<CitadelLivingRenderState, AlexAdvancedEntityModel.CitadelEntityModelBridge<EntitySunbird, ModelSunbird>> layer : this.layers) {
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

    private final class LayerScorch extends RenderLayer<CitadelLivingRenderState, AlexAdvancedEntityModel.CitadelEntityModelBridge<EntitySunbird, ModelSunbird>> {
        private final ModelSunbird glowModel;

        LayerScorch(ModelSunbird glowModel) {
            super(RenderSunbird.this);
            this.glowModel = glowModel;
        }

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, CitadelLivingRenderState state, float netHeadYaw, float headPitch) {
            if (!(state.citadelEntity instanceof EntitySunbird entitylivingbaseIn)) {
                return;
            }
            float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
            float alpha = entitylivingbaseIn.getScorchProgress(partialTicks);
            if (alpha <= 0.0F) {
                return;
            }
            this.getParentModel().setupAnim(state);
            PoseStack citadelPoseStack = new PoseStack();
            collector.submitCustomGeometry(poseStack, AMRenderTypes.getEyesAlphaEnabled(TEXTURE_GLOW), (pose, consumer) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> this.glowModel.renderToBuffer(scratch, consumer, 240, LivingEntityRenderer.getOverlayCoords(state, 0.0F), -1))
            );
        }
    }
}
