package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelMantisShrimp;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderMantisShrimp;
import com.github.alexthe666.alexsmobs.entity.EntityMantisShrimp;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LayerMantisShrimpItem extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityMantisShrimp>> {

    public LayerMantisShrimpItem(RenderMantisShrimp render) {
        super(render);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityMantisShrimp entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityMantisShrimp m ? m : null;
        if (entitylivingbaseIn == null) {
            return;
        }
        ItemStack itemstack = entitylivingbaseIn.getItemBySlot(EquipmentSlot.MAINHAND);
        matrixStackIn.pushPose();
        boolean left = entitylivingbaseIn.isLeftHanded();
        if (entitylivingbaseIn.isBaby()) {
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
        }
        matrixStackIn.pushPose();
        translateToHand(matrixStackIn, left);
        matrixStackIn.translate(left ? 0.075F : -0.075F, 0.45F, -0.125F);
        ItemModelResolver itemModelResolver = Minecraft.getInstance().getItemModelResolver();
        ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
        ClientLevel clientLevel = entitylivingbaseIn.level() instanceof ClientLevel cl ? cl : null;
        itemModelResolver.updateForTopItem(itemStackRenderState, itemstack, ItemDisplayContext.GROUND, clientLevel, entitylivingbaseIn, 0);
        if (itemStackRenderState.usesBlockLight()) {
            matrixStackIn.translate(0F, 0F, 0.05F);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(left ? -40F : 40F));
        }
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-2.5F));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180F));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180F));
        matrixStackIn.scale(1.2F, 1.2F, 1.2F);
        ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
        renderer.renderItem(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }

    protected void translateToHand(PoseStack matrixStack, boolean left) {
        ModelMantisShrimp model = (ModelMantisShrimp) this.getParentModel().citadel();
        model.root.translateAndRotate(matrixStack);
        model.body.translateAndRotate(matrixStack);
        model.head.translateAndRotate(matrixStack);
        if (left) {
            model.arm_left.translateAndRotate(matrixStack);
            model.fist_left.translateAndRotate(matrixStack);
        } else {
            model.arm_right.translateAndRotate(matrixStack);
            model.fist_right.translateAndRotate(matrixStack);
        }
    }
}
