package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel.CitadelLivingRenderState;
import com.github.alexthe666.alexsmobs.client.model.ModelMungus;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.entity.EntityMungus;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class RenderMungus extends MobRenderer<EntityMungus, CitadelLivingRenderState, CitadelEntityModelBridge<EntityMungus>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/mungus.png");
    private static final Identifier BEAM_TEXTURE = Identifier.parse("alexsmobs:textures/entity/mungus_beam.png");
    private static final Identifier TEXTURE_BEAM_OVERLAY = Identifier.parse("alexsmobs:textures/entity/mungus_beam_overlay.png");
    private static final Identifier TEXTURE_SACK_OVERLAY = Identifier.parse("alexsmobs:textures/entity/mungus_sack.png");
    private static final Identifier TEXTURE_SHOES = Identifier.parse("alexsmobs:textures/entity/mungus_shoes.png");
    private static final RenderType beamType = AMRenderTypes.getEyesNoFog(BEAM_TEXTURE);

    private final ModelMungus mungusModel;
    private final BlockDisplayContext blockDisplayContext = BlockDisplayContext.create();

    public RenderMungus(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelMungus(0)), 0.5F);
        this.mungusModel = (ModelMungus) this.model.citadel();
        this.addLayer(new MungusSackLayer(this));
        this.addLayer(new MungusMushroomLayer(this));
    }

    @Override
    public CitadelLivingRenderState createRenderState() {
        return new CitadelLivingRenderState();
    }

    @Override
    public void extractRenderState(EntityMungus entity, CitadelLivingRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.citadelEntity = entity;
    }

    @Override
    protected boolean isShaking(CitadelLivingRenderState state) {
        EntityMungus mungus = state.citadelEntity instanceof EntityMungus m ? m : null;
        return mungus != null && mungus.isReverting();
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z, int r, int g, int b, float u, float v) {
        consumer.addVertex(matrix, x, y, z).setColor(r, g, b, 255).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);;
    }

    @Override
    protected void setupRotations(CitadelLivingRenderState state, PoseStack matrixStackIn, float rotationYaw, float scaleFactor) {
        EntityMungus entityLiving = state.citadelEntity instanceof EntityMungus m ? m : null;
        if (entityLiving == null) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        if (state.deathTime > 0) {
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F - rotationYaw));
            float f = ((float) state.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(f * -90));
        } else {
            super.setupRotations(state, matrixStackIn, rotationYaw, scaleFactor);
        }
    }

    @Override
    protected float getFlipDegrees() {
        return 0F;
    }

    @Override
    protected void scale(CitadelLivingRenderState state, PoseStack matrixStackIn) {
        EntityMungus entitylivingbaseIn = state.citadelEntity instanceof EntityMungus m ? m : null;
        if (entitylivingbaseIn == null) {
            return;
        }
        this.mungusModel.young = entitylivingbaseIn.isBaby();
        String s = ChatFormatting.stripFormatting(entitylivingbaseIn.getName().getString());
        if (s != null && s.toLowerCase().contains("drip")) {
            matrixStackIn.translate(0F, entitylivingbaseIn.isBaby() ? -0.075F : -0.15F, 0F);
        }
    }

    @Override
    public boolean shouldRender(EntityMungus livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else {
            if (livingEntityIn.getBeamTarget() != null) {
                BlockPos pos = livingEntityIn.getBeamTarget();
                if (pos != null) {
                    Vec3 vector3d = Vec3.atLowerCornerOf(pos);
                    Vec3 vector3dCorner = Vec3.atLowerCornerOf(pos).add(1, 1, 1);
                    Vec3 vector3d1 = this.getPosition(livingEntityIn, livingEntityIn.getEyeHeight(), 1.0F);
                    return camera.isVisible(new AABB(vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y, vector3d.z))
                            || camera.isVisible(new AABB(vector3d1.x, vector3d1.y, vector3d1.z, vector3dCorner.x, vector3dCorner.y, vector3dCorner.z));
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
    public Identifier getTextureLocation(CitadelLivingRenderState state) {
        return TEXTURE;
    }

    @Override
    public void submit(CitadelLivingRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        EntityMungus entityIn = state.citadelEntity instanceof EntityMungus m ? m : null;
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
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> this.mungusModel.renderToBuffer(scratch, consumer, state.lightCoords, finalOverlay, finalTint))
            );
        }
        if (this.shouldRenderLayers(state) && !this.layers.isEmpty()) {
            this.model.setupAnim(state);
            for (RenderLayer<CitadelLivingRenderState, CitadelEntityModelBridge<EntityMungus>> layer : this.layers) {
                layer.submit(poseStack, collector, state.lightCoords, state, state.yRot, state.xRot);
            }
        }
        poseStack.popPose();

        if (entityIn != null && entityIn.getBeamTarget() != null) {
            BlockPos target = entityIn.getBeamTarget();
            float f1 = (float) entityIn.level().getGameTime() + partialTicks;
            float f2 = -1.0F * (f1 * 0.15F % 1.0F);
            float f3 = 1.13F;
            if (entityIn.isBaby()) {
                f3 = 0.555F;
            }
            poseStack.pushPose();
            poseStack.translate(0.0D, f3, 0.0D);
            Vec3 vector3d = Vec3.upFromBottomCenterOf(target, 0.15F);
            Vec3 vector3d1 = this.getPosition(entityIn, f3, partialTicks);
            Vec3 vector3d2 = vector3d.subtract(vector3d1);
            float f4 = (float) (vector3d2.length());
            vector3d2 = vector3d2.normalize();
            float f5 = (float) Math.acos(vector3d2.y);
            float f6 = (float) Math.atan2(vector3d2.z, vector3d2.x);
            poseStack.mulPose(Axis.YP.rotationDegrees(((Mth.PI / 2F) - f6) * Mth.RAD_TO_DEG));
            poseStack.mulPose(Axis.XP.rotationDegrees(f5 * Mth.RAD_TO_DEG));
            float f7 = f1 * 0.05F * 1.5F;
            float f8 = 1F;
            int j = (int) (f8 * 255.0F);
            int k = (int) (f8 * 255.0F);
            int l = (int) (f8 * 255.0F);
            float f9 = 0.2F;
            float f10 = 0.282F;
            float f11 = Mth.cos(0 + 2.3561945F) * 0.8F;
            float f12 = Mth.sin(0 + 2.3561945F) * 0.8F;
            float f13 = Mth.cos(0 + Maths.QUARTER_PI) * 0.8F;
            float f14 = Mth.sin(0 + Maths.QUARTER_PI) * 0.8F;
            float f15 = Mth.cos(0 + 3.926991F) * 0.8F;
            float f16 = Mth.sin(0 + 3.926991F) * 0.8F;
            float f17 = Mth.cos(0 + 5.4977875F) * 0.8F;
            float f18 = Mth.sin(0 + 5.4977875F) * 0.8F;
            float f19 = Mth.cos(0 + Mth.PI) * 0.4F;
            float f20 = Mth.sin(0 + Mth.PI) * 0.4F;
            float f21 = Mth.cos(0 + 0.0F) * 0.4F;
            float f22 = Mth.sin(0 + 0.0F) * 0.4F;
            float f23 = Mth.cos(0 + (Mth.PI / 2F)) * 0.4F;
            float f24 = Mth.sin(0 + (Mth.PI / 2F)) * 0.4F;
            float f25 = Mth.cos(0 + (Mth.PI * 1.5F)) * 0.4F;
            float f26 = Mth.sin(0 + (Mth.PI * 1.5F)) * 0.4F;
            float f29 = -1.0F + f2;
            float f30 = f4 * 0.5F + f29;
            PoseStack.Pose matrixstack$entry = poseStack.last();
            Matrix4f matrix4f = matrixstack$entry.pose();
            collector.submitCustomGeometry(poseStack, beamType, (pose, ivertexbuilder) -> {
                vertex(ivertexbuilder, matrix4f, f19, f4, f20, j, k, l, 0.4999F, f30);
                vertex(ivertexbuilder, matrix4f, f19, 0.0F, f20, j, k, l, 0.4999F, f29);
                vertex(ivertexbuilder, matrix4f, f21, 0.0F, f22, j, k, l, 0.0F, f29);
                vertex(ivertexbuilder, matrix4f, f21, f4, f22, j, k, l, 0.0F, f30);
                vertex(ivertexbuilder, matrix4f, f23, f4, f24, j, k, l, 0.4999F, f30);
                vertex(ivertexbuilder, matrix4f, f23, 0.0F, f24, j, k, l, 0.4999F, f29);
                vertex(ivertexbuilder, matrix4f, f25, 0.0F, f26, j, k, l, 0.0F, f29);
                vertex(ivertexbuilder, matrix4f, f25, f4, f26, j, k, l, 0.0F, f30);
                float f31 = 0.0F;
                if (entityIn.tickCount % 4 > 1) {
                    f31 = 0.5F;
                }

                vertex(ivertexbuilder, matrix4f, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F);
                vertex(ivertexbuilder, matrix4f, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F);
                vertex(ivertexbuilder, matrix4f, f17, f4, f18, j, k, l, 1.0F, f31);
                vertex(ivertexbuilder, matrix4f, f15, f4, f16, j, k, l, 0.5F, f31);
            });
            poseStack.popPose();
        }

        if (state.leashStates != null) {
            for (EntityRenderState.LeashState leashState : state.leashStates) {
                collector.submitLeash(poseStack, leashState);
            }
        }
        this.submitNameDisplay(state, poseStack, collector, cameraState);
    }

    private final class MungusSackLayer extends RenderLayer<CitadelLivingRenderState, CitadelEntityModelBridge<EntityMungus>> {

        public MungusSackLayer(RenderMungus p_i50928_1_) {
            super(p_i50928_1_);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, CitadelLivingRenderState state, float netHeadYaw, float headPitch) {
            EntityMungus entitylivingbaseIn = state.citadelEntity instanceof EntityMungus m ? m : null;
            if (entitylivingbaseIn == null) {
                return;
            }
            this.getParentModel().setupAnim(state);
            PoseStack citadelPoseStack = new PoseStack();
            collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.getEyesFlickering(TEXTURE_SACK_OVERLAY, 0), (pose, lead) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> RenderMungus.this.mungusModel.renderToBuffer(scratch, lead, 240, OverlayTexture.NO_OVERLAY, -1))
            );
            if (entitylivingbaseIn.getBeamTarget() != null) {
                collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.getGhost(TEXTURE_BEAM_OVERLAY), (pose, beam) ->
                    AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> RenderMungus.this.mungusModel.renderToBuffer(scratch, beam, 240, LivingEntityRenderer.getOverlayCoords(state, 0.0F), -1))
                );
            }
            String s = ChatFormatting.stripFormatting(entitylivingbaseIn.getName().getString());
            if (s != null && s.toLowerCase().contains("drip")) {
                collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(TEXTURE_SHOES), (pose, shoeBuffer) -> {
                    matrixStackIn.pushPose();
                    RenderMungus.this.mungusModel.renderShoes();
                    AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> RenderMungus.this.mungusModel.renderToBuffer(scratch, shoeBuffer, packedLightIn, OverlayTexture.NO_OVERLAY, -1));
                    RenderMungus.this.mungusModel.postRenderShoes();
                    matrixStackIn.popPose();
                });
            }
        }
    }

    private final class MungusMushroomLayer extends RenderLayer<CitadelLivingRenderState, CitadelEntityModelBridge<EntityMungus>> {

        public MungusMushroomLayer(RenderMungus p_i50928_1_) {
            super(p_i50928_1_);
        }

        private void renderSingleMushroomBlock(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, int overlay, BlockState blockstate) {
            BlockModelRenderState blockModelRenderState = new BlockModelRenderState();
            new net.minecraft.client.renderer.block.BlockModelResolver(Minecraft.getInstance().getModelManager())
                    .update(blockModelRenderState, blockstate, RenderMungus.this.blockDisplayContext);
            blockModelRenderState.submit(matrixStackIn, collector, packedLightIn, overlay, 0);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, CitadelLivingRenderState state, float netHeadYaw, float headPitch) {
            EntityMungus entitylivingbaseIn = state.citadelEntity instanceof EntityMungus m ? m : null;
            if (entitylivingbaseIn == null) {
                return;
            }
            BlockState blockstate = entitylivingbaseIn.getMushroomState();
            if (blockstate == null) {
                return;
            }
            int i = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            boolean altOrder = entitylivingbaseIn.isAltOrderMushroom();
            int mushroomCount = entitylivingbaseIn.getMushroomCount();
            matrixStackIn.pushPose();
            if (entitylivingbaseIn.isBaby()) {
                matrixStackIn.scale(0.5F, 0.5F, 0.5F);
                matrixStackIn.translate(0.0D, 1.5D, 0D);
            }
            matrixStackIn.pushPose();
            translateToBody(matrixStackIn);
            if (mushroomCount == 1 && !altOrder || mushroomCount >= 2) {
                matrixStackIn.pushPose();
                matrixStackIn.translate(0.2F, -1.4F, 0.15D);
                matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
                matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
                this.renderSingleMushroomBlock(matrixStackIn, collector, packedLightIn, i, blockstate);
                matrixStackIn.popPose();
            }
            if (mushroomCount == 1 && altOrder || mushroomCount >= 2) {
                matrixStackIn.pushPose();
                matrixStackIn.translate(-0.2F, -1.5F, -0.2D);
                matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
                matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
                this.renderSingleMushroomBlock(matrixStackIn, collector, packedLightIn, i, blockstate);
                matrixStackIn.popPose();
            }
            if (mushroomCount >= 3) {
                matrixStackIn.pushPose();
                matrixStackIn.translate(0.76F, -0.4F, 0.1D);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(90F));
                matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
                matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
                this.renderSingleMushroomBlock(matrixStackIn, collector, packedLightIn, i, blockstate);
                matrixStackIn.popPose();
            }
            if (mushroomCount >= 4) {
                matrixStackIn.pushPose();
                matrixStackIn.translate(-0.76F, -1.0F, 0.1D);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-60F));
                matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
                matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
                this.renderSingleMushroomBlock(matrixStackIn, collector, packedLightIn, i, blockstate);
                matrixStackIn.popPose();
            }
            if (mushroomCount >= 5) {
                matrixStackIn.pushPose();
                matrixStackIn.translate(-0.76F, -0.1F, 0.1D);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-100F));
                matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
                matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
                this.renderSingleMushroomBlock(matrixStackIn, collector, packedLightIn, i, blockstate);
                matrixStackIn.popPose();
            }
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }

        protected void translateToBody(PoseStack matrixStack) {
            ModelMungus model = (ModelMungus) this.getParentModel().citadel();
            model.root.translateAndRotate(matrixStack);
            model.body.translateAndRotate(matrixStack);
        }
    }

}
