package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelSkreecher;
import com.github.alexthe666.alexsmobs.entity.EntitySkreecher;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderSkreecher extends MobRenderer<EntitySkreecher, LivingEntityRenderState, CitadelEntityModelBridge<EntitySkreecher>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/skreecher.png");
    private static final Identifier TEXTURE_GLOW = Identifier.parse("alexsmobs:textures/entity/skreecher_glow.png");

    public RenderSkreecher(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelSkreecher()), 0.35F);
        this.addLayer(new LayerScorch(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }

    static class LayerScorch extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntitySkreecher>> {

        public LayerScorch(RenderSkreecher render) {
            super(render);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntitySkreecher entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntitySkreecher s ? s : null;
            if (entitylivingbaseIn == null) {
                return;
            }
            float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
            float alpha = (float) Math.sin((entitylivingbaseIn.tickCount + partialTicks) * 0.1F) * 0.35F + 0.5F;
            this.getParentModel().setupAnim(state);
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0);
            int packedColor = AMColorUtil.packColor(1.0F, 1.0F, 1.0F, alpha);
            PoseStack citadelPoseStack = new PoseStack();
            collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.getEyesAlphaEnabled(TEXTURE_GLOW), (pose, scorch) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> this.getParentModel().renderCitadelToBuffer(scratch, scorch, 240, overlay, packedColor))
            );
        }
    }
}
