package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelGrizzlyBear;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderGrizzlyBear;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
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

public class LayerGrizzlyItem extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityGrizzlyBear>> {

    public LayerGrizzlyItem(RenderGrizzlyBear renderGrizzlyBear) {
        super(renderGrizzlyBear);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityGrizzlyBear entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityGrizzlyBear b ? b : null;
        if (entitylivingbaseIn == null) {
            return;
        }
        ItemStack itemstack = entitylivingbaseIn.getItemBySlot(EquipmentSlot.MAINHAND);
        ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
        matrixStackIn.pushPose();
        if (entitylivingbaseIn.isBaby()) {
            matrixStackIn.scale(0.35F, 0.35F, 0.35F);
            matrixStackIn.translate(0.0D, 2.75D, 0.125D);
            translateToHand(false, matrixStackIn);
            matrixStackIn.translate(0.2F, 0.7F, -0.4F);
            matrixStackIn.scale(2.8F, 2.8F, 2.8F);
        } else {
            translateToHand(false, matrixStackIn);
            matrixStackIn.translate(0.2F, 0.7F, -0.4F);
        }
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(10F));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(100F));
        matrixStackIn.scale(1, 1, 1);
        renderer.renderItem(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.popPose();
    }

    protected void translateToHand(boolean left, PoseStack matrixStack) {
        ModelGrizzlyBear model = (ModelGrizzlyBear) this.getParentModel().citadel();
        model.root.translateAndRotate(matrixStack);
        model.midbody.translateAndRotate(matrixStack);
        model.body.translateAndRotate(matrixStack);
        model.right_arm.translateAndRotate(matrixStack);
    }
}
