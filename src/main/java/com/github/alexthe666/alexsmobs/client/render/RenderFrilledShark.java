package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelFrilledShark;
import com.github.alexthe666.alexsmobs.entity.EntityFrilledShark;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderFrilledShark extends MobRenderer<EntityFrilledShark, LivingEntityRenderState, CitadelEntityModelBridge<EntityFrilledShark>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/frilled_shark.png");
    private static final Identifier TEXTURE_DEPRESSURIZED = Identifier.parse("alexsmobs:textures/entity/frilled_shark_depressurized.png");
    private static final Identifier TEXTURE_KAIJU = Identifier.parse("alexsmobs:textures/entity/frilled_shark_kaiju.png");
    private static final Identifier TEXTURE_KAIJU_DEPRESSURIZED = Identifier.parse("alexsmobs:textures/entity/frilled_shark_kaiju_depressurized.png");
    private static final Identifier TEXTURE_TEETH = Identifier.parse("alexsmobs:textures/entity/frilled_shark_teeth.png");

    public RenderFrilledShark(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelFrilledShark()), 0.4F);
        this.addLayer(new TeethLayer(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.scale(0.85F, 0.85F, 0.85F);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityFrilledShark entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityFrilledShark e ? e : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isKaiju() ? (entity.isDepressurized() ? TEXTURE_KAIJU_DEPRESSURIZED : TEXTURE_KAIJU) : (entity.isDepressurized() ? TEXTURE_DEPRESSURIZED : TEXTURE);
    }

    static class TeethLayer extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityFrilledShark>> {

        TeethLayer(RenderFrilledShark render) {
            super(render);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityFrilledShark entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityFrilledShark e ? e : null;
            if (entity == null) {
                return;
            }
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            this.getParentModel().setupAnim(state);
            collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.getEyesFlickering(TEXTURE_TEETH, 240), (pose, consumer) ->
                    this.getParentModel().renderCitadelToBuffer(matrixStackIn, consumer, 240, overlay, -1));
        }
    }
}
