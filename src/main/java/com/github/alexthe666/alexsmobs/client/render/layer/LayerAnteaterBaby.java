package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.ClientProxy;
import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelAnteater;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderAnteater;
import com.github.alexthe666.alexsmobs.entity.EntityAnteater;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;

public class LayerAnteaterBaby extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityAnteater>> {

    public LayerAnteaterBaby(RenderAnteater render) {
        super(render);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int packedLightIn, LivingEntityRenderState renderState, float netHeadYaw, float headPitch) {
        EntityAnteater roo = AlexsMobsClientKeys.getLiving(renderState) instanceof EntityAnteater a ? a : null;
        if (roo == null) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        if(roo.isVehicle() && !roo.isBaby()){
            for(Entity passenger : roo.getPassengers()){
                float riderRot = passenger.yRotO + (passenger.getYRot() - passenger.yRotO) * partialTicks;
                EntityRenderer<?, ?> render = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(passenger);
                EntityModel<?> modelBase = null;
                if (render instanceof LivingEntityRenderer<?, ?, ?> livingRenderer) {
                    modelBase = livingRenderer.getModel();
                }
                if(modelBase != null){
                    ClientProxy.currentUnrenderedEntities.remove(passenger.getUUID());
                    matrixStackIn.pushPose();
                    translateToPouch(matrixStackIn);
                    matrixStackIn.translate(0, -0.12F, 0.1F);
                    matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180F));
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(riderRot + 180F));
                    ClientProxy.submitEntityInWorld(passenger, 0, 0, 0, 0, partialTicks, matrixStackIn, bufferIn);
                    matrixStackIn.popPose();
                    ClientProxy.currentUnrenderedEntities.add(passenger.getUUID());
                }

            }
        }

    }

    protected void translateToPouch(PoseStack matrixStack) {
        ModelAnteater m = (ModelAnteater) ((CitadelEntityModelBridge<?>) getParentModel()).citadel();
        m.root.translateAndRotate(matrixStack);
        m.body.translateAndRotate(matrixStack);
    }
}
