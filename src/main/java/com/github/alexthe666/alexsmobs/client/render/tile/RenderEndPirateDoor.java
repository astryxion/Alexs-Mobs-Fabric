package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateDoor;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateDoor;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateDoor;
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
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.phys.Vec3;

public class RenderEndPirateDoor<T extends TileEntityEndPirateDoor> implements BlockEntityRenderer<T, RenderEndPirateDoor.EndPirateDoorRenderState> {

    private final PoseStack citadelPoseScratch = new PoseStack();

    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/end_pirate/door.png");
    private static final ModelEndPirateDoor DOOR_MODEL = new ModelEndPirateDoor();

    public static final class EndPirateDoorRenderState extends BlockEntityRenderState {
        public TileEntityEndPirateDoor tile;
        public float partialTick;
        public Direction dir;
        public boolean hingeLeft;
    }

    public RenderEndPirateDoor(Context rendererDispatcherIn) {
    }

    @Override
    public EndPirateDoorRenderState createRenderState() {
        return new EndPirateDoorRenderState();
    }

    @Override
    public void extractRenderState(T entity, EndPirateDoorRenderState renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(entity, renderState, partialTick, cameraPos, crumblingOverlay);
        renderState.tile = entity;
        renderState.partialTick = partialTick;
        renderState.dir = entity.getBlockState().getValue(BlockEndPirateDoor.HORIZONTAL_FACING);
        renderState.hingeLeft = entity.getBlockState().getValue(BlockEndPirateDoor.HINGE) == DoorHingeSide.LEFT;
    }

    @Override
    public void submit(EndPirateDoorRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        TileEntityEndPirateDoor tileEntityIn = state.tile;
        float partialTicks = state.partialTick;
        Direction dir = state.dir;
        int light = state.lightCoords;
        int overlay = OverlayTexture.NO_OVERLAY;

        poseStack.pushPose();
        switch (dir) {
            case NORTH -> poseStack.translate(0.5, 0.5F, -0.5F);
            case EAST -> poseStack.translate(1.5F, 0.5F, 0.5F);
            case SOUTH -> poseStack.translate(0.5, 0.5F, 1.5F);
            case WEST -> poseStack.translate(-0.5F, 0.5F, 0.5F);
        }
        poseStack.mulPose(dir.getOpposite().getRotation());
        poseStack.pushPose();
        poseStack.translate(0, 1, -1);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.scale(0.999F, 0.999F, 0.999F);
        DOOR_MODEL.renderDoor(tileEntityIn, partialTicks, state.hingeLeft);
        int finalLight = light;
        int finalOverlay = overlay;
        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(TEXTURE), (pose, consumer) ->
            AlexAdvancedEntityModel.withCitadelSubmitPose(pose, this.citadelPoseScratch, s ->
                DOOR_MODEL.renderToBuffer(s, consumer, finalLight, finalOverlay, -1)));
        poseStack.popPose();
        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        return 128;
    }
}
