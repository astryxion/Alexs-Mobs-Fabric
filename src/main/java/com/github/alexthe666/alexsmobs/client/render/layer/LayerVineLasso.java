package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.render.RenderVineLasso;
import com.github.alexthe666.alexsmobs.entity.util.VineLassoUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class LayerVineLasso<S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {

    public LayerVineLasso(RenderLayerParent<S, M> parent) {
        super(parent);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, S renderState, float netHeadYaw, float headPitch) {
        LivingEntity lassoed = AlexsMobsClientKeys.getLiving(renderState);
        if (lassoed == null || !VineLassoUtil.hasLassoData(lassoed)) {
            return;
        }

        Entity lassoOwner = VineLassoUtil.getLassoedTo(lassoed);
        if (!(lassoOwner instanceof LivingEntity holder)) {
            return;
        }

        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        RenderVineLasso.renderVineFromEntityLayer(lassoed, partialTicks, matrixStackIn, collector, holder, holder.getMainArm() != HumanoidArm.LEFT, 0.0F);
    }
}