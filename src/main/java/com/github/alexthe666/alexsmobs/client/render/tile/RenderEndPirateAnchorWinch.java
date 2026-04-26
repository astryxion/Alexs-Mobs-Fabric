package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateAnchorWinch;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateAnchorChain;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateAnchorWinch;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateAnchorWinch;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public class RenderEndPirateAnchorWinch<T extends TileEntityEndPirateAnchorWinch> implements BlockEntityRenderer<T, RenderEndPirateAnchorWinch.AnchorWinchRenderState> {

    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/end_pirate/anchor_winch.png");
    private static final Identifier TEXTURE_CHAIN = Identifier.parse("alexsmobs:textures/entity/end_pirate/anchor_chain.png");
    private static final ModelEndPirateAnchorWinch WINCH_MODEL = new ModelEndPirateAnchorWinch();
    private static final ModelEndPirateAnchorChain CHAIN_MODEL = new ModelEndPirateAnchorChain();

    public static final class AnchorWinchRenderState extends BlockEntityRenderState {
        public TileEntityEndPirateAnchorWinch tile;
        public float partialTick;
        public boolean east;
    }

    public RenderEndPirateAnchorWinch(Context rendererDispatcherIn) {
    }

    @Override
    public AnchorWinchRenderState createRenderState() {
        return new AnchorWinchRenderState();
    }

    @Override
    public void extractRenderState(T entity, AnchorWinchRenderState renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(entity, renderState, partialTick, cameraPos, crumblingOverlay);
        renderState.tile = entity;
        renderState.partialTick = partialTick;
        renderState.east = entity.getBlockState().getValue(BlockEndPirateAnchorWinch.EASTORWEST);
    }

    @Override
    public void submit(AnchorWinchRenderState state, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState cameraState) {
        TileEntityEndPirateAnchorWinch tileEntityIn = state.tile;
        float partialTicks = state.partialTick;
        boolean east = state.east;
        int combinedLightIn = state.lightCoords;
        int combinedOverlayIn = OverlayTexture.NO_OVERLAY;

        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5F, 1.5F, 0.5F);
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
        if (east) {
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
        }
        boolean flag = false;
        matrixStackIn.pushPose();
        if (!tileEntityIn.isAnchorEW()) {
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
        }
        float bottomOfChain = tileEntityIn.getChainLength(partialTicks);
        for (float i = 0; i < tileEntityIn.getChainLengthForRender(); i += 0.5F) {
            matrixStackIn.pushPose();
            float moveDown = Math.max(bottomOfChain - i, 0);
            matrixStackIn.translate(0, 0.1F + moveDown, 0);
            if (flag) {
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
            }
            if (moveDown <= 1F) {
                float modulatedScale = 0.5F + moveDown * 0.5F;
                matrixStackIn.translate(0, (1F - moveDown) * 0.5F, 0);
                matrixStackIn.scale(modulatedScale, modulatedScale, modulatedScale);
            }
            int light = combinedLightIn;
            int overlay = combinedOverlayIn;
            collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(TEXTURE_CHAIN), (pose, consumer) ->
                CHAIN_MODEL.renderToBuffer(matrixStackIn, consumer, light, overlay, -1));
            collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(TEXTURE_CHAIN), (pose, consumer) ->
                CHAIN_MODEL.renderToBuffer(matrixStackIn, null, 0, 0, -1));
            collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(TEXTURE), (pose, consumer) ->
                WINCH_MODEL.renderToBuffer(matrixStackIn, consumer, light, overlay, -1));
            collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(TEXTURE), (pose, consumer) ->
                WINCH_MODEL.renderToBuffer(matrixStackIn, null, 0, 0, -1));
            matrixStackIn.pushPose();
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
            if (tileEntityIn.isAnchorEW()) {
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
            }
            RenderEndPirateAnchor.ANCHOR_MODEL.resetToDefaultPose();
            collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(RenderEndPirateAnchor.TEXTURE_ANCHOR), (pose, consumer) ->
                RenderEndPirateAnchor.ANCHOR_MODEL.renderToBuffer(matrixStackIn, consumer, light, overlay, -1));
            collector.submitCustomGeometry(matrixStackIn, RenderTypes.eyes(RenderEndPirateAnchor.TEXTURE_ANCHOR_GLOW), (pose, consumer) ->
                RenderEndPirateAnchor.ANCHOR_MODEL.renderToBuffer(matrixStackIn, consumer, light, overlay, -1));

            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }
    }

    public int getViewDistance() {
        return 256;
    }
}
