package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateAnchor;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateAnchor;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateAnchor;
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

public class RenderEndPirateAnchor<T extends TileEntityEndPirateAnchor> implements BlockEntityRenderer<T, RenderEndPirateAnchor.AnchorRenderState> {

    protected static final Identifier TEXTURE_ANCHOR = Identifier.parse("alexsmobs:textures/entity/end_pirate/anchor.png");
    protected static final Identifier TEXTURE_ANCHOR_GLOW = Identifier.parse("alexsmobs:textures/entity/end_pirate/anchor_glow.png");
    protected static final ModelEndPirateAnchor ANCHOR_MODEL = new ModelEndPirateAnchor();

    public RenderEndPirateAnchor(Context rendererDispatcherIn) {
    }

    public static final class AnchorRenderState extends BlockEntityRenderState {
        public TileEntityEndPirateAnchor tile;
        public boolean east;
        public float partialTick;
    }

    @Override
    public AnchorRenderState createRenderState() {
        return new AnchorRenderState();
    }

    @Override
    public void extractRenderState(T entity, AnchorRenderState renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(entity, renderState, partialTick, cameraPos, crumblingOverlay);
        renderState.tile = entity;
        renderState.east = entity.getBlockState().getValue(BlockEndPirateAnchor.EASTORWEST);
        renderState.partialTick = partialTick;
    }

    @Override
    public void submit(AnchorRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        TileEntityEndPirateAnchor tileEntityIn = state.tile;
        float partialTicks = state.partialTick;
        boolean east = state.east;
        int light = state.lightCoords;
        int overlay = OverlayTexture.NO_OVERLAY;

        poseStack.pushPose();
        poseStack.translate(0.5F, 1.5F, 0.5F);
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        if (east) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        }
        ANCHOR_MODEL.renderAnchor(tileEntityIn, partialTicks, east);
        int finalLight = light;
        collector.submitCustomGeometry(poseStack, AMRenderTypes.entityCutoutNoCull(TEXTURE_ANCHOR), (pose, consumer) ->
            ANCHOR_MODEL.renderToBuffer(poseStack, consumer, finalLight, overlay, -1));
        collector.submitCustomGeometry(poseStack, RenderTypes.eyes(TEXTURE_ANCHOR_GLOW), (pose, consumer) ->
            ANCHOR_MODEL.renderToBuffer(poseStack, consumer, finalLight, overlay, -1));

        poseStack.popPose();
        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
