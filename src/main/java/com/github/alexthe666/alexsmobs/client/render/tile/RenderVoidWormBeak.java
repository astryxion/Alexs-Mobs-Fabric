package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockVoidWormBeak;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelVoidWormBeak;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityVoidWormBeak;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public class RenderVoidWormBeak<T extends TileEntityVoidWormBeak> implements BlockEntityRenderer<T, RenderVoidWormBeak.VoidWormBeakRenderState> {

    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/void_worm/void_worm_beak.png");
    private static final ModelVoidWormBeak HEAD_MODEL = new ModelVoidWormBeak();
    /** Citadel {@link ModelVoidWormBeak} reads {@link PoseStack#last()} at emit time; must use queued submit pose. */
    private final PoseStack citadelPoseScratch = new PoseStack();

    public static final class VoidWormBeakRenderState extends BlockEntityRenderState {
        public TileEntityVoidWormBeak tile;
        public float partialTick;
        public Direction dir;
    }

    public RenderVoidWormBeak(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public VoidWormBeakRenderState createRenderState() {
        return new VoidWormBeakRenderState();
    }

    @Override
    public void extractRenderState(T entity, VoidWormBeakRenderState renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(entity, renderState, partialTick, cameraPos, crumblingOverlay);
        renderState.tile = entity;
        renderState.partialTick = partialTick;
        renderState.dir = entity.getBlockState().getValue(BlockVoidWormBeak.FACING);
    }

    @Override
    public void submit(VoidWormBeakRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        TileEntityVoidWormBeak tileEntityIn = state.tile;
        float partialTicks = state.partialTick;
        Direction dir = state.dir;
        int light = state.lightCoords;
        int overlay = OverlayTexture.NO_OVERLAY;

        poseStack.pushPose();
        switch (dir) {
            case UP -> poseStack.translate(0.5F, 1.5F, 0.5F);
            case DOWN -> poseStack.translate(0.5F, -0.5F, 0.5F);
            case NORTH -> poseStack.translate(0.5, 0.5F, -0.5F);
            case EAST -> poseStack.translate(1.5F, 0.5F, 0.5F);
            case SOUTH -> poseStack.translate(0.5, 0.5F, 1.5F);
            case WEST -> poseStack.translate(-0.5F, 0.5F, 0.5F);
        }
        poseStack.mulPose(dir.getOpposite().getRotation());
        poseStack.pushPose();
        poseStack.translate(0, -0.01F, 0.0F);
        HEAD_MODEL.renderBeak(tileEntityIn, partialTicks);
        int finalLight = light;
        int finalOverlay = overlay;
        collector.submitCustomGeometry(poseStack, AMRenderTypes.entityCutoutNoCull(TEXTURE), (submitPose, consumer) ->
            AlexAdvancedEntityModel.withCitadelSubmitPose(submitPose, this.citadelPoseScratch, scratch ->
                HEAD_MODEL.renderToBuffer(scratch, consumer, finalLight, finalOverlay, -1)));
        poseStack.popPose();
        poseStack.popPose();
    }
}
