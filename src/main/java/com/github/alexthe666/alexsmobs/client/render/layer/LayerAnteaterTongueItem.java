package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelAnteater;
import com.github.alexthe666.alexsmobs.client.model.ModelLeafcutterAnt;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderAnteater;
import com.github.alexthe666.alexsmobs.entity.EntityAnteater;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class LayerAnteaterTongueItem extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityAnteater>> {

    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/leafcutter_ant.png");
    private final ModelLeafcutterAnt ANT_MODEL = new ModelLeafcutterAnt();

    public LayerAnteaterTongueItem(RenderAnteater render) {
        super(render);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int packedLightIn, LivingEntityRenderState renderState, float netHeadYaw, float headPitch) {
        EntityAnteater anteater = AlexsMobsClientKeys.getLiving(renderState) instanceof EntityAnteater a ? a : null;
        if (anteater == null) {
            return;
        }
        ItemStack itemstack = anteater.getMainHandItem();
        if(!itemstack.isEmpty() || anteater.hasAntOnTongue()){
            float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
            float ageInTicks = renderState.ageInTicks;
            double tongueM = Math.min(Math.sin(ageInTicks * 0.15F), 0);
            float scaleItem = -0.2F * (float) tongueM * (anteater.prevTongueProgress + (anteater.tongueProgress - anteater.prevTongueProgress) * partialTicks * 0.2F);

            matrixStackIn.pushPose();
            if(anteater.isBaby()){
                matrixStackIn.scale(0.35F, 0.35F, 0.35F);
                matrixStackIn.translate(0.0D, 2.8D, 0D);
            }
            matrixStackIn.pushPose();
            translateToTongue(matrixStackIn);
            if(anteater.isBaby()){
                matrixStackIn.translate(0.0D, 0.2F, -0.22D);
            }
            matrixStackIn.translate(-0.0, 0.0F, -0.35F);
            matrixStackIn.scale(scaleItem, scaleItem, scaleItem);
            if(anteater.hasAntOnTongue()){
                matrixStackIn.pushPose();
                matrixStackIn.translate(0F, -1.35F, -0.01F);
                ((OrderedSubmitNodeCollector) bufferIn).submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(TEXTURE), (pose, ivertexbuilder) -> {
                    ANT_MODEL.animateAnteater(anteater, partialTicks);
                    PoseStack modelStack = new PoseStack();
                    modelStack.last().set(pose);
                    ANT_MODEL.renderToBuffer(modelStack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
                });
                matrixStackIn.popPose();
            }
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }
    }

    protected void translateToTongue(PoseStack matrixStack) {
        ModelAnteater m = modelAnteater();
        m.root.translateAndRotate(matrixStack);
        m.body.translateAndRotate(matrixStack);
        m.head.translateAndRotate(matrixStack);
        m.snout.translateAndRotate(matrixStack);
        m.tongue1.translateAndRotate(matrixStack);
        m.tongue2.translateAndRotate(matrixStack);
    }

    private ModelAnteater modelAnteater() {
        return (ModelAnteater) ((CitadelEntityModelBridge<?>) getParentModel()).citadel();
    }
}
