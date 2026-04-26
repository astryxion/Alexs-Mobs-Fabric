package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelGrizzlyBear;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerGrizzlyHoney;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerGrizzlyItem;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderGrizzlyBear extends MobRenderer<EntityGrizzlyBear, LivingEntityRenderState, CitadelEntityModelBridge<EntityGrizzlyBear>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/grizzly_bear.png");
    private static final Identifier TEXTURE_SNOWY = Identifier.parse("alexsmobs:textures/entity/grizzly_bear_snowy.png");
    public static final Identifier TEXTURE_FREDDY = Identifier.parse("alexsmobs:textures/entity/grizzly_bear_freddy.png");
    private static final Identifier TEXTURE_FREDDY_EYES = Identifier.parse("alexsmobs:textures/entity/grizzly_bear_freddy_eyes.png");

    public RenderGrizzlyBear(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelGrizzlyBear()), 0.8F);
        this.addLayer(new LayerFreddyEyes(this));
        this.addLayer(new LayerGrizzlyHoney(this));
        this.addLayer(new LayerSnow(this));
        this.addLayer(new LayerGrizzlyItem(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
    }

    @Override
    public boolean shouldRender(EntityGrizzlyBear livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        if (livingEntityIn.getAprilFoolsFlag() == 5) {
            return false;
        }
        return super.shouldRender(livingEntityIn, camera, camX, camY, camZ);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityGrizzlyBear entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityGrizzlyBear b ? b : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isFreddy() ? TEXTURE_FREDDY : TEXTURE;
    }

    static class LayerSnow extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityGrizzlyBear>> {

        LayerSnow(RenderGrizzlyBear renderer) {
            super(renderer);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityGrizzlyBear entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityGrizzlyBear b ? b : null;
            if (entity == null || !entity.isSnowy()) {
                return;
            }
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            this.getParentModel().setupAnim(state);
            bufferIn.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(TEXTURE_SNOWY), (pose, consumer) ->
                    this.getParentModel().renderCitadelToBuffer(matrixStackIn, consumer, packedLightIn, overlay, -1));
        }
    }

    static class LayerFreddyEyes extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityGrizzlyBear>> {

        LayerFreddyEyes(RenderGrizzlyBear renderer) {
            super(renderer);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityGrizzlyBear entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityGrizzlyBear b ? b : null;
            if (entity == null || entity.getAprilFoolsFlag() != 4 || entity.tickCount % 6 > 2) {
                return;
            }
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            int packedColor = AMColorUtil.packColor(1.0F, 1.0F, 1.0F, 0.1F);
            this.getParentModel().setupAnim(state);
            bufferIn.submitCustomGeometry(matrixStackIn, AMRenderTypes.getEyesNoFog(TEXTURE_FREDDY_EYES), (pose, consumer) ->
                    this.getParentModel().renderCitadelToBuffer(matrixStackIn, consumer, packedLightIn, overlay, packedColor));
        }
    }
}
