package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateAnchorWinch;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateAnchorChain;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateAnchorWinch;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateAnchorWinch;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class RenderEndPirateAnchorWinch<T extends TileEntityEndPirateAnchorWinch> implements BlockEntityRenderer<T> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("alexsmobs", "textures/entity/end_pirate/anchor_winch.png");
    private static final ResourceLocation TEXTURE_CHAIN = ResourceLocation.fromNamespaceAndPath("alexsmobs", "textures/entity/end_pirate/anchor_chain.png");
    private static final ModelEndPirateAnchorWinch WINCH_MODEL = new ModelEndPirateAnchorWinch();
    private static final ModelEndPirateAnchorChain CHAIN_MODEL = new ModelEndPirateAnchorChain();

    public RenderEndPirateAnchorWinch(Context rendererDispatcherIn) {
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        try {
            boolean east = tileEntityIn.getBlockState().getValue(BlockEndPirateAnchorWinch.EASTORWEST);
            float bottomOfChain = tileEntityIn.getChainLength(partialTicks);
            matrixStackIn.translate(0.5F, 1.5F, 0.5F);
            matrixStackIn.pushPose();
            try {
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
                if (east) {
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
                }
                boolean flag = false;
                matrixStackIn.pushPose();
                try {
                    if (!tileEntityIn.isAnchorEW()) {
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
                    }
                    for (float i = 0; i < tileEntityIn.getChainLengthForRender(); i += 0.5F) {
                        matrixStackIn.pushPose();
                        try {
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
                            CHAIN_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutout(TEXTURE_CHAIN)), combinedLightIn, combinedOverlayIn, 0xFFFFFFFF);
                            CHAIN_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.eyes(TEXTURE_CHAIN)), combinedLightIn, combinedOverlayIn, 0xFFFFFFFF);
                        } finally {
                            matrixStackIn.popPose();
                        }
                        flag = !flag;
                    }
                } finally {
                    matrixStackIn.popPose();
                }
                WINCH_MODEL.renderAnchor(tileEntityIn, partialTicks, east);
                WINCH_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutout(TEXTURE)), combinedLightIn, combinedOverlayIn, 0xFFFFFFFF);
                WINCH_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.eyes(TEXTURE)), combinedLightIn, combinedOverlayIn, 0xFFFFFFFF);
            } finally {
                matrixStackIn.popPose();
            }

            if (tileEntityIn.hasAnchor()) {
                matrixStackIn.pushPose();
                try {
                    matrixStackIn.translate(0.5F, -1.5F - bottomOfChain, 0.5F);
                    matrixStackIn.pushPose();
                    try {
                        matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
                        if (tileEntityIn.isAnchorEW()) {
                            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
                        }
                        RenderEndPirateAnchor.ANCHOR_MODEL.resetToDefaultPose();
                        RenderEndPirateAnchor.ANCHOR_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutout(RenderEndPirateAnchor.TEXTURE_ANCHOR)), combinedLightIn, combinedOverlayIn, 0xFFFFFFFF);
                        RenderEndPirateAnchor.ANCHOR_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.eyes(RenderEndPirateAnchor.TEXTURE_ANCHOR_GLOW)), combinedLightIn, combinedOverlayIn, 0xFFFFFFFF);
                    } finally {
                        matrixStackIn.popPose();
                    }
                } finally {
                    matrixStackIn.popPose();
                }
            }
        } finally {
            matrixStackIn.popPose();
        }
    }

    public boolean shouldRenderOffScreen(T entity) {
        return true;
    }

    public int getViewDistance() {
        return 256;
    }
}
