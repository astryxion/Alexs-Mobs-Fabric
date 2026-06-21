package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateShipWheel;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateShipWheel;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateShipWheel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public class RenderEndPirateShipWheel<T extends TileEntityEndPirateShipWheel> implements BlockEntityRenderer<T, RenderEndPirateShipWheel.EndPirateShipWheelRenderState> {

    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/end_pirate/ship_wheel.png");
    private static final Identifier TEXTURE_GLOW = Identifier.parse("alexsmobs:textures/entity/end_pirate/ship_wheel_glow.png");
    private static final ModelEndPirateShipWheel WHEEL_MODEL = new ModelEndPirateShipWheel();

    public static final class EndPirateShipWheelRenderState extends BlockEntityRenderState {
        public TileEntityEndPirateShipWheel tile;
        public float partialTick;
        public Direction dir;
    }

    public RenderEndPirateShipWheel(Context rendererDispatcherIn) {
    }

    @Override
    public EndPirateShipWheelRenderState createRenderState() {
        return new EndPirateShipWheelRenderState();
    }

    @Override
    public void extractRenderState(T entity, EndPirateShipWheelRenderState renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(entity, renderState, partialTick, cameraPos, crumblingOverlay);
        renderState.tile = entity;
        renderState.partialTick = partialTick;
        renderState.dir = entity.getBlockState().getValue(BlockEndPirateShipWheel.FACING);
    }

    @Override
    public void submit(EndPirateShipWheelRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        TileEntityEndPirateShipWheel tileEntityIn = state.tile;
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
        WHEEL_MODEL.renderWheel(tileEntityIn, partialTicks);
        int finalLight = light;
        int finalOverlay = overlay;
        collector.submitCustomGeometry(poseStack, AMRenderTypes.entityCutoutNoCull(TEXTURE), (pose, consumer) ->
            WHEEL_MODEL.renderToBuffer(poseStack, consumer, finalLight, finalOverlay, -1));
        collector.submitCustomGeometry(poseStack, AMRenderTypes.entityCutoutNoCull(TEXTURE_GLOW), (pose, consumer) ->
            WHEEL_MODEL.renderToBuffer(poseStack, consumer, 240, finalOverlay, -1));
        poseStack.popPose();
        poseStack.popPose();
    }
}
