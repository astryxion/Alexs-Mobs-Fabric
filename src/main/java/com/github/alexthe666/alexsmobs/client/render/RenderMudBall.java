package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.entity.EntityMudBall;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;

public class RenderMudBall extends EntityRenderer<EntityMudBall, EntityRenderState> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/mud_ball.png");

    public RenderMudBall(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        poseStack.pushPose();
        poseStack.scale(0.7F, 0.7F, 0.7F);
        poseStack.mulPose(cameraState.orientation);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        PoseStack.Pose stackPose = poseStack.last();
        Matrix4f poseMat = stackPose.pose();
        int packedLight = state.lightCoords;
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TEXTURE), (poseInner, consumer) -> {
            vertex(consumer, poseMat, packedLight, 0.0F, 0, 0, 1);
            vertex(consumer, poseMat, packedLight, 1.0F, 0, 1, 1);
            vertex(consumer, poseMat, packedLight, 1.0F, 1, 1, 0);
            vertex(consumer, poseMat, packedLight, 0.0F, 1, 0, 0);
        });
        poseStack.popPose();
        super.submit(state, poseStack, collector, cameraState);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, int packedLight, float xOffset, int y, int u, int v) {
        consumer.addVertex(pose, xOffset - 0.5F, (float) y - 0.25F, 0.0F)
            .setColor(255, 255, 255, 255)
            .setUv((float) u, (float) v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(packedLight)
            .setNormal(0.0F, 1.0F, 0.0F);
    }
}
