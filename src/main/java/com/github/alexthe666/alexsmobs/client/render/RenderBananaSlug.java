package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel.CitadelLivingRenderState;
import com.github.alexthe666.alexsmobs.client.model.ModelBananaSlug;
import com.github.alexthe666.alexsmobs.entity.EntityBananaSlug;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
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
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import org.jspecify.annotations.Nullable;

public class RenderBananaSlug extends MobRenderer<EntityBananaSlug, CitadelLivingRenderState, AlexAdvancedEntityModel.CitadelEntityModelBridge<EntityBananaSlug, ModelBananaSlug>> {

    private static final Identifier TEXTURE_0 = Identifier.parse("alexsmobs:textures/entity/banana_slug/banana_slug_0.png");
    private static final Identifier TEXTURE_1 = Identifier.parse("alexsmobs:textures/entity/banana_slug/banana_slug_1.png");
    private static final Identifier TEXTURE_2 = Identifier.parse("alexsmobs:textures/entity/banana_slug/banana_slug_2.png");
    private static final Identifier TEXTURE_3 = Identifier.parse("alexsmobs:textures/entity/banana_slug/banana_slug_3.png");
    private static final Identifier TEXTURE_SLIME = Identifier.parse("alexsmobs:textures/entity/banana_slug/banana_slug_slime.png");

    private final ModelBananaSlug bananaModel;

    /** See {@link AlexAdvancedEntityModel#withCitadelSubmitPose}. */
    private final PoseStack citadelPoseScratch = new PoseStack();

