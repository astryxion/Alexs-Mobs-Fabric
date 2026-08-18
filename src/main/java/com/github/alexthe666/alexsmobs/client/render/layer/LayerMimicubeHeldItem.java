package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelMimicube;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderMimicube;
import com.github.alexthe666.alexsmobs.entity.EntityMimicube;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

public class LayerMimicubeHeldItem extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityMimicube>> {

    public LayerMimicubeHeldItem(RenderMimicube render) {
        super(render);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityMimicube entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityMimicube m ? m : null;
        if (entitylivingbaseIn == null) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        ItemStack itemRight = entitylivingbaseIn.getMainHandItem();
        ItemStack itemLeft = entitylivingbaseIn.getOffhandItem();
        float rightSwap = Mth.lerp(partialTicks, entitylivingbaseIn.prevRightSwapProgress, entitylivingbaseIn.rightSwapProgress) * 0.2F;
        float leftSwap = Mth.lerp(partialTicks, entitylivingbaseIn.prevLeftSwapProgress, entitylivingbaseIn.leftSwapProgress) * 0.2F;
        float attackprogress = Mth.lerp(partialTicks, entitylivingbaseIn.prevAttackProgress, entitylivingbaseIn.attackProgress);
        double bob1 = Math.cos(state.ageInTicks * 0.1F) * 0.1F + 0.1F;
        double bob2 = Math.sin(state.ageInTicks * 0.1F) * 0.1F + 0.1F;
        ItemInHandRenderer itemRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
        if (!itemRight.isEmpty()) {
            matrixStackIn.pushPose();
            translateToHand(false, matrixStackIn);
            matrixStackIn.translate(-0.5F, 0.1F - bob1, -0.1F);
            matrixStackIn.scale(0.9F * (1F - rightSwap), 0.9F * (1F - rightSwap), 0.9F * (1F - rightSwap));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(180));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
            if (itemRight.getItem() instanceof ShieldItem) {
                matrixStackIn.translate(-0.1F, 0, -0.4F);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
            }
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-10));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(360 * rightSwap));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-40 * attackprogress));
            int lightRight = rightSwap > 0 ? (int) (-100 * rightSwap) : packedLightIn;
            itemRenderer.renderItem(entitylivingbaseIn, itemRight, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, matrixStackIn, collector, lightRight);
            matrixStackIn.popPose();
        }
        if (!itemLeft.isEmpty()) {
            matrixStackIn.pushPose();
            translateToHand(false, matrixStackIn);
            matrixStackIn.translate(0.45F, 0.1F - bob2, -0.1F);
            matrixStackIn.scale(0.9F * (1F - leftSwap), 0.9F * (1F - leftSwap), 0.9F * (1F - leftSwap));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(180));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
            if (itemLeft.getItem() instanceof ShieldItem) {
                matrixStackIn.translate(-0.2F, 0, -0.4F);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
            }
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(10));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(360 * leftSwap));
            int lightLeft = leftSwap > 0 ? (int) (-100 * leftSwap) : packedLightIn;
            itemRenderer.renderItem(entitylivingbaseIn, itemLeft, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, matrixStackIn, collector, lightLeft);
            matrixStackIn.popPose();
        }
    }

    protected void translateToHand(boolean left, PoseStack matrixStack) {
        ModelMimicube model = (ModelMimicube) ((CitadelEntityModelBridge<?>) this.getParentModel()).citadel();
        model.root.translateAndRotate(matrixStack);
        model.innerbody.translateAndRotate(matrixStack);
    }
}
