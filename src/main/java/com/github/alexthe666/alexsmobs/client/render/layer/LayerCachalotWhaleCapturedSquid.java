package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.ClientProxy;
import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelCachalotWhale;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderCachalotWhale;
import com.github.alexthe666.alexsmobs.entity.EntityCachalotWhale;
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

public class LayerCachalotWhaleCapturedSquid extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityCachalotWhale>> {

    public LayerCachalotWhaleCapturedSquid(RenderCachalotWhale render) {
        super(render);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityCachalotWhale whale = AlexsMobsClientKeys.getLiving(state) instanceof EntityCachalotWhale w ? w : null;
        if (whale == null) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        if (whale.hasCaughtSquid() && whale.isAlive()) {
            Entity squid = whale.getCaughtSquid();
            if (squid != null && squid.isAlive()) {
                boolean rightSquid = !whale.isHoldingSquidLeft();
                float riderRot = squid.yRotO + (squid.getYRot() - squid.yRotO) * partialTicks;
                EntityRenderer<?, ?> render = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(squid);
                EntityModel<?> modelBase = null;
                if (render instanceof LivingEntityRenderer<?, ?, ?> livingRenderer) {
                    modelBase = livingRenderer.getModel();
                }
                if (modelBase != null) {
                    ClientProxy.currentUnrenderedEntities.remove(squid.getUUID());
                    matrixStackIn.pushPose();
                    translateToPouch(matrixStackIn);
                    matrixStackIn.translate(rightSquid ? -1.2F : 1.2F, -0, -3.4F);
                    matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180F));
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(riderRot + (rightSquid ? -90F : 90F)));
                    ClientProxy.submitEntityInWorld(squid, 0, 0, 0, 0, partialTicks, matrixStackIn, collector);
                    matrixStackIn.popPose();
                    ClientProxy.currentUnrenderedEntities.add(squid.getUUID());
                }
            }
        }
    }

    protected void translateToPouch(PoseStack matrixStack) {
        ModelCachalotWhale m = (ModelCachalotWhale) ((CitadelEntityModelBridge<?>) getParentModel()).citadel();
        m.root.translateAndRotate(matrixStack);
        m.body.translateAndRotate(matrixStack);
        m.head.translateAndRotate(matrixStack);
        m.jaw.translateAndRotate(matrixStack);
    }
}
