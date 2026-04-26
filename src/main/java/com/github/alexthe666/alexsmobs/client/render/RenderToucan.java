package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelToucan;
import com.github.alexthe666.alexsmobs.entity.EntityToucan;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RenderToucan extends MobRenderer<EntityToucan, LivingEntityRenderState, CitadelEntityModelBridge<EntityToucan>> {
    private static final Identifier TEXTURE_0 = Identifier.parse("alexsmobs:textures/entity/toucan/toucan_0.png");
    private static final Identifier TEXTURE_1 = Identifier.parse("alexsmobs:textures/entity/toucan/toucan_1.png");
    private static final Identifier TEXTURE_2 = Identifier.parse("alexsmobs:textures/entity/toucan/toucan_2.png");
    private static final Identifier TEXTURE_3 = Identifier.parse("alexsmobs:textures/entity/toucan/toucan_3.png");
    private static final Identifier TEXTURE_GOLDEN = Identifier.parse("alexsmobs:textures/entity/toucan/toucan_gold.png");
    private static final Identifier TEXTURE_SAM = Identifier.parse("alexsmobs:textures/entity/toucan/toucan_sam.png");

    public RenderToucan(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelToucan()), 0.2F);
        this.addLayer(new LayerGlint(this));
        this.addLayer(new LayerHeldItem(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.scale(0.9F, 0.9F, 0.9F);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityToucan entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityToucan t ? t : null;
        if (entity == null) {
            return TEXTURE_0;
        }
        if (entity.isSam()) {
            return TEXTURE_SAM;
        }
        if (entity.isGolden()) {
            return TEXTURE_GOLDEN;
        }
        return switch (entity.getVariant()) {
            case 3 -> TEXTURE_3;
            case 2 -> TEXTURE_2;
            case 1 -> TEXTURE_1;
            default -> TEXTURE_0;
        };
    }

    static class LayerGlint extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityToucan>> {

        public LayerGlint(RenderToucan render) {
            super(render);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityToucan entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityToucan t ? t : null;
            if (entitylivingbaseIn == null || !entitylivingbaseIn.isEnchanted()) {
                return;
            }
            this.getParentModel().setupAnim(state);
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            collector.submitCustomGeometry(matrixStackIn, RenderTypes.armorCutoutNoCull(TEXTURE_GOLDEN), (pose, baseVc) ->
                this.getParentModel().renderCitadelToBuffer(matrixStackIn, baseVc, packedLightIn, overlay, -1)
            );
            collector.submitCustomGeometry(matrixStackIn, RenderTypes.armorEntityGlint(), (pose, glintVc) ->
                this.getParentModel().renderCitadelToBuffer(matrixStackIn, glintVc, packedLightIn, overlay, -1)
            );
        }
    }

    static class LayerHeldItem extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityToucan>> {

        public LayerHeldItem(RenderToucan render) {
            super(render);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityToucan entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityToucan t ? t : null;
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
            matrixStackIn.translate(-0.07F, -0.1F, -0.25F);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-45F));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90F));
            ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            renderer.renderItem(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, matrixStackIn, collector, packedLightIn);
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }

        protected void translateToHand(PoseStack matrixStack) {
            ModelToucan model = (ModelToucan) this.getParentModel().citadel();
            model.root.translateAndRotate(matrixStack);
            model.body.translateAndRotate(matrixStack);
            model.head.translateAndRotate(matrixStack);
        }
    }
}
