package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelGorilla;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderGorilla;
import com.github.alexthe666.alexsmobs.entity.EntityGorilla;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LayerGorillaItem extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityGorilla>> {

    public LayerGorillaItem(RenderGorilla render) {
        super(render);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityGorilla entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityGorilla g ? g : null;
        if (entitylivingbaseIn == null) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        ItemStack itemstack = entitylivingbaseIn.getItemBySlot(EquipmentSlot.MAINHAND);
        String name = entitylivingbaseIn.getName().getString().toLowerCase();
        ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
        if (name.contains("harambe")) {
            ItemStack haloStack = new ItemStack(AMItemRegistry.HALO);
            matrixStackIn.pushPose();
            ModelGorilla model = (ModelGorilla) this.getParentModel().citadel();
            model.root.translateAndRotate(matrixStackIn);
            model.body.translateAndRotate(matrixStackIn);
            model.chest.translateAndRotate(matrixStackIn);
            model.head.translateAndRotate(matrixStackIn);
            float f = 0.1F * (float) Math.sin((entitylivingbaseIn.tickCount + partialTicks) * 0.1F) + (entitylivingbaseIn.isBaby() ? 0.2F : 0F);
            matrixStackIn.translate(0.0F, -0.7F - f, -0.2F);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(90F));
            matrixStackIn.scale(1.3F, 1.3F, 1.3F);
            renderer.renderItem(entitylivingbaseIn, haloStack, ItemDisplayContext.GROUND, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
        }
        matrixStackIn.pushPose();
        if (entitylivingbaseIn.isBaby()) {
            matrixStackIn.scale(0.35F, 0.35F, 0.35F);
            matrixStackIn.translate(-0.1D, 2D, -1.15D);
            translateToHand(false, matrixStackIn);
            matrixStackIn.translate(-0.4F, 0.75F, -0.0F);
            matrixStackIn.scale(2.8F, 2.8F, 2.8F);
        } else {
            translateToHand(false, matrixStackIn);
            matrixStackIn.translate(-0.4F, 0.75F, -0.0F);
        }
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-2.5F));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90F));
        if (itemstack.getItem() instanceof BlockItem) {
            matrixStackIn.scale(2, 2, 2);
        }
        renderer.renderItem(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.popPose();
    }

    protected void translateToHand(boolean left, PoseStack matrixStack) {
        ModelGorilla model = (ModelGorilla) this.getParentModel().citadel();
        model.root.translateAndRotate(matrixStack);
        model.body.translateAndRotate(matrixStack);
        model.chest.translateAndRotate(matrixStack);
        model.leftArm.translateAndRotate(matrixStack);
    }
}
