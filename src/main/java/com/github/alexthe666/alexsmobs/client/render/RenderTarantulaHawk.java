package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel.CitadelLivingRenderState;
import com.github.alexthe666.alexsmobs.client.model.ModelTarantulaHawk;
import com.github.alexthe666.alexsmobs.client.model.ModelTarantulaHawkBaby;
import com.github.alexthe666.alexsmobs.entity.EntityTarantulaHawk;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Pose;

import javax.annotation.Nullable;

public class RenderTarantulaHawk extends MobRenderer<
        EntityTarantulaHawk,
        CitadelLivingRenderState,
        AlexAdvancedEntityModel.CitadelEntityModelBridge<EntityTarantulaHawk, ? extends AlexAdvancedEntityModel<EntityTarantulaHawk>>> {

    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/tarantula_hawk.png");
    private static final Identifier TEXTURE_ANGRY = Identifier.parse("alexsmobs:textures/entity/tarantula_hawk_angry.png");
    private static final Identifier TEXTURE_NETHER = Identifier.parse("alexsmobs:textures/entity/tarantula_hawk_nether.png");
    private static final Identifier TEXTURE_NETHER_ANGRY = Identifier.parse("alexsmobs:textures/entity/tarantula_hawk_nether_angry.png");
    private static final Identifier TEXTURE_BABY = Identifier.parse("alexsmobs:textures/entity/tarantula_hawk_baby.png");
    private static final ModelTarantulaHawk MODEL = new ModelTarantulaHawk();
    private static final ModelTarantulaHawkBaby MODEL_BABY = new ModelTarantulaHawkBaby();

    private final AlexAdvancedEntityModel.CitadelEntityModelBridge<EntityTarantulaHawk, ModelTarantulaHawk> adultBridge;
    private final AlexAdvancedEntityModel.CitadelEntityModelBridge<EntityTarantulaHawk, ModelTarantulaHawkBaby> babyBridge;

    @SuppressWarnings("unchecked")
    public RenderTarantulaHawk(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new AlexAdvancedEntityModel.CitadelEntityModelBridge<>(MODEL), 0.5F);
        this.adultBridge = (AlexAdvancedEntityModel.CitadelEntityModelBridge<EntityTarantulaHawk, ModelTarantulaHawk>) (Object) this.model;
        this.babyBridge = new AlexAdvancedEntityModel.CitadelEntityModelBridge<>(MODEL_BABY);
    }

    @Override
    public CitadelLivingRenderState createRenderState() {
        return new CitadelLivingRenderState();
    }

    @Override
    public void extractRenderState(EntityTarantulaHawk entity, CitadelLivingRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.citadelEntity = entity;
    }

    @Override
    protected void scale(CitadelLivingRenderState state, PoseStack matrixStackIn) {
        EntityTarantulaHawk entitylivingbaseIn = state.citadelEntity instanceof EntityTarantulaHawk h ? h : null;
        if (entitylivingbaseIn == null) {
            return;
        }
        float partialTickTime = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        if (entitylivingbaseIn.isBaby()) {
            this.model = this.babyBridge;
        } else {
            this.model = this.adultBridge;
            matrixStackIn.scale(0.9F, 0.9F, 0.9F);
            float f = entitylivingbaseIn.prevDragProgress + (entitylivingbaseIn.dragProgress - entitylivingbaseIn.prevDragProgress) * partialTickTime;
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(f * 180 * 0.2F));
        }
    }

    @Override
    protected boolean isShaking(CitadelLivingRenderState state) {
        EntityTarantulaHawk hawk = state.citadelEntity instanceof EntityTarantulaHawk h ? h : null;
        return hawk != null && hawk.isScared();
    }

    @Nullable
    @Override
    protected RenderType getRenderType(CitadelLivingRenderState state, boolean b0, boolean b1, boolean b2) {
        Identifier id = this.getTextureLocation(state);
        if (b1) {
            return RenderTypes.entityTranslucentCullItemTarget(id);
        } else if (b0) {
            return RenderTypes.entityTranslucent(id);
        } else {
            return b2 ? RenderTypes.outline(id) : null;
        }
    }

    @Override
    public Identifier getTextureLocation(CitadelLivingRenderState state) {
        EntityTarantulaHawk entity = state.citadelEntity instanceof EntityTarantulaHawk e ? e : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isBaby() ? TEXTURE_BABY : entity.isNether() ? entity.isAngry() ? TEXTURE_NETHER_ANGRY : TEXTURE_NETHER : entity.isAngry() ? TEXTURE_ANGRY : TEXTURE;
    }

    @Override
    public void submit(CitadelLivingRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        EntityTarantulaHawk entityFix = state.citadelEntity instanceof EntityTarantulaHawk h ? h : null;
        this.model = entityFix != null && entityFix.isBaby() ? this.babyBridge : this.adultBridge;

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
            boolean baby = entityFix != null && entityFix.isBaby();
            collector.submitCustomGeometry(poseStack, renderType, (pose, consumer) -> {
                if (baby) {
                    MODEL_BABY.renderToBuffer(poseStack, consumer, state.lightCoords, finalOverlay, finalTint);
                } else {
                    MODEL.renderToBuffer(poseStack, consumer, state.lightCoords, finalOverlay, finalTint);
                }
            });
        }
        if (this.shouldRenderLayers(state) && !this.layers.isEmpty()) {
            this.model.setupAnim(state);
            for (RenderLayer<CitadelLivingRenderState, AlexAdvancedEntityModel.CitadelEntityModelBridge<EntityTarantulaHawk, ? extends AlexAdvancedEntityModel<EntityTarantulaHawk>>> layer : this.layers) {
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
