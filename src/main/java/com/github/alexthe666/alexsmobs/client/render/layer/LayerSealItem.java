package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelSeal;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderSeal;
import com.github.alexthe666.alexsmobs.entity.EntitySeal;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LayerSealItem extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntitySeal>> {

    public LayerSealItem(RenderSeal render) {
        super(render);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntitySeal entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntitySeal s ? s : null;
        if (entitylivingbaseIn == null) {
            return;
        }
        ItemStack itemstack = entitylivingbaseIn.getMainHandItem();
        matrixStackIn.pushPose();
        if (entitylivingbaseIn.isBaby()) {
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
        }
        matrixStackIn.pushPose();
        translateToHand(matrixStackIn);
        if (entitylivingbaseIn.isBaby()) {
            matrixStackIn.translate(0.0D, 0.1F, -0.6D);
        }
        matrixStackIn.translate(-0.1F, 0.15F, -0.6F);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-45F));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90F));
        ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
        renderer.renderItem(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, matrixStackIn, collector, packedLightIn);
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }

    protected void translateToHand(PoseStack matrixStack) {
        ModelSeal model = (ModelSeal) this.getParentModel().citadel();
        model.root.translateAndRotate(matrixStack);
        model.body.translateAndRotate(matrixStack);
        model.head.translateAndRotate(matrixStack);
    }
}
