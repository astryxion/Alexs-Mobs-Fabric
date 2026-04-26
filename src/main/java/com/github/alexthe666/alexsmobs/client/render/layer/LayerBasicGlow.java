package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;

public class LayerBasicGlow<E extends Mob> extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<E>> {
    private final Identifier texture;

    public LayerBasicGlow(MobRenderer<E, LivingEntityRenderState, CitadelEntityModelBridge<E>> renderer, Identifier texture) {
        super(renderer);
        this.texture = texture;
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        this.getParentModel().setupAnim(state);
        collector.submitCustomGeometry(matrixStackIn, RenderTypes.eyes(this.texture), (pose, consumer) ->
                this.getParentModel().renderCitadelToBuffer(matrixStackIn, consumer, packedLightIn, OverlayTexture.NO_OVERLAY, -1));
    }
}
