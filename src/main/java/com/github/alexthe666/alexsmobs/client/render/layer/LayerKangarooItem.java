package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelKangaroo;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderKangaroo;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
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

public class LayerKangarooItem extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityKangaroo>> {

    public LayerKangarooItem(RenderKangaroo render) {
        super(render);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityKangaroo entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityKangaroo k ? k : null;
        if (entitylivingbaseIn == null) {
            return;
        }
        ItemStack itemstack = entitylivingbaseIn.getItemBySlot(EquipmentSlot.MAINHAND);
        matrixStackIn.pushPose();
        boolean left = entitylivingbaseIn.isLeftHanded();
        if(entitylivingbaseIn.isBaby()){
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
        }
        matrixStackIn.pushPose();
        translateToHand(matrixStackIn, left);
        matrixStackIn.translate(0F, 0.75F, -0.125F);

        matrixStackIn.mulPose(Axis.XP.rotationDegrees(-110F));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180F));
        matrixStackIn.scale(0.8F, 0.8F, 0.8F);
        ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
        renderer.renderItem(entitylivingbaseIn, itemstack, left ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }

    protected void translateToHand(PoseStack matrixStack, boolean left) {
        ModelKangaroo model = (ModelKangaroo) ((CitadelEntityModelBridge<?>) this.getParentModel()).citadel();
        model.root.translateAndRotate(matrixStack);
        model.body.translateAndRotate(matrixStack);
        model.chest.translateAndRotate(matrixStack);
        if(left){
            model.arm_left.translateAndRotate(matrixStack);
        }else{
            model.arm_right.translateAndRotate(matrixStack);
        }
    }
}
