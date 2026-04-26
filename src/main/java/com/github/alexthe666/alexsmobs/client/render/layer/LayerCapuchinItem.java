package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelAncientDart;
import com.github.alexthe666.alexsmobs.client.model.ModelCapuchinMonkey;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderCapuchinMonkey;
import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LayerCapuchinItem extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityCapuchinMonkey>> {

    public static final Identifier DART_TEXTURE = Identifier.parse("alexsmobs:textures/entity/ancient_dart.png");
    public static final ModelAncientDart DART_MODEL = new ModelAncientDart();

    public LayerCapuchinItem(RenderCapuchinMonkey render) {
        super(render);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityCapuchinMonkey entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityCapuchinMonkey m ? m : null;
        if (entitylivingbaseIn == null) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);

        if (entitylivingbaseIn.hasDart()) {
            matrixStackIn.pushPose();
            if (entitylivingbaseIn.isBaby()) {
                matrixStackIn.scale(0.35F, 0.35F, 0.35F);
                matrixStackIn.translate(0.5D, 2.6D, 0.15D);
                translateToHand(false, matrixStackIn);
                matrixStackIn.translate(-0.65, -0.75F, -0.1F);
                matrixStackIn.scale(2.8F, 2.8F, 2.8F);

            } else {
                translateToHand(false, matrixStackIn);
            }
            float f = 0.0F;
            if (entitylivingbaseIn.getAnimation() == EntityCapuchinMonkey.ANIMATION_THROW) {
                if (entitylivingbaseIn.getAnimationTick() < 6) {
                    f = Math.min(3, entitylivingbaseIn.getAnimationTick() + partialTicks) * 60;
                } else {
                    f = (12 - (entitylivingbaseIn.getAnimationTick() + partialTicks)) * 30;
                }
            }
            matrixStackIn.translate(0, 0.5F, 0F);
            matrixStackIn.scale(1.2F, 1.2F, 1.2F);
            matrixStackIn.pushPose();
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(f));
            bufferIn.submitCustomGeometry(matrixStackIn, RenderTypes.entityCutout(DART_TEXTURE), (pose, consumer) ->
                    DART_MODEL.renderToBuffer(matrixStackIn, consumer, packedLightIn, overlay, -1));
            matrixStackIn.popPose();
            matrixStackIn.popPose();

        } else if (entitylivingbaseIn.getAnimation() == EntityCapuchinMonkey.ANIMATION_THROW && entitylivingbaseIn.getAnimationTick() <= 5) {
            ItemStack itemstack = new ItemStack(Items.COBBLESTONE);
            matrixStackIn.pushPose();
            if (entitylivingbaseIn.isBaby()) {
                matrixStackIn.scale(0.35F, 0.35F, 0.35F);
                matrixStackIn.translate(0.5D, 2.6D, 0.15D);
                translateToHand(false, matrixStackIn);
                matrixStackIn.translate(-0.4F, 0.75F, -0.0F);
                matrixStackIn.scale(2.8F, 2.8F, 2.8F);
            } else {
                translateToHand(false, matrixStackIn);
                matrixStackIn.translate(0.125F, 0.5F, 0.1F);
            }
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-2.5F));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90F));
            ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            renderer.renderItem(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
        }
    }

    protected void translateToHand(boolean left, PoseStack matrixStack) {
        ModelCapuchinMonkey model = (ModelCapuchinMonkey) this.getParentModel().citadel();
        model.root.translateAndRotate(matrixStack);
        model.body.translateAndRotate(matrixStack);
        model.arm_right.translateAndRotate(matrixStack);
    }
}
