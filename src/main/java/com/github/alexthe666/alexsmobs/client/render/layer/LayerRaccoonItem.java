package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelRaccoon;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderRaccoon;
import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
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

public class LayerRaccoonItem extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityRaccoon>> {

    public LayerRaccoonItem(RenderRaccoon render) {
        super(render);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityRaccoon entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityRaccoon r ? r : null;
        if (entitylivingbaseIn == null) {
            return;
        }
        ItemStack itemstack = entitylivingbaseIn.getItemBySlot(EquipmentSlot.MAINHAND);
        matrixStackIn.pushPose();
        boolean inHand = entitylivingbaseIn.begProgress > 0 || entitylivingbaseIn.standProgress > 0 || entitylivingbaseIn.washProgress > 0;
        if (entitylivingbaseIn.isBaby()) {
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
        }
        matrixStackIn.pushPose();
        translateToHand(inHand, matrixStackIn);
        if (inHand) {
            matrixStackIn.translate(0.2F, 0.4F, 0F);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(90F * entitylivingbaseIn.washProgress * 0.2F));
        } else {
            matrixStackIn.translate(0, 0.1F, -0.35F);
        }
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-2.5F));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90F));
        ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
        renderer.renderItem(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, matrixStackIn, collector, packedLightIn);
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }

    protected void translateToHand(boolean inHand, PoseStack matrixStack) {
        ModelRaccoon model = (ModelRaccoon) this.getParentModel().citadel();
        if (inHand) {
            model.root.translateAndRotate(matrixStack);
            model.body.translateAndRotate(matrixStack);
            model.arm_right.translateAndRotate(matrixStack);
        } else {
            model.root.translateAndRotate(matrixStack);
            model.body.translateAndRotate(matrixStack);
            model.head.translateAndRotate(matrixStack);
        }
    }
}
