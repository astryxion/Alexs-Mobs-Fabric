package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelBunfungus;
import com.github.alexthe666.alexsmobs.entity.EntityBunfungus;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RenderBunfungus extends MobRenderer<EntityBunfungus, LivingEntityRenderState, CitadelEntityModelBridge<EntityBunfungus>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/bunfungus.png");
    private static final Identifier TEXTURE_SLEEPING = Identifier.parse("alexsmobs:textures/entity/bunfungus_sleeping.png");

    public RenderBunfungus(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelBunfungus()), 0.6F);
        this.addLayer(new LayerHeldItem(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        EntityBunfungus rabbit = AlexsMobsClientKeys.getLiving(state) instanceof EntityBunfungus b ? b : null;
        if (rabbit == null) {
            return;
        }
        float partialTickTime = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        float f = rabbit.prevTransformTime + (rabbit.transformsIn() - rabbit.prevTransformTime) * partialTickTime;
        float f1 = (EntityBunfungus.MAX_TRANSFORM_TIME - f) / (float) EntityBunfungus.MAX_TRANSFORM_TIME;
        float f2 = f1 * 0.7F + 0.3F;
        matrixStackIn.scale(f2, f2, f2);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityBunfungus entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityBunfungus b ? b : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isSleeping() ? TEXTURE_SLEEPING : TEXTURE;
    }

    static class LayerHeldItem extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityBunfungus>> {

        public LayerHeldItem(RenderBunfungus render) {
            super(render);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityBunfungus entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityBunfungus b ? b : null;
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
            matrixStackIn.translate(0.3F, 0.45F, -0.15F);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90F));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90F));
            matrixStackIn.scale(1.15F, 1.15F, 1.15F);
            ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            renderer.renderItem(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, matrixStackIn, collector, packedLightIn);
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }

        protected void translateToHand(PoseStack matrixStack) {
            ModelBunfungus model = (ModelBunfungus) this.getParentModel().citadel();
            model.root.translateAndRotate(matrixStack);
            model.body.translateAndRotate(matrixStack);
            model.right_arm.translateAndRotate(matrixStack);
        }
    }
}
