package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelCrow;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderCrow;
import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LayerCrowItem extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityCrow>> {

    public LayerCrowItem(RenderCrow render) {
        super(render);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityCrow entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityCrow c ? c : null;
        if (entitylivingbaseIn == null) {
            return;
        }
        ItemStack itemstack = entitylivingbaseIn.getItemBySlot(EquipmentSlot.MAINHAND);
        matrixStackIn.pushPose();
        if (entitylivingbaseIn.isBaby()) {
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
        }
        matrixStackIn.pushPose();
        translateToHand(matrixStackIn);
        matrixStackIn.translate(0, -0.09F, -0.125F);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-2.5F));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90F));
        matrixStackIn.scale(0.75F, 0.75F, 0.75F);
        ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
        renderer.renderItem(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, matrixStackIn, collector, packedLightIn);
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }

    protected void translateToHand(PoseStack matrixStack) {
        ModelCrow m = (ModelCrow) ((CitadelEntityModelBridge<?>) getParentModel()).citadel();
        m.root.translateAndRotate(matrixStack);
        m.body.translateAndRotate(matrixStack);
        m.head.translateAndRotate(matrixStack);
    }
}
