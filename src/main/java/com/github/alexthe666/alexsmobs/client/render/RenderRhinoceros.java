package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelRhinoceros;
import com.github.alexthe666.alexsmobs.entity.EntityRhinoceros;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class RenderRhinoceros extends MobRenderer<EntityRhinoceros, LivingEntityRenderState, CitadelEntityModelBridge<EntityRhinoceros>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/rhinoceros.png");
    private static final Identifier TEXTURE_ANGRY = Identifier.parse("alexsmobs:textures/entity/rhinoceros_angry.png");
    private static final Identifier TEXTURE_POTION = Identifier.parse("alexsmobs:textures/entity/rhinoceros_potion.png");

    public RenderRhinoceros(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelRhinoceros()), 0.9F);
        this.addLayer(new PotionLayer(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.scale(1.1F, 1.1F, 1.1F);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityRhinoceros entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityRhinoceros r ? r : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isAngry() ? TEXTURE_ANGRY : TEXTURE;
    }

    private static class PotionLayer extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityRhinoceros>> {
        public PotionLayer(RenderRhinoceros parent) {
            super(parent);
        }

        @Override
        public void submit(PoseStack p_225628_1_, SubmitNodeCollector collector, int p_225628_3_, LivingEntityRenderState state, float p_225628_9_, float p_225628_10_) {
            EntityRhinoceros rhino = AlexsMobsClientKeys.getLiving(state) instanceof EntityRhinoceros r ? r : null;
            if (rhino == null) {
                return;
            }
            int color = rhino.getPotionColor();
            if (color != -1 && !rhino.isInvisible()) {
                float r = (float) (color >> 16 & 255) / 255.0F;
                float g = (float) (color >> 8 & 255) / 255.0F;
                float b = (float) (color & 255) / 255.0F;
                this.getParentModel().setupAnim(state);
                int packedColor = AMColorUtil.packColor(r, g, b, 1.0F);
                collector.submitCustomGeometry(p_225628_1_, AMRenderTypes.entityCutoutNoCull(TEXTURE_POTION), (pose, vc) ->
                    this.getParentModel().renderCitadelToBuffer(p_225628_1_, vc, p_225628_3_, OverlayTexture.NO_OVERLAY, packedColor)
                );
            }
        }
    }
}
