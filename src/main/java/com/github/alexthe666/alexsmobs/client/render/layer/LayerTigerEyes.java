package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderTiger;
import com.github.alexthe666.alexsmobs.entity.EntityTiger;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.LightLayer;

public class LayerTigerEyes extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityTiger>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/tiger/tiger_eyes.png");
    private static final Identifier TEXTURE_WHITE = Identifier.parse("alexsmobs:textures/entity/tiger/tiger_white_eyes.png");
    private static final Identifier TEXTURE_ANGRY = Identifier.parse("alexsmobs:textures/entity/tiger/tiger_angry_eyes.png");

    public LayerTigerEyes(RenderTiger render) {
        super(render);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityTiger tiger = AlexsMobsClientKeys.getLiving(state) instanceof EntityTiger t ? t : null;
        if (tiger == null || tiger.isSleeping()) {
            return;
        }
        long roundedTime = tiger.level().getDefaultClockTime() % 24000;
        boolean night = roundedTime >= 13000 && roundedTime <= 22000;
        BlockPos ratPos = tiger.getLightPosition();
        int i = tiger.level().getBrightness(LightLayer.SKY, ratPos);
        int j = tiger.level().getBrightness(LightLayer.BLOCK, ratPos);
        int brightness;
        if (night) {
            brightness = j;
        } else {
            brightness = Math.max(i, j);
        }
        if (brightness < 7 || tiger.getRemainingPersistentAngerTime() > 0) {
            Identifier texture = tiger.getRemainingPersistentAngerTime() > 0 ? TEXTURE_ANGRY : tiger.isWhite() ? TEXTURE_WHITE : TEXTURE;
            this.getParentModel().setupAnim(state);
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            collector.submitCustomGeometry(matrixStackIn, RenderTypes.eyes(texture), (pose, consumer) ->
                this.getParentModel().renderCitadelToBuffer(matrixStackIn, consumer, packedLightIn, overlay, -1)
            );
        }
    }
}
