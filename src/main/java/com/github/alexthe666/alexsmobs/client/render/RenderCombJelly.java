package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelCombJelly;
import com.github.alexthe666.alexsmobs.entity.EntityCombJelly;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public class RenderCombJelly extends MobRenderer<EntityCombJelly, LivingEntityRenderState, CitadelEntityModelBridge<EntityCombJelly>> {
    private static final Identifier TEXTURE_0 = Identifier.parse("alexsmobs:textures/entity/comb_jelly_blue.png");
    private static final Identifier TEXTURE_1 = Identifier.parse("alexsmobs:textures/entity/comb_jelly_green.png");
    private static final Identifier TEXTURE_2 = Identifier.parse("alexsmobs:textures/entity/comb_jelly_red.png");
    private static final Identifier TEXTURE_OVERLAY = Identifier.parse("alexsmobs:textures/entity/comb_jelly_overlay.png");
    private static final ModelCombJelly STRIPES_MODEL = new ModelCombJelly(0.05F);

    public RenderCombJelly(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelCombJelly(0.0F)), 0.3F);
        this.addLayer(new RainbowLayer(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        EntityCombJelly jelly = AlexsMobsClientKeys.getLiving(state) instanceof EntityCombJelly j ? j : null;
        if (jelly != null) {
            matrixStackIn.scale(jelly.getJellyScale(), jelly.getJellyScale(), jelly.getJellyScale());
        }
    }

    @Override
    protected float getFlipDegrees() {
        return 0.0F;
    }

    @Nullable
    @Override
    protected RenderType getRenderType(LivingEntityRenderState state, boolean normal, boolean invis, boolean outline) {
        Identifier tex = this.getTextureLocation(state);
        if (invis) {
            return RenderTypes.entityTranslucentCullItemTarget(tex);
        } else if (normal) {
            return RenderTypes.entityTranslucent(tex);
        } else {
            return outline ? RenderTypes.outline(tex) : null;
        }
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityCombJelly entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityCombJelly j ? j : null;
        if (entity == null) {
            return TEXTURE_0;
        }
        return entity.getVariant() == 0 ? TEXTURE_0 : entity.getVariant() == 1 ? TEXTURE_1 : TEXTURE_2;
    }

    static class RainbowLayer extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityCombJelly>> {

        public RainbowLayer(RenderCombJelly render) {
            super(render);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityCombJelly entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityCombJelly j ? j : null;
            if (entitylivingbaseIn == null) {
                return;
            }
            float limbSwing = state.walkAnimationPos;
            float limbSwingAmount = Math.min(1.0F, state.walkAnimationSpeed);
            float ageInTicks = state.ageInTicks;
            float wrappedHead = state.yRot;
            STRIPES_MODEL.setupAnim(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, wrappedHead, state.xRot);
            collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.COMBJELLY_RAINBOW_GLINT, (pose, rainbow) ->
                STRIPES_MODEL.renderToBuffer(matrixStackIn, rainbow, packedLightIn, OverlayTexture.NO_OVERLAY, -1)
            );
            collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(TEXTURE_OVERLAY), (pose, overlay) ->
                STRIPES_MODEL.renderToBuffer(matrixStackIn, overlay, packedLightIn, OverlayTexture.NO_OVERLAY, -1)
            );
        }
    }
}
