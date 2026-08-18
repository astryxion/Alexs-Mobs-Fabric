package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateFlag;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateFlag;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateFlag;
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
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public class RenderEndPirateFlag<T extends TileEntityEndPirateFlag> implements BlockEntityRenderer<T, RenderEndPirateFlag.EndPirateFlagRenderState> {

    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/end_pirate/flag.png");
    private static final ModelEndPirateFlag FLAG_MODEL = new ModelEndPirateFlag();

    public static final class EndPirateFlagRenderState extends BlockEntityRenderState {
        public TileEntityEndPirateFlag tile;
        public float partialTick;
        public Direction dir;
    }

    public RenderEndPirateFlag(Context rendererDispatcherIn) {
    }

    @Override
    public EndPirateFlagRenderState createRenderState() {
        return new EndPirateFlagRenderState();
    }

    @Override
    public void extractRenderState(T entity, EndPirateFlagRenderState renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(entity, renderState, partialTick, cameraPos, crumblingOverlay);
        renderState.tile = entity;
        renderState.partialTick = partialTick;
        renderState.dir = entity.getBlockState().getValue(BlockEndPirateFlag.FACING);
    }

    @Override
    public void submit(EndPirateFlagRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        TileEntityEndPirateFlag tileEntityIn = state.tile;
        float partialTicks = state.partialTick;
        Direction dir = state.dir;
        int light = state.lightCoords;
        int overlay = OverlayTexture.NO_OVERLAY;

        poseStack.pushPose();
        switch (dir) {
            case NORTH -> poseStack.translate(0.5, 1.5F, 0.5F);
            case EAST -> poseStack.translate(0.5F, 1.5F, 0.5F);
            case SOUTH -> poseStack.translate(0.5, 1.5F, 0.5F);
            case WEST -> poseStack.translate(0.5F, 1.5F, 0.5F);
        }
        poseStack.mulPose(dir.getOpposite().getRotation());
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.YN.rotationDegrees(dir.getAxis() == Direction.Axis.Y ? -90.0F : 90.0F));
        poseStack.pushPose();
        FLAG_MODEL.renderFlag(tileEntityIn, partialTicks);
        int finalLight = light;
        int finalOverlay = overlay;
        collector.submitCustomGeometry(poseStack, AMRenderTypes.entityCutoutNoCull(TEXTURE), (pose, consumer) ->
            FLAG_MODEL.renderToBuffer(poseStack, consumer, finalLight, finalOverlay, -1));
        poseStack.popPose();
        poseStack.popPose();
    }
}
