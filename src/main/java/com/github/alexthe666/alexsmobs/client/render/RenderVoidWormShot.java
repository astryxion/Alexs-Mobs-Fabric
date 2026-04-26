package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelVoidWormShot;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWormShot;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class RenderVoidWormShot extends EntityRenderer<EntityVoidWormShot, EntityRenderState> {

    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/void_worm/void_worm_shot.png");
    private static final ModelVoidWormShot MODEL = new ModelVoidWormShot();

    public RenderVoidWormShot(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    public Identifier getTextureLocation(EntityRenderState state) {
        return TEXTURE;
    }

    @SuppressWarnings("unused")
    @Override
    public void submit(EntityRenderState state, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (!(AlexsMobsClientKeys.getEntity(state) instanceof EntityVoidWormShot entityIn)) {
            super.submit(state, matrixStackIn, collector, cameraState);
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);

        matrixStackIn.pushPose();
        matrixStackIn.mulPose((new Quaternionf()).rotateX(Maths.rad(180)));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        matrixStackIn.pushPose();
        MODEL.animate(entityIn, entityIn.tickCount + partialTicks);
        float home = (entityIn.prevStopHomingProgress + (entityIn.getStopHomingProgress() - entityIn.prevStopHomingProgress) * partialTicks) / EntityVoidWormShot.HOME_FOR;
        float colorize = home;
        matrixStackIn.translate(0, -1.5F, 0);
        collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.getFullBright(TEXTURE), (pose, ivertexbuilder) ->
            MODEL.renderToBuffer(matrixStackIn, ivertexbuilder, 210, NO_OVERLAY, -1)
        );
        matrixStackIn.popPose();
        matrixStackIn.popPose();

        super.submit(state, matrixStackIn, collector, cameraState);
    }
}
