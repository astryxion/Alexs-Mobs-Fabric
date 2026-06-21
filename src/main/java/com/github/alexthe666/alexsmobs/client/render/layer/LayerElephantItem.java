package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelElephant;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderElephant;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LayerElephantItem extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityElephant>> {

    public LayerElephantItem(RenderElephant render) {
        super(render);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityElephant entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityElephant e ? e : null;
        if (entitylivingbaseIn == null) {
            return;
        }
        ItemStack itemstack = entitylivingbaseIn.getMainHandItem();
        matrixStackIn.pushPose();
        if (entitylivingbaseIn.isBaby()) {
            matrixStackIn.scale(0.35F, 0.35F, 0.35F);
            matrixStackIn.translate(0.0D, 2.8D, 0D);
        }
        matrixStackIn.pushPose();
        translateToHand(matrixStackIn);
        if (entitylivingbaseIn.isBaby()) {
            matrixStackIn.translate(0.0D, 0.2F, -0.22D);
        }
        matrixStackIn.translate(-0.0, 1.0F, 0.15F);
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(180F));
        matrixStackIn.scale(1.3F, 1.3F, 1.3F);
        ItemModelResolver itemModelResolver = Minecraft.getInstance().getItemModelResolver();
        ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
        ClientLevel clientLevel = entitylivingbaseIn.level() instanceof ClientLevel cl ? cl : null;
        itemModelResolver.updateForTopItem(itemStackRenderState, itemstack, ItemDisplayContext.GROUND, clientLevel, entitylivingbaseIn, 0);
        if (itemStackRenderState.usesBlockLight()) {
            matrixStackIn.translate(-0.05F, -0.1F, -0.15F);
            matrixStackIn.scale(2, 2, 2);
        }
        ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
        renderer.renderItem(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, matrixStackIn, collector, packedLightIn);
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }

    protected void translateToHand(PoseStack matrixStack) {
        ModelElephant model = (ModelElephant) this.getParentModel().citadel();
        model.root.translateAndRotate(matrixStack);
        model.body.translateAndRotate(matrixStack);
        model.head.translateAndRotate(matrixStack);
        model.trunk1.translateAndRotate(matrixStack);
        model.trunk2.translateAndRotate(matrixStack);
    }
}
