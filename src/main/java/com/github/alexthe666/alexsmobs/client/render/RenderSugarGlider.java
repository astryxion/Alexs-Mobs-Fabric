package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel.CitadelLivingRenderState;
import com.github.alexthe666.alexsmobs.client.model.ModelSugarGlider;
import com.github.alexthe666.alexsmobs.entity.EntitySugarGlider;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;

public class RenderSugarGlider extends MobRenderer<EntitySugarGlider, CitadelLivingRenderState, AlexAdvancedEntityModel.CitadelEntityModelBridge<EntitySugarGlider, ModelSugarGlider>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/sugar_glider.png");

    private final ModelSugarGlider sugarModel;

    /** See {@link AlexAdvancedEntityModel#withCitadelSubmitPose}. */
    private final PoseStack citadelPoseScratch = new PoseStack();

    public RenderSugarGlider(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new AlexAdvancedEntityModel.CitadelEntityModelBridge<>(new ModelSugarGlider()), 0.35F);
        this.sugarModel = this.model.delegate();
    }

    @Override
    public CitadelLivingRenderState createRenderState() {
        return new CitadelLivingRenderState();
    }

    @Override
    public void extractRenderState(EntitySugarGlider entity, CitadelLivingRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.citadelEntity = entity;
    }

    private Direction rotate(Direction attachmentFacing) {
        return attachmentFacing.getAxis() == Direction.Axis.Y ? Direction.UP : attachmentFacing;
    }

    @Override
    protected void setupRotations(CitadelLivingRenderState state, PoseStack matrixStackIn, float bodyRot, float scaleFactor) {
        EntitySugarGlider entityLiving = (EntitySugarGlider) state.citadelEntity;
        if (entityLiving == null) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        if (entityLiving.isPassenger()) {
            super.setupRotations(state, matrixStackIn, state.bodyRot, scaleFactor);
            return;
        }
        if (this.isShaking(state)) {
            bodyRot += (float) (Math.cos((double) entityLiving.tickCount * 3.25D) * Math.PI * (double) 0.4F);
        }
        float trans = state.isBaby ? 0.2F : 0.4F;
        Pose pose = state.pose;
        if (pose != Pose.SLEEPING) {
            float prevProg = entityLiving.prevAttachChangeProgress + (entityLiving.attachChangeProgress - entityLiving.prevAttachChangeProgress) * partialTicks;
            float yawMul = 0F;
            if (entityLiving.prevAttachDir == entityLiving.getAttachmentFacing() && entityLiving.getAttachmentFacing().getAxis() == Direction.Axis.Y) {
                yawMul = 1.0F;
            }
            matrixStackIn.mulPose(Axis.YP.rotationDegrees((180.0F - yawMul * bodyRot)));

            if (entityLiving.getAttachmentFacing() == Direction.DOWN) {
                matrixStackIn.translate(0.0D, trans, 0.0D);
                if (entityLiving.yOld <= entityLiving.getY()) {
                    matrixStackIn.mulPose(Axis.XP.rotationDegrees(90 * prevProg));
                } else {
                    matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90 * prevProg));
                }
                matrixStackIn.translate(0.0D, -trans, 0.0D);
            }

            matrixStackIn.translate(0.0D, trans, 0.0D);
            Quaternionf current = rotate(entityLiving.getAttachmentFacing()).getRotation();
            current.mul(1F - prevProg);
            matrixStackIn.mulPose(current);
            matrixStackIn.translate(0.0D, -trans, 0.0D);
        }

        if (state.deathTime > 0) {
            float f = ((float) state.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(f * 90.0F));
        } else if (state.isAutoSpinAttack) {
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90.0F - state.xRot));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(((float) entityLiving.tickCount + partialTicks) * -75.0F));
        } else if (entityLiving.hasCustomName()) {
            String s = ChatFormatting.stripFormatting(entityLiving.getName().getString());
            if (("Dinnerbone".equals(s) || "Grumm".equals(s))) {
                matrixStackIn.translate(0.0D, (double) (entityLiving.getBbHeight() + 0.1F), 0.0D);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
            }
        }
    }

    @Override
    protected void scale(CitadelLivingRenderState state, PoseStack matrixStackIn) {
        EntitySugarGlider mob = (EntitySugarGlider) state.citadelEntity;
        if (mob == null) {
            return;
        }
        this.sugarModel.young = mob.isBaby();
        if (mob.isPassenger() && mob.getVehicle() != null) {
            if (mob.getVehicle() instanceof Player) {
                Player mount = (Player) mob.getVehicle();
                EntityRenderer<?, ?> playerRender = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(mount);
                if (Minecraft.getInstance().player == mount && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
                } else if (playerRender instanceof LivingEntityRenderer<?, ?, ?> ler && ler.getModel() instanceof HumanoidModel<?> hm) {
                    matrixStackIn.translate(0.0F, 0.5F, 0.0F);
                    hm.head.translateAndRotate(matrixStackIn);
                    matrixStackIn.translate(0.0F, -0.5F, 0.0F);
                }
            }
        }
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
                    this.sugarModel.renderToBuffer(s, consumer, state.lightCoords, finalOverlay, finalTint))
            );
        }
        if (this.shouldRenderLayers(state) && !this.layers.isEmpty()) {
            this.model.setupAnim(state);
            for (net.minecraft.client.renderer.entity.layers.RenderLayer<CitadelLivingRenderState, AlexAdvancedEntityModel.CitadelEntityModelBridge<EntitySugarGlider, ModelSugarGlider>> layer : this.layers) {
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
