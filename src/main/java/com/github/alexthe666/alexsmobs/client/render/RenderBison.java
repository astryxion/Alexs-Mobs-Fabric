package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelBison;
import com.github.alexthe666.alexsmobs.client.model.ModelBisonBaby;
import com.github.alexthe666.alexsmobs.entity.EntityBison;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;

public class RenderBison extends MobRenderer<EntityBison, LivingEntityRenderState, CitadelEntityModelBridge<EntityBison>> {
    private static final Identifier TEXTURE_BABY = Identifier.parse("alexsmobs:textures/entity/bison_baby.png");
    private static final Identifier TEXTURE_BABY_SNOWY = Identifier.parse("alexsmobs:textures/entity/bison_baby_snowy.png");
    private static final Identifier TEXTURE_SNOWY = Identifier.parse("alexsmobs:textures/entity/bison_snowy.png");
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/bison.png");
    private static final Identifier TEXTURE_SHEARED = Identifier.parse("alexsmobs:textures/entity/bison_sheared.png");

    private final CitadelEntityModelBridge<EntityBison> adultBridge;
    private final CitadelEntityModelBridge<EntityBison> babyBridge;

    @SuppressWarnings("unchecked")
    public RenderBison(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelBison()), 0.8F);
        this.adultBridge = (CitadelEntityModelBridge<EntityBison>) (Object) this.model;
        this.babyBridge = new CitadelEntityModelBridge<>(new ModelBisonBaby());
        this.addLayer(new LayerSnow(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        EntityBison entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityBison b ? b : null;
        if (entity != null) {
            this.model = entity.isBaby() ? this.babyBridge : this.adultBridge;
        }
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityBison entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityBison b ? b : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isBaby() ? TEXTURE_BABY : entity.isSheared() ? TEXTURE_SHEARED : TEXTURE;
    }

    static class LayerSnow extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityBison>> {

        LayerSnow(RenderBison renderer) {
            super(renderer);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int packedLightIn,
                LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityBison entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityBison b ? b : null;
            if (entity == null || !entity.isSnowy()) {
                return;
            }
            Identifier tex = entity.isBaby() ? TEXTURE_BABY_SNOWY : TEXTURE_SNOWY;
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            this.getParentModel().setupAnim(state);
            bufferIn.submitCustomGeometry(matrixStackIn, RenderTypes.entityCutout(tex), (pose, consumer) ->
                    this.getParentModel().renderCitadelToBuffer(matrixStackIn, consumer, packedLightIn, overlay, -1));
        }
    }
}
