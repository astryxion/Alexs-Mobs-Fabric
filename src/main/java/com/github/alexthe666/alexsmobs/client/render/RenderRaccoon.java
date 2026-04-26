package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelRaccoon;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerRaccoonEyes;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerRaccoonItem;
import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;

public class RenderRaccoon extends MobRenderer<EntityRaccoon, LivingEntityRenderState, CitadelEntityModelBridge<EntityRaccoon>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/raccoon.png");
    private static final Identifier TEXTURE_RIGBY = Identifier.parse("alexsmobs:textures/entity/raccoon_rigby.png");
    private static final Identifier TEXTURE_BANDANA = Identifier.parse("alexsmobs:textures/entity/raccoon_bandana.png");

    public RenderRaccoon(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelRaccoon()), 0.4F);
        this.addLayer(new LayerRaccoonEyes(this));
        this.addLayer(new LayerRaccoonItem(this));
        this.addLayer(new BandanaLayer(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.scale(0.75F, 0.75F, 0.75F);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityRaccoon entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityRaccoon r ? r : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isRigby() ? TEXTURE_RIGBY : TEXTURE;
    }

    private static class BandanaLayer extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityRaccoon>> {
        public BandanaLayer(RenderRaccoon renderRaccoon) {
            super(renderRaccoon);
        }

        @Override
        public void submit(PoseStack p_225628_1_, SubmitNodeCollector collector, int p_225628_3_, LivingEntityRenderState state, float p_225628_9_, float p_225628_10_) {
            EntityRaccoon raccoon = AlexsMobsClientKeys.getLiving(state) instanceof EntityRaccoon r ? r : null;
            if (raccoon == null) {
                return;
            }
            float p_225628_7_ = state.ageInTicks;
            if (raccoon.getColor() != null && !raccoon.isInvisible()) {
                float lvt_11_2_;
                float lvt_12_2_;
                float lvt_13_2_;
                if (raccoon.hasCustomName() && "jeb_".equals(raccoon.getName().getContents())) {
                    int lvt_15_1_ = raccoon.tickCount / 25 + raccoon.getId();
                    int lvt_16_1_ = DyeColor.values().length;
                    int lvt_17_1_ = lvt_15_1_ % lvt_16_1_;
                    int lvt_18_1_ = (lvt_15_1_ + 1) % lvt_16_1_;
                    float lvt_19_1_ = ((float) (raccoon.tickCount % 25) + p_225628_7_) / 25.0F;
                    int color1 = DyeColor.byId(lvt_17_1_).getTextureDiffuseColor();
                    int color2 = DyeColor.byId(lvt_18_1_).getTextureDiffuseColor();
                    float r1 = (float) (color1 >> 16 & 255) / 255.0F;
                    float g1 = (float) (color1 >> 8 & 255) / 255.0F;
                    float b1 = (float) (color1 & 255) / 255.0F;
                    float r2 = (float) (color2 >> 16 & 255) / 255.0F;
                    float g2 = (float) (color2 >> 8 & 255) / 255.0F;
                    float b2 = (float) (color2 & 255) / 255.0F;
                    lvt_11_2_ = r1 * (1.0F - lvt_19_1_) + r2 * lvt_19_1_;
                    lvt_12_2_ = g1 * (1.0F - lvt_19_1_) + g2 * lvt_19_1_;
                    lvt_13_2_ = b1 * (1.0F - lvt_19_1_) + b2 * lvt_19_1_;
                } else {
                    int color = raccoon.getColor().getTextureDiffuseColor();
                    lvt_11_2_ = (float) (color >> 16 & 255) / 255.0F;
                    lvt_12_2_ = (float) (color >> 8 & 255) / 255.0F;
                    lvt_13_2_ = (float) (color & 255) / 255.0F;
                }
                this.getParentModel().setupAnim(state);
                int packedColor = AMColorUtil.packColor(lvt_11_2_, lvt_12_2_, lvt_13_2_, 1.0F);
                collector.submitCustomGeometry(p_225628_1_, AMRenderTypes.entityCutoutNoCull(TEXTURE_BANDANA), (pose, buffer) ->
                    this.getParentModel().renderCitadelToBuffer(p_225628_1_, buffer, p_225628_3_, OverlayTexture.NO_OVERLAY, packedColor)
                );
            }
        }
    }
}
