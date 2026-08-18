package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockTransmutationTable;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelTransmutationTable;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityTransmutationTable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderTransmutationTable<T extends TileEntityTransmutationTable> implements BlockEntityRenderer<T, RenderTransmutationTable.TransmutationTableRenderState> {

    /** Citadel draws from {@link PoseStack#last()}; must match {@link SubmitNodeCollector} queued pose, not the block BER stack after pop. */
    private final PoseStack citadelPoseScratch = new PoseStack();

    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/farseer/transmutation_table.png");
    private static final Identifier OVERLAY = Identifier.parse("alexsmobs:textures/entity/farseer/transmutation_table_overlay.png");
    private static final Identifier GLOW_TEXTURE = Identifier.parse("alexsmobs:textures/entity/farseer/transmutation_table_glow.png");
    private static final ModelTransmutationTable MODEL = new ModelTransmutationTable(0F);
    private static final ModelTransmutationTable OVERLAY_MODEL = new ModelTransmutationTable(0.01F);

    public static final class TransmutationTableRenderState extends BlockEntityRenderState {
        public TileEntityTransmutationTable tile;
        public float partialTick;
        public Direction dir;
    }

    public RenderTransmutationTable(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public TransmutationTableRenderState createRenderState() {
        return new TransmutationTableRenderState();
    }

    @Override
    public void extractRenderState(T entity, TransmutationTableRenderState renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(entity, renderState, partialTick, cameraPos, crumblingOverlay);
        renderState.tile = entity;
        renderState.partialTick = partialTick;
        renderState.dir = entity.getBlockState().getValue(BlockTransmutationTable.FACING);
    }

    @Override
    public void submit(TransmutationTableRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        TileEntityTransmutationTable tileEntityIn = state.tile;
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
        poseStack.pushPose();
        MODEL.animate(tileEntityIn, partialTicks);
        int finalLight = light;
        int combinedOverlayIn = overlay;
        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(TEXTURE), (pose, consumer) ->
            AlexAdvancedEntityModel.withCitadelSubmitPose(pose, this.citadelPoseScratch, s ->
                MODEL.renderToBuffer(s, consumer, finalLight, combinedOverlayIn, -1)));
        collector.submitCustomGeometry(poseStack, AMRenderTypes.getEyesAlphaEnabled(GLOW_TEXTURE), (pose, consumer) ->
            AlexAdvancedEntityModel.withCitadelSubmitPose(pose, this.citadelPoseScratch, s ->
                MODEL.renderToBuffer(s, consumer, 240, combinedOverlayIn, -1)));
        OVERLAY_MODEL.animate(tileEntityIn, partialTicks);
        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucentEmissive(OVERLAY), (pose, staticyOverlay) ->
            AlexAdvancedEntityModel.withCitadelSubmitPose(pose, this.citadelPoseScratch, s ->
                OVERLAY_MODEL.renderToBuffer(s, staticyOverlay, finalLight, OverlayTexture.NO_OVERLAY, -1)));
        poseStack.popPose();
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer p_114090_, Matrix4f p_114091_, Matrix3f p_114092_, int p_114093_, float p_114094_, float p_114095_, int p_114096_, int p_114097_) {
        p_114090_.addVertex(0.0F, 0.0F, 0.0F).setColor((int) (255 * 255), (int) (255 * 255), (int) (255 * 255), (int) (100 * 255)).setUv((float) p_114096_, (float) p_114097_).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(p_114093_ & 0xFFFF, p_114093_ >> 16).setNormal(0.0F, 1.0F, 0.0F);
    }
}
