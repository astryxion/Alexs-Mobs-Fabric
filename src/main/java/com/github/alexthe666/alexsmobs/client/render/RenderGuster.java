package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelGuster;
import com.github.alexthe666.alexsmobs.entity.EntityGuster;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class RenderGuster extends MobRenderer<EntityGuster, LivingEntityRenderState, CitadelEntityModelBridge<EntityGuster>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/guster.png");
    private static final Identifier TEXTURE_GOOGLY = Identifier.parse("alexsmobs:textures/entity/guster_silly.png");
    private static final Identifier TEXTURE_EYES = Identifier.parse("alexsmobs:textures/entity/guster_eye.png");
    private static final Identifier TEXTURE_RED = Identifier.parse("alexsmobs:textures/entity/guster_red.png");
    private static final Identifier TEXTURE_SOUL = Identifier.parse("alexsmobs:textures/entity/guster_soul.png");
    private static final Identifier TEXTURE_SOUL_EYES = Identifier.parse("alexsmobs:textures/entity/guster_eye_soul.png");

    public RenderGuster(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelGuster()), 0.25F);
        this.addLayer(new GusterEyesLayer(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Nullable
    @Override
    protected RenderType getRenderType(LivingEntityRenderState state, boolean normal, boolean invis, boolean outline) {
        Identifier tex = this.getTextureLocation(state);
        if (invis) {
            return RenderTypes.entityTranslucent(tex);
        } else if (normal) {
            return RenderTypes.entityTranslucent(tex);
        } else {
            return outline ? RenderTypes.outline(tex) : null;
        }
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityGuster entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityGuster g ? g : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isGooglyEyes() ? TEXTURE_GOOGLY : entity.getVariant() == 2 ? TEXTURE_SOUL : entity.getVariant() == 1 ? TEXTURE_RED : TEXTURE;
    }

    static class GusterEyesLayer extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityGuster>> {

        public GusterEyesLayer(RenderGuster p_i50928_1_) {
            super(p_i50928_1_);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityGuster entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityGuster g ? g : null;
            if (entitylivingbaseIn == null || entitylivingbaseIn.isGooglyEyes()) {
                return;
            }
            this.getParentModel().setupAnim(state);
            RenderType eyeType = entitylivingbaseIn.getVariant() == 2 ? AMRenderTypes.getEyesNoCull(TEXTURE_SOUL_EYES) : AMRenderTypes.getEyesNoCull(TEXTURE_EYES);
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            collector.submitCustomGeometry(matrixStackIn, eyeType, (pose, ivertexbuilder) ->
                this.getParentModel().renderCitadelToBuffer(matrixStackIn, ivertexbuilder, 15728640, overlay, -1)
            );
        }
    }
}
