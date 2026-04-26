package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelPollenBall;
import com.github.alexthe666.alexsmobs.entity.EntityPollenBall;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
public class RenderPollenBall extends EntityRenderer<EntityPollenBall, EntityRenderState> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/pollen_ball.png");
    private static final ModelPollenBall MODEL_POLLEN_BALL = new ModelPollenBall();

    public RenderPollenBall(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    public Identifier getTextureLocation(EntityRenderState state) {
        return TEXTURE;
    }

    @Override
    public void submit(EntityRenderState state, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (!(AlexsMobsClientKeys.getEntity(state) instanceof EntityPollenBall entityIn)) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        matrixStackIn.pushPose();
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0D, (double) -0.25F, 0.0D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 180F));
        matrixStackIn.pushPose();
        matrixStackIn.translate(0, 0.5F, 0);
        matrixStackIn.scale(1F, 1F, 1F);
        int packedLightIn = state.lightCoords;
        collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.getFullBright(getTextureLocation(state)), (pose, ivertexbuilder) ->
            MODEL_POLLEN_BALL.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1)
        );
        matrixStackIn.popPose();
        matrixStackIn.popPose();
        matrixStackIn.popPose();
        super.submit(state, matrixStackIn, collector, cameraState);
    }
}
