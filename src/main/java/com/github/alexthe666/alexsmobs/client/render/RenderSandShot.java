package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.entity.EntityGuster;
import com.github.alexthe666.alexsmobs.entity.EntitySandShot;
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

public class RenderSandShot extends EntityRenderer<EntitySandShot, LlamaSpitRenderState> {
    private static final Identifier SAND_SHOT = Identifier.parse("alexsmobs:textures/entity/sand_shot.png");
    private final LlamaSpitModel model;

    public RenderSandShot(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
        this.model = new LlamaSpitModel(renderManagerIn.bakeLayer(ModelLayers.LLAMA_SPIT));
    }

    @Override
    public LlamaSpitRenderState createRenderState() {
        return new LlamaSpitRenderState();
    }

    @Override
    public void extractRenderState(EntitySandShot entity, LlamaSpitRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        state.xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
    }

    @Override
    public void submit(LlamaSpitRenderState state, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (!(AlexsMobsClientKeys.getEntity(state) instanceof EntitySandShot entityIn)) {
            return;
        }
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0D, (double) 0.15F, 0.0D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(state.xRot));
        matrixStackIn.scale(1.2F, 1.2F, 1.2F);
        int i = EntityGuster.getColorForVariant(entityIn.getVariant());
        float r = (float) (i >> 16 & 255) / 255.0F;
        float g = (float) (i >> 8 & 255) / 255.0F;
        float b = (float) (i & 255) / 255.0F;
        this.model.setupAnim(state);
        int packedLightIn = state.lightCoords;
        collector.submitCustomGeometry(matrixStackIn, this.model.renderType(SAND_SHOT), (pose, ivertexbuilder) ->
            this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1)
        );
        matrixStackIn.popPose();
        super.submit(state, matrixStackIn, collector, cameraState);
    }

    public Identifier getTextureLocation(LlamaSpitRenderState state) {
        return SAND_SHOT;
    }
}
