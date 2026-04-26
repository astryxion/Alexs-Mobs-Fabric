package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelStraddler;
import com.github.alexthe666.alexsmobs.client.model.ModelStradpole;
import com.github.alexthe666.alexsmobs.entity.EntityStraddler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class RenderStraddler extends MobRenderer<EntityStraddler, LivingEntityRenderState, CitadelEntityModelBridge<EntityStraddler>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/straddler.png");
    private static final ModelStradpole STRADPOLE_MODEL = new ModelStradpole();

    public RenderStraddler(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelStraddler()), 0.6F);
        this.addLayer(new StradpoleLayer(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.scale(1.2F, 1.2F, 1.2F);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }

    static class StradpoleLayer extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityStraddler>> {

        public StradpoleLayer(RenderStraddler p_i50928_1_) {
            super(p_i50928_1_);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityStraddler straddler = AlexsMobsClientKeys.getLiving(state) instanceof EntityStraddler s ? s : null;
            if (straddler == null) {
                return;
            }
            int t = straddler.getAnimationTick();
            if (straddler.getAnimation() == EntityStraddler.ANIMATION_LAUNCH && t < 20 && t > 6) {
                matrixStackIn.pushPose();
                translateToModel(matrixStackIn);
                final float back = t <= 15 ? (t - 6) * 0.05F : 0.25F;
                matrixStackIn.translate(0F, -2.5F + back * 0.5F, 0.35F + back);
                int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
                collector.submitCustomGeometry(matrixStackIn, RenderTypes.entityTranslucent(RenderStradpole.TEXTURE), (pose, ivertexbuilder) ->
                    STRADPOLE_MODEL.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, overlay, -1)
                );
                matrixStackIn.popPose();
            }
        }

        protected void translateToModel(PoseStack matrixStack) {
            ModelStraddler model = (ModelStraddler) this.getParentModel().citadel();
            model.root.translateAndRotate(matrixStack);
            model.body.translateAndRotate(matrixStack);
        }
    }
}
