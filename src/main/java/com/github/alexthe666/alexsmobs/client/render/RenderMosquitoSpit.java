package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.entity.EntityMosquitoSpit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.animal.llama.LlamaSpitModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LlamaSpitRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class RenderMosquitoSpit extends EntityRenderer<EntityMosquitoSpit, LlamaSpitRenderState> {
    private static final Identifier SPIT_TEXTURE = Identifier.parse("alexsmobs:textures/entity/mosquito_spit.png");
    /** Llama spit model reads {@link PoseStack#last()}; must match queued submit pose when geometry replays. */
    private final PoseStack citadelPoseScratch = new PoseStack();
    private final LlamaSpitModel model;

    public RenderMosquitoSpit(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
        this.model = new LlamaSpitModel(renderManagerIn.bakeLayer(ModelLayers.LLAMA_SPIT));
    }

    @Override
    public LlamaSpitRenderState createRenderState() {
        return new LlamaSpitRenderState();
    }

    @Override
    public void extractRenderState(EntityMosquitoSpit entity, LlamaSpitRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        state.xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
    }

    @Override
    public void submit(LlamaSpitRenderState state, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState cameraState) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0D, (double) 0.15F, 0.0D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(state.xRot));
        this.model.setupAnim(state);
        int packedLightIn = state.lightCoords;
        collector.submitCustomGeometry(matrixStackIn, this.model.renderType(SPIT_TEXTURE), (pose, ivertexbuilder) ->
            AlexAdvancedEntityModel.withCitadelSubmitPose(pose, this.citadelPoseScratch, s ->
                this.model.renderToBuffer(s, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1))
        );
        matrixStackIn.popPose();
        super.submit(state, matrixStackIn, collector, cameraState);
    }

    public Identifier getTextureLocation(LlamaSpitRenderState state) {
        return SPIT_TEXTURE;
    }
}
