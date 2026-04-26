package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderRaccoon;
import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.LightLayer;

public class LayerRaccoonEyes extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityRaccoon>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/raccoon_eyes.png");

    public LayerRaccoonEyes(RenderRaccoon render) {
        super(render);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityRaccoon raccoon = AlexsMobsClientKeys.getLiving(state) instanceof EntityRaccoon r ? r : null;
        if (raccoon == null) {
            return;
        }
        long roundedTime = raccoon.level().getDefaultClockTime() % 24000;
        boolean night = roundedTime >= 13000 && roundedTime <= 22000;
        BlockPos ratPos = raccoon.getLightPosition();
        int i = raccoon.level().getBrightness(LightLayer.SKY, ratPos);
        int j = raccoon.level().getBrightness(LightLayer.BLOCK, ratPos);
        int brightness;
        if (night) {
            brightness = j;
        } else {
            brightness = Math.max(i, j);
        }
        if (brightness < 7 && !raccoon.isRigby()) {
            this.getParentModel().setupAnim(state);
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            collector.submitCustomGeometry(matrixStackIn, RenderTypes.eyes(TEXTURE), (pose, ivertexbuilder) ->
                this.getParentModel().renderCitadelToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, overlay, -1)
            );
        }
    }
}
