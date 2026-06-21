package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelCockroach;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.model.layered.ModelSombrero;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LayerCockroachMaracas extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityCockroach>> {

    private ItemStack stack;
    private final ModelSombrero sombrero;
    private static final Identifier SOMBRERO_TEX = Identifier.parse("alexsmobs:textures/armor/sombrero.png");

    public LayerCockroachMaracas(RenderCockroach render, EntityRendererProvider.Context renderManagerIn) {
        super(render);
        this.sombrero = new ModelSombrero(renderManagerIn.bakeLayer(AMModelLayers.SOMBRERO));
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityCockroach entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityCockroach c ? c : null;
        if (entitylivingbaseIn == null || !entitylivingbaseIn.hasMaracas()) {
            return;
        }
        if (this.stack == null) {
            this.stack = new ItemStack(AMItemRegistry.MARACA);
        }
        this.getParentModel().setupAnim(state);
        int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
        ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
        matrixStackIn.pushPose();
        if (entitylivingbaseIn.isBaby()) {
            matrixStackIn.scale(0.65F, 0.65F, 0.65F);
            matrixStackIn.translate(0.0D, 0.815D, 0.125D);
        }
        renderMaraca(renderer, entitylivingbaseIn, matrixStackIn, bufferIn, packedLightIn, 0, -0.25F, -90F, 60F);
        renderMaraca(renderer, entitylivingbaseIn, matrixStackIn, bufferIn, packedLightIn, 1, 0.25F, 90F, -120F);
        renderMaraca(renderer, entitylivingbaseIn, matrixStackIn, bufferIn, packedLightIn, 2, -0.35F, -90F, 60F);
        renderMaraca(renderer, entitylivingbaseIn, matrixStackIn, bufferIn, packedLightIn, 3, 0.35F, 90F, -120F);
        if (!entitylivingbaseIn.isHeadless()) {
            matrixStackIn.pushPose();
            translateToHand(4, matrixStackIn);
            matrixStackIn.translate(0F, -0.4F, -0.01F);
            matrixStackIn.translate(0F, entitylivingbaseIn.danceProgress * 0.045F, entitylivingbaseIn.danceProgress * -0.09F);
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(60F * entitylivingbaseIn.danceProgress * 0.2F));
            bufferIn.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(SOMBRERO_TEX), (pose, consumer) -> {
                PoseStack modelStack = new PoseStack();
                modelStack.last().set(pose);
                // Sombrero cubes are modeled above the humanoid head pivot; drop them onto the cockroach head.
                modelStack.translate(0.0F, 11.0F / 16.0F, 0.0F);
                sombrero.head.getChild("sombrero").render(modelStack, consumer, packedLightIn, overlay, -1);
                sombrero.head.getChild("sombrero2").render(modelStack, consumer, packedLightIn, overlay, -1);
            });
            matrixStackIn.popPose();
        }
        matrixStackIn.popPose();
    }

    private void renderMaraca(
            ItemInHandRenderer renderer,
            EntityCockroach entity,
            PoseStack matrixStackIn,
            SubmitNodeCollector bufferIn,
            int packedLightIn,
            int hand,
            float xOffset,
            float xRot,
            float zRot
    ) {
        matrixStackIn.pushPose();
        translateToHand(hand, matrixStackIn);
        matrixStackIn.translate(xOffset, 0.0F, 0.0F);
        matrixStackIn.scale(1.4F, 1.4F, 1.4F);
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(xRot));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(zRot));
        PoseStack itemPose = new PoseStack();
        itemPose.last().set(matrixStackIn.last());
        renderer.renderItem(entity, stack, ItemDisplayContext.GROUND, itemPose, bufferIn, packedLightIn);
        matrixStackIn.popPose();
    }

    protected void translateToHand(int hand, PoseStack matrixStack) {
        ModelCockroach m = cockroachModel();
        m.root.translateAndRotate(matrixStack);
        m.abdomen.translateAndRotate(matrixStack);
        if (hand == 0) {
            m.right_leg_front.translateAndRotate(matrixStack);
        } else if (hand == 1) {
            m.left_leg_front.translateAndRotate(matrixStack);
        } else if (hand == 2) {
            m.right_leg_mid.translateAndRotate(matrixStack);
        } else if (hand == 3) {
            m.left_leg_mid.translateAndRotate(matrixStack);
        } else {
            m.neck.translateAndRotate(matrixStack);
            m.head.translateAndRotate(matrixStack);
        }
    }

    private ModelCockroach cockroachModel() {
        return (ModelCockroach) ((CitadelEntityModelBridge<?>) getParentModel()).citadel();
    }
}
