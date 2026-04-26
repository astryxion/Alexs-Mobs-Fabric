package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderSoulVulture;
import com.github.alexthe666.alexsmobs.entity.EntitySoulVulture;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class LayerSoulVultureGlow extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntitySoulVulture>> {
    private static final Identifier TEXTURE_GLOW = Identifier.parse("alexsmobs:textures/entity/soul_vulture/soul_vulture_glow.png");
    private static final Identifier TEXTURE_0 = Identifier.parse("alexsmobs:textures/entity/soul_vulture/soul_vulture_flames_0.png");
    private static final Identifier TEXTURE_1 = Identifier.parse("alexsmobs:textures/entity/soul_vulture/soul_vulture_flames_1.png");
    private static final Identifier TEXTURE_2 = Identifier.parse("alexsmobs:textures/entity/soul_vulture/soul_vulture_flames_2.png");

    public LayerSoulVultureGlow(RenderSoulVulture renderSoulVulture) {
        super(renderSoulVulture);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntitySoulVulture entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntitySoulVulture v ? v : null;
        if (entitylivingbaseIn == null) {
            return;
        }
        this.getParentModel().setupAnim(state);
        int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
        collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.getGhost(TEXTURE_GLOW), (pose, consumer) ->
            this.getParentModel().renderCitadelToBuffer(matrixStackIn, consumer, 240, overlay, -1)
        );
        if (entitylivingbaseIn.hasSoulHeart()) {
            collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.getGhost(getFlames(entitylivingbaseIn.tickCount)), (pose, consumer) ->
                this.getParentModel().renderCitadelToBuffer(matrixStackIn, consumer, 240, overlay, -1)
            );
        }
    }

    private Identifier getFlames(int tickCount) {
        final int i = tickCount / 3 % 3;
        return switch (i) {
            case 2 -> TEXTURE_2;
            case 1 -> TEXTURE_1;
            default -> TEXTURE_0;
        };
    }
}
