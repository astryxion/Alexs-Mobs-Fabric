package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.ClientProxy;
import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelKangaroo;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderKangaroo;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
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

public class LayerKangarooBaby extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityKangaroo>> {

    public LayerKangarooBaby(RenderKangaroo render) {
        super(render);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityKangaroo roo = AlexsMobsClientKeys.getLiving(state) instanceof EntityKangaroo k ? k : null;
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
                    matrixStackIn.translate(0, 1.12F, -0.3F);
                    ModelKangaroo.renderOnlyHead = true;
                    matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180F));
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(riderRot + 180F));
                    ClientProxy.submitEntityInWorld(passenger, 0, 0, 0, 0, partialTicks, matrixStackIn, collector);
                    ModelKangaroo.renderOnlyHead = false;
                    matrixStackIn.popPose();
                    ClientProxy.currentUnrenderedEntities.add(passenger.getUUID());
                }

            }
        }

    }

    protected void translateToPouch(PoseStack matrixStack) {
        ModelKangaroo m = (ModelKangaroo) ((CitadelEntityModelBridge<?>) getParentModel()).citadel();
        m.root.translateAndRotate(matrixStack);
        m.body.translateAndRotate(matrixStack);
    }
}