    public RenderBananaSlug(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new AlexAdvancedEntityModel.CitadelEntityModelBridge<>(new ModelBananaSlug()), 0.2F);
        this.bananaModel = this.model.delegate();
        this.addLayer(new LayerSlime(this.bananaModel));
    }

    @Override
    public CitadelLivingRenderState createRenderState() {
        return new CitadelLivingRenderState();
    }

    @Override
    public void extractRenderState(EntityBananaSlug entity, CitadelLivingRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.citadelEntity = entity;
    }

    @Override
    protected @Nullable RenderType getRenderType(CitadelLivingRenderState state, boolean isBodyVisible, boolean forceTransparent, boolean appearGlowing) {
        Identifier texture = this.getTextureLocation(state);
        if (forceTransparent) {
            return RenderTypes.entityTranslucentCullItemTarget(texture);
        } else if (isBodyVisible) {
            return this.model.renderType(texture);
        } else {
            return appearGlowing ? RenderTypes.outline(texture) : null;
        }
    }

    @Override
    protected void scale(CitadelLivingRenderState state, PoseStack poseStack) {
        poseStack.scale(0.9F, 0.9F, 0.9F);
    }

    private Direction rotate(Direction attachmentFacing) {
        return attachmentFacing.getAxis() == Direction.Axis.Y ? Direction.UP : attachmentFacing;
    }

    private void rotateForAngle(PoseStack matrixStackIn, Direction rotate, float f) {
        if (rotate.getAxis() != Direction.Axis.Y) {
            matrixStackIn.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F * f));
        }
        switch (rotate) {
            case DOWN:
                matrixStackIn.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180.0F * f));
                break;
            case UP:
                break;
            case NORTH:
                matrixStackIn.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180.0F * f));
                break;
            case SOUTH:
                break;
            case WEST:
                matrixStackIn.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90F * f));
                break;
            case EAST:
                matrixStackIn.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90F * f));
                break;
        }
    }

    @Override
    protected void setupRotations(CitadelLivingRenderState state, PoseStack matrixStackIn, float bodyRot, float scaleFactor) {
        EntityBananaSlug entityLiving = (EntityBananaSlug) state.citadelEntity;
        if (entityLiving == null) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        float rotationYaw = bodyRot;
        if (entityLiving.isPassenger()) {
            super.setupRotations(state, matrixStackIn, state.bodyRot, scaleFactor);
            return;
        }
        if (this.isShaking(state)) {
            rotationYaw += (float) (Math.cos((double) entityLiving.tickCount * 3.25D) * Math.PI * (double) 0.4F);
        }
        float trans = state.isBaby ? 0.2F : 0.4F;
        Pose pose = state.pose;
        if (pose != Pose.SLEEPING) {
            float progress = (entityLiving.prevAttachChangeProgress + (entityLiving.attachChangeProgress - entityLiving.prevAttachChangeProgress) * partialTicks) * 0.2F;
            float yawMul = 0F;
            if (entityLiving.prevAttachDir == entityLiving.getAttachmentFacing() && entityLiving.getAttachmentFacing().getAxis() == Direction.Axis.Y) {
                yawMul = 1.0F;
            }
            matrixStackIn.mulPose(com.mojang.math.Axis.YP.rotationDegrees((180.0F - yawMul * rotationYaw)));
            matrixStackIn.translate(0.0D, trans, 0.0D);
            float prevProg = 1F - progress;
            rotateForAngle(matrixStackIn, rotate(entityLiving.prevAttachDir), prevProg);
            rotateForAngle(matrixStackIn, rotate(entityLiving.getAttachmentFacing()), progress);
            if (entityLiving.getAttachmentFacing() != Direction.DOWN) {
                matrixStackIn.translate(0.0D, trans, 0.0D);
                if (entityLiving.getDeltaMovement().y <= -0.001F) {
                    matrixStackIn.mulPose(com.mojang.math.Axis.YN.rotationDegrees(180 * progress));
                }
                matrixStackIn.translate(0.0D, -trans, 0.0D);
            }
            matrixStackIn.translate(0.0D, -trans, 0.0D);
        }

        if (state.deathTime > 0) {
            float f = (state.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            matrixStackIn.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(f * 90.0F));
        } else if (state.isAutoSpinAttack) {
            matrixStackIn.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F - state.xRot));
            matrixStackIn.mulPose(com.mojang.math.Axis.YP.rotationDegrees(((float) entityLiving.tickCount + partialTicks) * -75.0F));
        } else if (pose == Pose.SLEEPING) {

        } else if (entityLiving.hasCustomName()) {
            String s = ChatFormatting.stripFormatting(entityLiving.getName().getString());
            if (("Dinnerbone".equals(s) || "Grumm".equals(s))) {
                matrixStackIn.translate(0.0D, (double) (entityLiving.getBbHeight() + 0.1F), 0.0D);
                matrixStackIn.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180.0F));
            }
        }
    }

    @Override
    public Identifier getTextureLocation(CitadelLivingRenderState state) {
        EntityBananaSlug entity = (EntityBananaSlug) state.citadelEntity;
        if (entity == null) {
            return TEXTURE_0;
        }
        return switch (entity.getVariant()) {
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            case 3 -> TEXTURE_3;
            default -> TEXTURE_0;
        };
    }

    @Override
    public void submit(CitadelLivingRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        poseStack.pushPose();
        if (state.hasPose(Pose.SLEEPING) && state.bedOrientation != null) {
            float f = state.eyeHeight - 0.1F;
            Direction direction = state.bedOrientation;
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
            collector.submitCustomGeometry(poseStack, renderType, (pose, consumer) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, this.citadelPoseScratch, s ->
                    this.bananaModel.renderToBuffer(s, consumer, state.lightCoords, finalOverlay, finalTint))
            );
        }
        if (this.shouldRenderLayers(state) && !this.layers.isEmpty()) {
            this.model.setupAnim(state);
            for (RenderLayer<CitadelLivingRenderState, AlexAdvancedEntityModel.CitadelEntityModelBridge<EntityBananaSlug, ModelBananaSlug>> layer : this.layers) {
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

    private final class LayerSlime extends RenderLayer<CitadelLivingRenderState, AlexAdvancedEntityModel.CitadelEntityModelBridge<EntityBananaSlug, ModelBananaSlug>> {
        private final ModelBananaSlug slimeModel;

        LayerSlime(ModelBananaSlug slimeModel) {
            super(RenderBananaSlug.this);
            this.slimeModel = slimeModel;
        }

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, CitadelLivingRenderState state, float netHeadYaw, float headPitch) {
            if (!(state.citadelEntity instanceof EntityBananaSlug entitylivingbaseIn)) {
                return;
            }
            float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
            float alpha = entitylivingbaseIn.prevTrailVisability + (entitylivingbaseIn.trailVisability - entitylivingbaseIn.prevTrailVisability) * partialTicks;
            if (alpha > 0) {
                collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(TEXTURE_SLIME), (pose, consumer) ->
                    AlexAdvancedEntityModel.withCitadelSubmitPose(pose, RenderBananaSlug.this.citadelPoseScratch, s ->
                        this.slimeModel.renderToBuffer(s, consumer, packedLight, LivingEntityRenderer.getOverlayCoords(state, 0.0F), -1))
                );
            }
        }
    }
}
