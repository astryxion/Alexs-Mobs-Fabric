package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelCosmaw;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerBasicGlow;
import com.github.alexthe666.alexsmobs.entity.EntityCosmaw;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RenderCosmaw extends MobRenderer<EntityCosmaw, LivingEntityRenderState, CitadelEntityModelBridge<EntityCosmaw>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/cosmaw.png");
    private static final Identifier TEXTURE_GLOW = Identifier.parse("alexsmobs:textures/entity/cosmaw_glow.png");

    public RenderCosmaw(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelCosmaw()), 0.9F);
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerBasicGlow<>(this, TEXTURE_GLOW));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.translate(0, -0.5F, 0);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }

    static class LayerHeldItem extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityCosmaw>> {

        LayerHeldItem(RenderCosmaw renderer) {
            super(renderer);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityCosmaw entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityCosmaw c ? c : null;
            if (entitylivingbaseIn == null) {
                return;
            }
            ItemStack itemstack = entitylivingbaseIn.getMainHandItem();
            matrixStackIn.pushPose();
            translateToHand(matrixStackIn);
            matrixStackIn.translate(-0.0, 0.1F, -1.35F);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-45F));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-180F));
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(135F));
            matrixStackIn.scale(2, 2, 2);
            ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            renderer.renderItem(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
        }

        protected void translateToHand(PoseStack matrixStack) {
            ModelCosmaw model = (ModelCosmaw) this.getParentModel().citadel();
            model.root.translateAndRotate(matrixStack);
            model.body.translateAndRotate(matrixStack);
            model.mouthArm1.translateAndRotate(matrixStack);
            model.mouthArm2.translateAndRotate(matrixStack);
        }
    }
}
